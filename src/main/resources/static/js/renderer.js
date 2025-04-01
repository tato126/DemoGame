const canvas = document.getElementById("game");
const ctx = canvas.getContext("2d");

export function drawGame(state, isAnimatingDeath, deathProgress) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // 목표점
    ctx.fillStyle = "red";
    ctx.beginPath();
    ctx.arc(state.dotX + 5, state.dotY + 5, 5, 0, 2 * Math.PI);
    ctx.fill();

    // 플레이어
    ctx.fillStyle = "green";
    if (state.gameOver && isAnimatingDeath) {
        const size = 10 * (1 - deathProgress);
        ctx.fillRect(
            state.playerX + (10 - size) / 2,
            state.playerY + (10 - size) / 2,
            size,
            size
        );
    } else {
        ctx.fillRect(state.playerX, state.playerY, 10, 10);
    }

    // 장애물
    ctx.fillStyle = "gray";
    state.obstacles.forEach(ob => {
        ctx.fillRect(ob.x, ob.y, 10, 10);
    });

    // 텍스트
    document.getElementById("score").innerText = `Score: ${state.score}`;
    document.getElementById("hp").innerText = `HP: ${state.hp}`;

    if (state.gameOver) {
        ctx.fillStyle = "black";
        ctx.font = "28px Arial";
        ctx.textAlign = "center";
        ctx.fillText("Game Over", canvas.width / 2, canvas.height / 2);
    }
}