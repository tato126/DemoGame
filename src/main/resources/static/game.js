const canvas = document.getElementById("game");
const ctx = canvas.getContext("2d");
const restartBtn = document.getElementById("restartBtn");

const moveSound = new Audio("/move.mp3");
const goalSound = new Audio("/goal.mp3");
const hitSound = new Audio("/hit.mp3");
const deathSound = new Audio("/death.mp3");

window.addEventListener("load", () => {
  moveSound.load();
  goalSound.load();
  hitSound.load();
});

let isGameOver = false;

document.addEventListener("keydown", (e) => {

  if (isGameOver) {
      return;
  }

  let direction;

  switch (e.key) {
    case "ArrowUp": direction = "up"; break;
    case "ArrowDown": direction = "down"; break;
    case "ArrowLeft": direction = "left"; break;
    case "ArrowRight": direction = "right"; break;
    default: return;
  }

  console.log("이동 방향", direction);

  fetch(`/api/move?direction=${direction}`, { method: "POST" })
    .then(res => {
        if(!res.ok) {
          throw new Error('서버 응답 오류 : %{res.status}');
        }
        return res.json();
    })
    .then(state => {
        if(state.gameOver) {
        isGameOver = true;
        deathSound.play();
        draw(state);
        restartBtn.style.display = "inline-block";
        return;
        }

    console.log("Collision 값:", state.collision);

      if (state.reachedGoal) {
        goalSound.currentTime = 0;
        goalSound.play();
      } else if (state.collision) {
        hitSound.pause();
        hitSound.currentTime = 0;
        hitSound.play().catch(e => console.error("사운드 재생 실패: ", e));
      } else {
        moveSound.currentTime = 0;
        moveSound.play();
      }
      draw(state);
    })
    .catch(error => {
        console.error("요청 처리 중 오류 발생:",error);
    });
});

restartBtn.addEventListener("click", () => {
    fetch("/api/reset", { method: "POST" })
    .then(() => {
        fetch("/api/state")
            .then(res => res.json())
            .then(draw)
    });
});

 function draw(state) {
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      // 목표
      ctx.fillStyle = "green";
      ctx.beginPath();
      ctx.arc(state.dotX + 5, state.dotY + 5, 5, 0, 2 * Math.PI); // 중심 좌표 + 반지름
      ctx.fill();

      // 플레이어
      ctx.fillStyle = "red";
      ctx.fillRect(state.playerX, state.playerY, 10, 10);

      // 장애물
      ctx.fillStyle = "gray";
      state.obstacles.forEach(ob => {
      ctx.fillRect(ob.x, ob.y, 10, 10);
      });


      document.getElementById("score").innerText = `Score: ${state.score}`;
      document.getElementById("hp").innerText = `HP: ${state.hp}`;

      if (state.gameOver) {
        ctx.fillStyle = "black";
        ctx.font = "28px Arial";
        ctx.textAlign = "center";
        ctx.fillText("Game Over", canvas.width / 2, canvas.height / 2);
      }
    }

    fetch("/api/state")
      .then(res => res.json())
      .then(draw);
