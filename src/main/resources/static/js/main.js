// Demo-main/src/main/resources/static/js/main.js

console.log("main.js 로드됨 - 전역 변수 관리 개선 버전");

// 게임 관련 상태와 설정을 관리하는 game 객체
const game = {
    canvas: null,
    ctx: null,
    player: { id: null, x: 0, y: 0, size: 0, color: 'red', direction: "UP" },
    enemies: [],
    projectiles: [],
    websocket: {
        instance: null,
        url: `ws://${window.location.host}/game-ws` // 서버 설정과 동일한 엔드포인트 사용
    },
    config: {
        // 색상 등 클라이언트 측에서만 사용되는 설정값들을 여기에 추가할 수 있습니다.
        // 서버에서 색상 정보를 보내준다면 이 부분은 필요 없을 수 있습니다.
        defaultPlayerColor: 'red',
        defaultEnemyColor: 'blue',
        defaultProjectileColor: 'orange'
    },
    isWebSocketConnected: function() {
        return this.websocket.instance && this.websocket.instance.readyState === WebSocket.OPEN;
    },
    canSendMessage: function() {
        return this.isWebSocketConnected() && this.player.id !== null;
    }
};

// --- 초기화 코드 ---
function initializeGame() {
    game.canvas = document.getElementById('gameCanvas');
    if (!game.canvas) {
        console.error("캔버스를 찾을 수 없습니다.");
        return false;
    }
    game.ctx = game.canvas.getContext('2d');
    if (!game.ctx) {
        console.error("2D 컨텍스트를 가져올 수 없습니다.");
        return false;
    }

    console.log("게임 초기화 시작...");
    drawGame(); // 초기 화면 그리기 (아무것도 없는 상태)
    connectWebSocket(); // 웹소켓 연결 시작
    document.addEventListener('keydown', handleKeyDown); // 키보드 리스너 추가
    console.log("초기화 완료 및 키보드 이벤트 리스너 추가 완료.");
    return true;
}

// --- 웹소켓 연결 함수 ---
function connectWebSocket() {
    console.log("웹소켓 연결 시도:", game.websocket.url);
    game.websocket.instance = new WebSocket(game.websocket.url);

    game.websocket.instance.onopen = () => {
        console.log("웹소켓 연결 성공!");
    };

    game.websocket.instance.onmessage = (event) => {
        console.log("서버로부터 메시지 수신:", event.data);
        try {
            const message = JSON.parse(event.data);

            if (message.type === 'gameStateUpdate') {
                const serverPlayers = message.players || [];
                console.log("받은 플레이어 목록:", serverPlayers);

                // 클라이언트의 player 객체 업데이트 (game.player 사용)
                if (game.player.id === null && serverPlayers.length > 0) {
                    // 서버가 보낸 첫 번째 플레이어를 내 플레이어로 가정 (서버 로직에 따라 수정 필요)
                    // 또는 서버가 명시적으로 클라이언트 ID를 알려주는 메시지 타입 추가 고려
                    const myPlayerDataFromServer = serverPlayers.find(p => {
                        // 이 부분은 서버가 클라이언트의 초기 ID를 어떻게 알려주는지에 따라 달라집니다.
                        // 만약 연결 직후 서버가 보내는 첫 gameStateUpdate의 players 배열에
                        // 현재 클라이언트의 플레이어 정보가 유일하게 포함되거나,
                        // 특정 속성(예: isMe=true)으로 구분된다면 해당 로직을 사용합니다.
                        // 지금은 임시로, 아직 ID가 없고 서버 플레이어 목록에 있으면 첫번째 것을 내것으로 간주합니다.
                        // 더 견고한 방법은 서버가 연결된 클라이언트에게 고유 ID를 명시적으로 알려주는 것입니다.
                        return true; // 단순 예시, 실제로는 더 정확한 식별 방법 필요
                    });

                    if (myPlayerDataFromServer) {
                         // 서버 DTO에 color가 없다면 클라이언트에서 설정
                        game.player.id = myPlayerDataFromServer.id;
                        game.player.color = myPlayerDataFromServer.color || game.config.defaultPlayerColor;
                        console.log("초기 클라이언트 플레이어 ID 및 색상 설정:", game.player.id, game.player.color);
                    }
                }

                if (game.player.id !== null) {
                    const myPlayerData = serverPlayers.find(p => p.id === game.player.id);
                    if (myPlayerData) {
                        console.log("내 플레이어 데이터 발견, 상태 업데이트:", myPlayerData);
                        game.player.x = myPlayerData.x;
                        game.player.y = myPlayerData.y;
                        game.player.size = myPlayerData.size;
                        // game.player.speed = myPlayerData.speed; // 서버에서 speed를 보내준다면 업데이트
                        game.player.direction = myPlayerData.direction;
                         // 서버 DTO에 color가 있다면 사용, 없다면 기존 클라이언트 색상 유지 또는 기본값 사용
                        game.player.color = myPlayerData.color || game.player.color || game.config.defaultPlayerColor;
                    } else {
                        console.warn("서버 목록에 내 플레이어 ID가 없습니다! ID:", game.player.id);
                        game.player.id = null; // ID 초기화
                    }
                }

                // Enemy 상태 업데이트 (game.enemies 사용)
                const serverEnemies = message.enemies || [];
                game.enemies = serverEnemies.map(e => ({
                    ...e,
                    color: e.color || game.config.defaultEnemyColor // 서버 DTO에 color가 없다면 클라이언트 기본값 사용
                }));

                // Projectile 상태 업데이트 (game.projectiles 사용)
                const serverProjectiles = message.projectiles || [];
                game.projectiles = serverProjectiles.map(p => ({
                    ...p,
                    color: p.color || game.config.defaultProjectileColor // 서버 DTO에 color가 없다면 클라이언트 기본값 사용
                }));

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

    game.websocket.instance.onerror = (error) => {
        console.error("웹소켓 오류:", error);
    };

    game.websocket.instance.onclose = (event) => {
        console.log("웹소켓 연결 종료:", event.code, event.reason);
        game.websocket.instance = null;
        game.player.id = null;
        game.enemies = [];
        game.projectiles = [];
        drawGame(); // 화면 클리어
        // 필요시 재연결 로직 추가
        // setTimeout(connectWebSocket, 5000);
    };
}

// --- 키보드 입력 처리 함수 ---
function handleKeyDown(e) {
    if (!game.canSendMessage()) {
        if (!game.isWebSocketConnected()) {
            console.warn("웹소켓이 연결되지 않았습니다.");
        } else if (!game.player.id) {
            console.warn("플레이어 ID가 설정되지 않아 메시지를 보낼 수 없습니다.");
        }
        return;
    }

    let keyDirection = null;
    let actionType = null;

    switch (e.key) {
        case "ArrowUp":    keyDirection = "UP"; actionType = "move"; break;
        case "ArrowDown":  keyDirection = "DOWN"; actionType = "move"; break;
        case "ArrowLeft":  keyDirection = "LEFT"; actionType = "move"; break;
        case "ArrowRight": keyDirection = "RIGHT"; actionType = "move"; break;
        case " ":
        case "Spacebar":
            actionType = "shot";
            // 발사 시에는 현재 플레이어의 방향(game.player.direction)을 사용하므로,
            // 이동 시 업데이트된 game.player.direction을 참조합니다.
            console.log("발사 시 사용될 방향:", game.player.direction);
            break;
    }

    if (actionType === "move" && keyDirection) {
        const moveMessage = {
            type: "move",
            playerId: game.player.id,
            direction: keyDirection
        };
        game.websocket.instance.send(JSON.stringify(moveMessage));
        console.log("이동 메시지 전송:", moveMessage);
        // 클라이언트 측 예측 이동 (선택 사항, 반응성 향상)
        // game.player.direction = keyDirection; // 서버에서 최종 상태를 받으므로, 클라이언트 예측은 생략하거나 주의해서 사용
    } else if (actionType === "shot") {
        if (!game.player.direction) { // 혹시 방향이 설정 안된 극초기 상태 방지
            game.player.direction = "UP"; // 기본값
            console.warn("플레이어 방향이 설정되지 않아 기본 방향(UP)으로 발사합니다.");
        }
        const shotMessage = {
            type: "shot",
            playerId: game.player.id,
            direction: game.player.direction // 현재 플레이어가 바라보는 방향으로 발사
        };
        game.websocket.instance.send(JSON.stringify(shotMessage));
        console.log("발사 메시지 전송:", shotMessage);
    }
}

// --- 통합 그리기 함수 ---
function drawGame() {
    if (!game.ctx) {
        // console.error("2D 컨텍스트가 없습니다. 게임 그리기를 건너뜁니다."); // 초기화 전 호출될 수 있음
        return;
    }
    game.ctx.clearRect(0, 0, game.canvas.width, game.canvas.height);

    // 플레이어 그리기 (game.player 사용)
    if (game.player.id) {
        game.ctx.fillStyle = game.player.color || game.config.defaultPlayerColor;
        game.ctx.fillRect(game.player.x, game.player.y, game.player.size, game.player.size);
    }

    // 적(Enemy) 목록 그리기 (game.enemies 사용)
    game.enemies.forEach(enemy => {
        if (enemy) {
            game.ctx.fillStyle = enemy.color || game.config.defaultEnemyColor;
            game.ctx.fillRect(enemy.x, enemy.y, enemy.size, enemy.size);
        }
    });

    // 투사체(Projectile) 목록 그리기 (game.projectiles 사용)
    game.projectiles.forEach(projectile => {
        if (projectile) {
            game.ctx.fillStyle = projectile.color || game.config.defaultProjectileColor;
            game.ctx.fillRect(projectile.x, projectile.y, projectile.size, projectile.size);
        }
    });
}

// --- 게임 시작 ---
// DOM이 완전히 로드된 후 게임을 초기화합니다.
document.addEventListener('DOMContentLoaded', () => {
    if (!initializeGame()) {
        console.error("게임 초기화 실패.");
    }
});