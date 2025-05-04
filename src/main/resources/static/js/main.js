// Demo-main/src/main/resources/static/js/main.js

console.log("main.js 로드됨 - 수정된 버전");

const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// --- 게임 상태 변수 (클라이언트 측 상태) ---
let player = { id: null, x: 0, y: 0, size: 0, color: 'red' }; // 클라이언트 자신의 플레이어 정보, 초기 위치는 서버에서 받음
let enemies = [];       // 서버로부터 받을 Enemy 목록 배열
let projectiles = [];   // 서버로부터 받을 Projectile 목록 배열

// --- 웹소켓 관련 변수 ---
let ws = null;
// 현재 페이지의 호스트 주소를 사용하여 웹소켓 URL 동적 생성
const wsUrl = `ws://${window.location.host}/game-ws`; // 서버 설정과 동일한 엔드포인트 사용

// --- 웹소켓 연결 함수 ---
function connectWebSocket() {
    console.log("웹소켓 연결 시도:", wsUrl);
    ws = new WebSocket(wsUrl); //

    ws.onopen = () => {
        console.log("웹소켓 연결 성공!"); //
        // 연결 성공 시 별도 메시지 전송은 서버 로직에 따라 결정 (현재 서버는 연결 시 바로 플레이어 생성 및 상태 전송)
    };

    ws.onmessage = (event) => {
        console.log("서버로부터 메시지 수신:", event.data); //
        try {
            const message = JSON.parse(event.data); //

            if (message.type === 'gameStateUpdate') { //
                // --- Player 상태 업데이트 ---
                const serverPlayers = message.players || []; // players 배열 받기, 없으면 빈 배열
                console.log("받은 플레이어 목록:", serverPlayers);

                // 서버에서 플레이어 목록을 받으면, 클라이언트의 player 객체 업데이트
                if (player.id === null && serverPlayers.length > 0) {
                    // 처음 ID를 받는 경우 (혹은 재연결 등) 첫 번째 플레이어를 내 플레이어로 가정 (서버 로직 확인 필요)
                    // 서버 GameService initializePlayer 및 WebSocketHandler afterConnectionEstablished 로직상
                    // 연결 시 새 플레이어 ID가 부여되고 해당 ID가 포함된 상태가 전송됨
                    // find를 통해 내 ID를 찾는 것이 더 안전함
                     const myPlayerData = serverPlayers[0]; // 단순 첫번째 할당보다는 find가 나을 수 있음. 서버가 보장한다면 OK.
                     if(myPlayerData){
                        player.id = myPlayerData.id;
                        console.log("초기 클라이언트 플레이어 ID 설정:", player.id);
                     }
                }

                // 내 플레이어 ID가 설정되었다면, 서버 목록에서 내 정보 찾아 업데이트
                if (player.id !== null) {
                    const myPlayerData = serverPlayers.find(p => p.id === player.id); // === 연산자로 수정
                    if (myPlayerData) {
                        console.log("내 플레이어 데이터 발견, 상태 업데이트:", myPlayerData);
                        player.x = myPlayerData.x; // DTO 필드명과 일치
                        player.y = myPlayerData.y; //
                        player.size = myPlayerData.size; //
                    } else {
                        // 서버 목록에 내 ID가 없는 경우 (ex: 게임 오버, 연결 종료 등)
                        console.warn("서버 목록에 내 플레이어 ID가 없습니다! 플레이어 상태 초기화. ID:", player.id);
                        player.id = null; // ID 초기화하여 그리지 않도록 함
                        // 필요하다면 게임 오버 처리 등 추가 로직
                    }
                }
                // --- Player 상태 업데이트 끝 ---

                // --- Enemy 상태 업데이트 (목록 전체 업데이트) ---
                const serverEnemies = message.enemies || []; // 없으면 빈 배열
                enemies = serverEnemies.map(e => ({ ...e, color: 'blue' })); // DTO 구조에 맞게 업데이트
                // --- Enemy 상태 업데이트 끝 ---

                // --- Projectile 상태 업데이트 (목록 전체 업데이트) ---
               const serverProjectiles = message.projectiles || []; // 없으면 빈 배열
               projectiles = serverProjectiles.map(p => ({ ...p, color: 'orange' })); // DTO 구조에 맞게 업데이트
                // --- Projectile 상태 업데이트 끝 ---

                // 변경된 상태로 캔버스 다시 그리기
                drawGame(); //

            } else if (message.type === 'error') { // 서버에서 보낸 에러 메시지 처리
                console.error("Server error:", message.message);
                // 사용자에게 오류 알림 등 추가 처리 가능
            } else {
                 console.log("처리되지 않은 메시지 타입:", message.type); //
            }
        } catch (e) {
            console.error("메시지 처리 오류:", e, "원본 데이터:", event.data); //
        }
    };

    ws.onerror = (error) => {
        console.error("웹소켓 오류:", error); //
    };

    ws.onclose = (event) => {
        console.log("웹소켓 연결 종료:", event.code, event.reason); //
        ws = null;
        player.id = null; // 플레이어 ID 초기화
        enemies = []; // 다른 객체들도 초기화
        projectiles = [];
        drawGame(); // 화면 클리어
        // 필요시 재연결 로직 추가
        // setTimeout(connectWebSocket, 5000);
    };
}

// --- 키보드 입력 처리 함수 ---
function handleKeyDown(e) {
    if (!ws || ws.readyState !== WebSocket.OPEN) { // 웹소켓 연결 상태 확인
        console.warn("웹소켓이 연결되지 않았습니다.");
        return;
    }
    if (!player.id) { // 플레이어 ID가 있어야 서버와 통신 가능
        console.warn("플레이어 ID가 설정되지 않아 메시지를 보낼 수 없습니다.");
        return;
    }

    let direction = null;
    let actionType = null; // 'move' 또는 'shot' 구분

    // 방향키 또는 스페이스바 입력 감지
    switch (e.key) { //
        case "ArrowUp":    direction = "UP"; actionType = "move"; break;
        case "ArrowDown":  direction = "DOWN"; actionType = "move"; break;
        case "ArrowLeft":  direction = "LEFT"; actionType = "move"; break;
        case "ArrowRight": direction = "RIGHT"; actionType = "move"; break;
        case " ": // 스페이스바
        case "Spacebar":
            actionType = "shot";
            // ★★★ 발사 방향 결정 로직 수정 필요 ★★★
            // 예시: 현재는 임시로 UP 방향 고정. 실제 게임에서는 플레이어 방향 등을 사용해야 함.
            direction = "UP"; // 실제 게임 로직에 맞게 수정 (예: 플레이어가 마지막으로 이동한 방향)
            break;
    }

    // 메시지 전송
    if (actionType === "move" && direction) {
        const moveMessage = {
            type: "move",       // MoveMessage 타입
            playerId: player.id, // 현재 플레이어 ID 포함
            direction: direction // 방향 포함
        };
        ws.send(JSON.stringify(moveMessage)); //
        console.log("이동 메시지 전송:", moveMessage);
    } else if (actionType === "shot" && direction) { // direction도 확인 (발사 방향이 결정되었는지)
        const shotMessage = {
            type: "shot",       // ShotMessage 타입
            playerId: player.id, // 현재 플레이어 ID 포함
            direction: direction // 결정된 발사 방향 사용
        };
        ws.send(JSON.stringify(shotMessage)); //
        console.log("발사 메시지 전송:", shotMessage); // 로그 메시지 명확화
    }
}

// --- 통합 그리기 함수 ---
function drawGame() {
    if (!ctx) { // 컨텍스트 확인
        console.error("2D 컨텍스트가 없습니다.");
        return;
    }
    // 1. 캔버스 클리어
    ctx.clearRect(0, 0, canvas.width, canvas.height); //

    // 2. 플레이어 그리기 (자신의 플레이어)
    // player.id 가 null이 아닐 때만 그림 (서버에서 제거되었거나 아직 ID를 못 받은 경우 방지)
    if (player.id) { // ID 존재 여부 확인 강화
        ctx.fillStyle = player.color || 'red'; // 기본 색상
        ctx.fillRect(player.x, player.y, player.size, player.size); // DTO 기반 위치/크기 사용
    }

    // 3. 적(Enemy) 목록 그리기
    enemies.forEach(enemy => { //
        if (enemy) { // enemy 객체가 유효한지 확인 (필수는 아님)
             ctx.fillStyle = enemy.color || 'blue'; // 기본 색상
             ctx.fillRect(enemy.x, enemy.y, enemy.size, enemy.size); // DTO 기반 위치/크기 사용
        }
    });

    // 4. 투사체(Projectile) 목록 그리기
    projectiles.forEach(projectile => { //
        if (projectile) { // projectile 객체가 유효한지 확인
            ctx.fillStyle = projectile.color || 'orange'; // 기본 색상
            ctx.fillRect(projectile.x, projectile.y, projectile.size, projectile.size); // DTO 기반 위치/크기 사용
        }
    });
}

// --- 초기화 코드 ---
if (canvas && ctx) { // 캔버스와 컨텍스트 유효성 검사
    drawGame(); // 초기 화면 그리기 (아무것도 없는 상태)
    connectWebSocket(); // 웹소켓 연결 시작
    document.addEventListener('keydown', handleKeyDown); // 키보드 리스너 추가
    console.log("초기화 완료 및 키보드 이벤트 리스너 추가 완료.");
} else {
    console.error("캔버스 또는 컨텍스트를 찾을 수 없어 초기화 실패."); //
}