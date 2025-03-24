const canvas = document.getElementById("game");
const ctx = canvas.getContext("2d");

const moveSound = new Audio("/move.mp3");
const goalSound = new Audio("/goal.mp3");

window.addEventListener("load", () => {
  moveSound.load();
  goalSound.load();
});

document.addEventListener("keydown", (e) => {
  let direction;
  switch (e.key) {
    case "ArrowUp": direction = "up"; break;
    case "ArrowDown": direction = "down"; break;
    case "ArrowLeft": direction = "left"; break;
    case "ArrowRight": direction = "right"; break;
    default: return;
  }

  fetch(`/api/move?direction=${direction}`, { method: "POST" })
    .then(res => res.json())
    .then(state => {
      if (state.reachedGoal) {
        goalSound.currentTime = 0;
        goalSound.play();
      } else {
        moveSound.currentTime = 0;
        moveSound.play();
      }
      draw(state);
    });
});
