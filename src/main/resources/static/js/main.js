console.log("main.js 로드됨 - 4단계: 단일 Enemy 그리기"); // 로그 메시지 업데이트

const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// --- 게임 상태 변수 (서버로부터 받을 정보) ---
let player = { id: null, x: 50, y: 50, size: 20, color: 'red' };
let currentEnemy = null; // 단일 Enemy 상태 저장 변수 (초기값 null)

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
                // Player 상태 업데이트
                const serverPlayer = message.player;
                if (serverPlayer) {
                    if(player.id === null) { player.id = serverPlayer.id; }
                    player.x = serverPlayer.x;
                    player.y = serverPlayer.y;
                    player.size = serverPlayer.size;
                }

                // --- Enemy 상태 업데이트 (단일 객체) ---
                const serverEnemy = message.enemy; // 'enemies' 리스트 대신 'enemy' 객체 사용
                if (serverEnemy) {
                    // currentEnemy 변수에 받은 정보 업데이트
                    currentEnemy = {
                        id: serverEnemy.id,
                        x: serverEnemy.x,
                        y: serverEnemy.y,
                        size: serverEnemy.size,
                        color: 'blue' // Enemy 색상 지정 (예시)
                    };
                } else {
                     // 서버에서 enemy 정보가 null로 오면 클라이언트 상태도 null로 설정
                     currentEnemy = null;
                }
                // --- Enemy 상태 업데이트 끝 ---

                // Player와 Enemy 상태를 모두 반영하여 캔버스 다시 그리기
                drawGame(); // drawPlayer 대신 drawGame 호출

            } else if (message.type === 'error') {
                console.error("Server error:", message.message);
            }
        } catch (e) {
            console.error("메시지 처리 오류:", e);
        }
    };

    ws.onerror = (error) => {
        console.error("웹소켓 오류:", error);
    };

    ws.onclose = (event) => {
        console.log("웹소켓 연결 종료:", event.code, event.reason);
        ws = null;
         setTimeout(connectWebSocket, 5000); // 5초 후 재연결 시도
    };
}

// --- 키보드 입력 처리 함수 (변경 없음) ---
function handleKeyDown(e) {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        console.warn("웹소켓이 연결되지 않았습니다.");
        return;
    }
    if (!player.id) {
        console.warn("플레이어 ID가 설정되지 않았습니다.");
        return;
    }

    let direction = null;
    switch (e.key) {
        case "ArrowUp":    direction = "UP"; break;
        case "ArrowDown":  direction = "DOWN"; break;
        case "ArrowLeft":  direction = "LEFT"; break;
        case "ArrowRight": direction = "RIGHT"; break;
    }

    if (direction) {
        const moveMessage = {
            type: "move",
            playerId: player.id,
            direction: direction
        };
        ws.send(JSON.stringify(moveMessage));
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

    // 2. 플레이어 그리기
    if (player && player.id) { // player 객체 및 id 존재 확인
        ctx.fillStyle = player.color;
        ctx.fillRect(player.x, player.y, player.size, player.size);
    }

    // 3. 적(Enemy) 그리기
    if (currentEnemy) { // currentEnemy 객체가 null이 아닐 때만 그림
         ctx.fillStyle = currentEnemy.color || 'blue'; // Enemy 색상 사용 (없으면 기본값)
         ctx.fillRect(currentEnemy.x, currentEnemy.y, currentEnemy.size, currentEnemy.size);
    }
}

// --- 초기화 코드 ---
if (canvas && ctx) {
    // 초기 화면 그리기 (Player와 Enemy 모두 그리는 함수 호출)
    drawGame(); // drawPlayer 대신 drawGame 호출

    // 웹소켓 연결 시작
    connectWebSocket();

    // 키보드 이벤트 리스너 추가
    document.addEventListener('keydown', handleKeyDown);
    console.log("키보드 이벤트 리스너 추가 완료.");

} else {
    console.error("캔버스 또는 컨텍스트를 찾을 수 없어 초기화 실패.");
}