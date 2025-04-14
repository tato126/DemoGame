console.log("main.js 로드됨 - 3단계: 웹소켓 통신");

const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// --- 게임 상태 변수 (서버로부터 받을 정보) ---
let player = { id: null, x: 50, y: 50, size: 20, color: 'red' }; // 플레이어 상태 객체

// --- 웹소켓 관련 변수 ---
let ws = null;
const wsUrl = `ws://${window.location.host}/game-ws`; // 현재 호스트 기반 웹소켓 주소

// --- 그리기 함수 ---
function drawPlayer() {
    if (!ctx) {
        console.error("2D 컨텍스트가 없습니다.");
        return;
    }
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = player.color;
    ctx.fillRect(player.x, player.y, player.size, player.size);
    // console.log(`플레이어 위치 (서버): (${player.x}, ${player.y})`);
}

// --- 웹소켓 연결 함수 ---
function connectWebSocket() {
    console.log("웹소켓 연결 시도:", wsUrl);
    ws = new WebSocket(wsUrl);

    ws.onopen = () => {
        console.log("웹소켓 연결 성공!");
        // 연결 성공 시 필요한 초기 작업 (예: 서버에 플레이어 정보 요청)
    };

    ws.onmessage = (event) => {
        // 서버로부터 메시지 수신
        // console.log("서버로부터 메시지 수신:", event.data);
        try {
            const message = JSON.parse(event.data);
            if (message.type === 'gameStateUpdate') {
                // GameState 업데이트 메시지 처리
                const serverPlayer = message.player;
                if (serverPlayer) {
                     // 현재 플레이어 ID와 비교 (싱글플레이어에서는 큰 의미 없으나 멀티 대비)
                    if(player.id === null) { // 처음 상태 받을 때 ID 설정
                       player.id = serverPlayer.id;
                    }
                    player.x = serverPlayer.x;
                    player.y = serverPlayer.y;
                    player.size = serverPlayer.size; // 필요시 서버에서 size도 받음
                    // 플레이어 다시 그리기
                    drawPlayer();
                }
                // 다른 게임 요소(적 등) 상태 업데이트 로직 추가 가능
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
        // 필요시 재연결 로직 추가
         setTimeout(connectWebSocket, 5000); // 5초 후 재연결 시도
    };
}

// --- 키보드 입력 처리 함수 ---
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
        // 이동 메시지를 서버로 전송
        const moveMessage = {
            type: "move",
            playerId: player.id, // GameState에서 관리하는 플레이어 ID
            direction: direction
        };
        ws.send(JSON.stringify(moveMessage));
        // console.log("서버로 이동 메시지 전송:", moveMessage);
        // 로컬에서 바로 그리지 않고 서버 응답을 기다림
    }
}

// --- 초기화 코드 ---
if (canvas && ctx) {
    // 초기 화면 그리기 (서버 연결 전 기본 위치)
    drawPlayer();

    // 웹소켓 연결 시작
    connectWebSocket();

    // 키보드 이벤트 리스너 추가
    document.addEventListener('keydown', handleKeyDown);
    console.log("키보드 이벤트 리스너 추가 완료.");

} else {
    console.error("캔버스 또는 컨텍스트를 찾을 수 없어 초기화 실패.");
}