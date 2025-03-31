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
let deathAnimationProgress = 0;
let isAnimatingDeath = false;
let currentState = null;

document.addEventListener("keydown", (e) => {

  if (isGameOver || isAnimatingDeath) {
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
        console.log("게임 오버 상태 감지")
        isGameOver = true;
        isAnimatingDeath = true;
        currentState = state;
        deathAnimationProgress = 0;
        deathSound.play();
        console.log("사망 애니메이션 시작")
        animateDeath();
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
    .then(() => fetch("/api/state"))
    .then(res => res.json())
    .then(state => {
      isGameOver = false;
      isAnimatingDeath = false;
      deathAnimationProgress = 0;
      currentState = state;
      restartBtn.style.display = "none";
      draw(state);
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

      if (state.gameOver && isAnimatingDeath) {
        // 죽는 중일 때 점점 작아짐
        const maxSize = 10;
        const currentSize = maxSize * (1 - deathAnimationProgress);

        ctx.fillRect(
            state.playerX + (maxSize - currentSize) / 2,
            state.playerY + (maxSize - currentSize) / 2,
            currentSize,
            currentSize
        );
      } else {
        ctx.fillRect(state.playerX, state.playerY, 10, 10);
      }

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

    function animateDeath() {
      if (!currentState) return;

      deathAnimationProgress += 0.01;

      draw(currentState);

      if (deathAnimationProgress < 1) {
        requestAnimationFrame(animateDeath);
      } else {
        isAnimatingDeath = false;
      }
    }

    fetch("/api/state")
      .then(res => res.json())
      .then(draw);
