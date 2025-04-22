// main.js

// 로그 메시지 업데이트 (옵션)
console.log("main.js 로드됨 - 백엔드 상태 관리 리팩토링 반영");

const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// --- 게임 상태 변수 (클라이언트 측 상태) ---
// 플레이어 객체: 초기값 설정, ID는 서버로부터 받아옴
let player = { id: null, x: 50, y: 50, size: 20, color: 'red' };
// 현재 화면에 표시할 단일 Enemy 상태 저장 변수 (초기값 null)
let currentEnemy = null;

// --- 웹소켓 관련 변수 ---
let ws = null;
// window.location.host는 현재 페이지의 호스트명과 포트를 포함 (예: localhost:8081)
const wsUrl = `ws://${window.location.host}/game-ws`;

// --- 웹소켓 연결 함수 ---
function connectWebSocket() {
    console.log("웹소켓 연결 시도:", wsUrl);
    ws = new WebSocket(wsUrl);

    ws.onopen = () => {
        console.log("웹소켓 연결 성공!");
        // 연결 성공 시 초기 상태 요청 등 가능 (현재는 서버가 자동으로 전송)
    };

    ws.onmessage = (event) => {
        // console.log("서버로부터 메시지 수신:", event.data); // 디버깅 시 주석 해제
        try {
            const message = JSON.parse(event.data);

            if (message.type === 'gameStateUpdate') {
                // --- Player 상태 업데이트 (배열 처리) ---
                const serverPlayers = message.players; // 'players' 배열 사용
                if (serverPlayers && Array.isArray(serverPlayers)) { // 배열인지 확인
                    // 클라이언트 자신의 플레이어 ID가 설정되지 않았는지 확인
                    if (player.id === null && serverPlayers.length > 0 && serverPlayers[0]) {
                        // 처음 상태를 받을 때, 첫 번째 플레이어 ID를 자신의 ID로 설정 (임시)
                        // 서버에서 명시적으로 ID를 주는 것이 더 좋음
                        player.id = serverPlayers[0].id;
                        console.log("클라이언트 플레이어 ID 설정:", player.id);
                    }

                    // players 배열에서 자신의 ID와 일치하는 플레이어 찾기
                    const myPlayerData = serverPlayers.find(p => p.id === player.id);

                    if (myPlayerData) {
                        // 찾은 데이터로 클라이언트 player 객체 업데이트
                        player.x = myPlayerData.x;
                        player.y = myPlayerData.y;
                        player.size = myPlayerData.size;
                        // player.color = myPlayerData.color; // 필요시 color 등 속성 업데이트
                    } else if (player.id !== null) {
                        // ID가 있는데 서버 목록에 없으면 비정상 상황 처리
                        console.warn("자신의 플레이어 데이터를 서버 목록에서 찾을 수 없습니다. ID:", player.id);
                        // 예: player 객체를 초기화하거나, 연결 재시도 등
                        // player = { id: null, x: 50, y: 50, size: 20, color: 'red' };
                    }
                } else {
                     // 서버 players 데이터가 없거나 배열이 아님
                     // console.log("서버로부터 유효한 플레이어 데이터가 오지 않았습니다.");
                     // 필요시 기존 player 상태 유지 또는 초기화
                }
                // --- Player 상태 업데이트 끝 ---


                // --- Enemy 상태 업데이트 (배열 처리) ---
                const serverEnemies = message.enemies; // 'enemies' 배열 사용
                if (serverEnemies && Array.isArray(serverEnemies) && serverEnemies.length > 0) {
                    // 현재는 적이 하나만 있다고 가정하고 첫 번째 적의 정보 사용
                    const firstEnemyData = serverEnemies[0];
                    currentEnemy = {
                        id: firstEnemyData.id,
                        x: firstEnemyData.x,
                        y: firstEnemyData.y,
                        size: firstEnemyData.size,
                        color: 'blue' // 필요시 DTO에 color 포함
                    };
                } else {
                     // 서버 enemies 배열이 비어있거나 배열이 아니면 클라이언트 상태도 null로 설정
                     currentEnemy = null;
                }
                // --- Enemy 상태 업데이트 끝 ---

                // 변경된 상태를 반영하여 캔버스 다시 그리기
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
        // 연결 종료 시 재연결 시도 (옵션)
        // setTimeout(connectWebSocket, 5000); // 5초 후 재연결 시도
    };
}

// --- 키보드 입력 처리 함수 ---
function handleKeyDown(e) {
    // 웹소켓 연결 상태 확인
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        console.warn("웹소켓이 연결되지 않았습니다.");
        return;
    }
    // 플레이어 ID 설정 여부 확인 (중요!)
    if (!player.id) {
        console.warn("플레이어 ID가 설정되지 않아 이동 메시지를 보낼 수 없습니다.");
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
            playerId: player.id, // 자신의 ID 사용
            direction: direction
        };
        ws.send(JSON.stringify(moveMessage));
    }
}

// --- 통합 그리기 함수 ---
// 이 함수는 클라이언트 측의 'player'와 'currentEnemy' 변수를 사용하므로,
// ws.onmessage에서 이 변수들이 잘 업데이트되면 수정 없이 동작 가능.
// (단, 여전히 플레이어 1명, 적 1명만 그림)
function drawGame() {
    if (!ctx) {
        console.error("2D 컨텍스트가 없습니다.");
        return;
    }
    // 1. 캔버스 클리어
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // 2. 플레이어 그리기 (자신의 플레이어)
    if (player && player.id) { // player 객체 및 id 존재 확인
        ctx.fillStyle = player.color || 'red'; // 기본 색상
        ctx.fillRect(player.x, player.y, player.size, player.size);
    }

    // 3. 적(Enemy) 그리기 (현재는 첫 번째 적만)
    if (currentEnemy) { // currentEnemy 객체가 null이 아닐 때만 그림
         ctx.fillStyle = currentEnemy.color || 'blue'; // 기본 색상
         ctx.fillRect(currentEnemy.x, currentEnemy.y, currentEnemy.size, currentEnemy.size);
    }

    // TODO: 만약 여러 플레이어/적을 그리고 싶다면,
    // 클라이언트 측에 playerList, enemyList 같은 배열을 관리하고
    // drawGame 함수 내에서 이 배열들을 순회하며 그려야 함.
}

// --- 초기화 코드 ---
if (canvas && ctx) {
    // 초기 화면 그리기 (연결 전에는 기본 위치에 그림)
    drawGame();

    // 웹소켓 연결 시작
    connectWebSocket();

    // 키보드 이벤트 리스너 추가
    document.addEventListener('keydown', handleKeyDown);
    console.log("키보드 이벤트 리스너 추가 완료.");

} else {
    console.error("캔버스 또는 컨텍스트를 찾을 수 없어 초기화 실패.");
}