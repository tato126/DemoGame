// 로그 메시지 업데이트 (옵션)
console.log("main.js 로드됨 - 다중 객체 상태 처리");

const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// --- 게임 상태 변수 (클라이언트 측 상태) ---
let player = { id: null, x: 50, y: 50, size: 20, color: 'red' }; // 클라이언트 자신의 플레이어 정보
let enemies = [];       // ★★★ 서버로부터 받을 Enemy 목록 배열 ★★★
let projectiles = [];   // ★★★ 서버로부터 받을 Projectile 목록 배열 ★★★

// --- 웹소켓 관련 변수 ---
let ws = null;
const wsUrl = `ws://${window.location.host}/game-ws`;

// --- 웹소켓 연결 함수 ---
function connectWebSocket() {
    console.log("웹소켓 연결 시도:", wsUrl);
    ws = new WebSocket(wsUrl);

    ws.onopen = () => {
        console.log("웹소켓 연결 성공!");
    };

    ws.onmessage = (event) => {
        // console.log("서버로부터 메시지 수신:", event.data);
        try {
            const message = JSON.parse(event.data);

            if (message.type === 'gameStateUpdate') {
                // --- Player 상태 업데이트 (자신의 정보만 업데이트) ---
                const serverPlayers = message.players; // 'players' 배열 받기
                if (player.id === null && serverPlayers && serverPlayers.length > 0 && serverPlayers[0]) {
                    player.id = serverPlayers[0].id; // 첫 메시지에서 ID 설정 (임시 방식)
                    console.log("클라이언트 플레이어 ID 설정:", player.id);
                }
                if (player.id && serverPlayers && Array.isArray(serverPlayers)) {
                    const myPlayerData = serverPlayers.find(p => p.id === player.id);
                    if (myPlayerData) {
                        player.x = myPlayerData.x;
                        player.y = myPlayerData.y;
                        player.size = myPlayerData.size;
                        // player.color = myPlayerData.color; // 필요시 업데이트
                    } else {
                        console.warn("자신의 플레이어 데이터를 서버 목록에서 찾을 수 없습니다. ID:", player.id);
                    }
                }
                // --- Player 상태 업데이트 끝 ---

                // --- Enemy 상태 업데이트 (목록 전체 업데이트) ---
                const serverEnemies = message.enemies; // 'enemies' 배열 받기
                if (serverEnemies && Array.isArray(serverEnemies)) {
                    enemies = serverEnemies.map(e => ({ ...e, color: 'blue' })); // 배열 전체를 교체 (색상 등 추가 정보 포함 가능)
                } else {
                    enemies = []; // 서버에서 빈 배열 또는 null 오면 클라이언트도 비움
                }
                // --- Enemy 상태 업데이트 끝 ---

                // --- Projectile 상태 업데이트 (목록 전체 업데이트) ---
               const serverProjectiles = message.projectiles; // 'projectiles' 배열 받기
               if (serverProjectiles && Array.isArray(serverProjectiles)) {
                    projectiles = serverProjectiles.map(p => ({ ...p, color: 'orange' })); // 배열 전체 교체
               } else {
                    projectiles = []; // 서버에서 빈 배열 또는 null 오면 클라이언트도 비움
               }
                // --- Projectile 상태 업데이트 끝 ---

                // 변경된 상태로 캔버스 다시 그리기
                drawGame();

            } else if (message.type === 'error') {
                console.error("Server error:", message.message);
            } else {
                 console.log("처리되지 않은 메시지 타입:", message.type);
            }
        } catch (e) {
            console.error("메시지 처리 오류:", e, "원본 데이터:", event.data);
        }
    };

    ws.onerror = (error) => {
        console.error("웹소켓 오류:", error);
    };

    ws.onclose = (event) => {
        console.log("웹소켓 연결 종료:", event.code, event.reason);
        ws = null;
        // setTimeout(connectWebSocket, 5000); // 필요시 재연결 로직
    };
}

// --- 키보드 입력 처리 함수 ---
function handleKeyDown(e) {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        console.warn("웹소켓이 연결되지 않았습니다.");
        return;
    }
    if (!player.id) {
        console.warn("플레이어 ID가 설정되지 않아 메시지를 보낼 수 없습니다.");
        return;
    }

    let direction = null;
    let actionType = null; // 'move' 또는 'shot' 구분

    // 방향키 또는 스페이스바 입력 감지
    switch (e.key) {
        case "ArrowUp":    direction = "UP"; actionType = "move"; break;
        case "ArrowDown":  direction = "DOWN"; actionType = "move"; break;
        case "ArrowLeft":  direction = "LEFT"; actionType = "move"; break;
        case "ArrowRight": direction = "RIGHT"; actionType = "move"; break;
        case " ": // 스페이스바 확인 (e.code === 'Space' 가 더 정확할 수 있음)
        case "Spacebar":   actionType = "shot"; direction = "UP"; /* ★★★ 발사 방향 결정 필요 ★★★ */ break;
    }

    // 메시지 전송
    if (actionType === "move" && direction) {
        const moveMessage = {
            type: "move",
            playerId: player.id,
            direction: direction
        };
        ws.send(JSON.stringify(moveMessage));
    } else if (actionType === "shot") {
        // ★★★ 발사 방향 결정 로직 필요 ★★★
        // 예시: 현재는 임시로 UP 방향으로 설정. 실제로는 플레이어가 바라보는 방향 등으로 설정해야 함.
        let fireDirection = direction || "UP"; // 기본값 UP (수정 필요!)

        const shotMessage = {
            type: "shot",
            playerId: player.id,
            direction: fireDirection // 결정된 발사 방향 사용
        };
        // ★★★ shotMessage 전송으로 수정 ★★★
        ws.send(JSON.stringify(shotMessage));
        console.log("발사 메시지 전송! 방향:", fireDirection);
    }
}

// --- 통합 그리기 함수 ---
function drawGame() {
    if (!ctx) {
        console.error("2D 컨텍스트가 없습니다.");
        return;
    }
    // 1. 캔버스 클리어
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // 2. 플레이어 그리기 (자신의 플레이어)
    if (player && player.id) {
        ctx.fillStyle = player.color || 'red';
        ctx.fillRect(player.x, player.y, player.size, player.size);
    }

    // 3. 적(Enemy) 목록 그리기 ★★★
    enemies.forEach(enemy => {
        if (enemy) {
             ctx.fillStyle = enemy.color || 'blue';
             ctx.fillRect(enemy.x, enemy.y, enemy.size, enemy.size);
        }
    });

    // 4. 투사체(Projectile) 목록 그리기 ★★★
    projectiles.forEach(projectile => {
        if (projectile) {
            ctx.fillStyle = projectile.color || 'orange';
            ctx.fillRect(projectile.x, projectile.y, projectile.size, projectile.size);
        }
    });
}

// --- 초기화 코드 ---
if (canvas && ctx) {
    drawGame(); // 초기 그리기
    connectWebSocket(); // 웹소켓 연결
    document.addEventListener('keydown', handleKeyDown); // 키 리스너 추가
    console.log("키보드 이벤트 리스너 추가 완료.");
} else {
    console.error("캔버스 또는 컨텍스트를 찾을 수 없어 초기화 실패.");
}