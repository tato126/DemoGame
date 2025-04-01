import {
    loadSounds,
    playMoveSound,
    playGoalSound,
    playHitSound,
    playDeathSound,
} from "./audio.js";

import { drawGame } from "./renderer.js";
import { animateDeath } from "./animation.js";

let isGameOver = false;
let isAnimatingDeath = false;
let currentState = null;

const restartBtn = document.getElementById("restartBtn");

window.addEventListener("load", () => {
    loadSounds();
    fetchStateAndDraw();
});

document.addEventListener("keydown", (e) => {
    if (isGameOver || isAnimatingDeath) return;

    let direction;
    switch (e.key) {
        case "ArrowUp": direction = "up"; break;
        case "ArrowDown": direction = "down"; break;
        case "ArrowLeft": direction = "left"; break;
        case "ArrowRight": direction = "right"; break;
        default: return;
    }

    fetch(`/api/move?direction=${direction}`, {method: "POST" })
        .then(res => res.json())
        .then(handleGameState)
        .catch(console.error);
});

// 다시 시작 버튼
restartBtn.addEventListener("click", () => {
    fetch("/api/reset", { method: "POST" })
        .then(() => fetchStateAndDraw());
});

function fetchStateAndDraw() {
    fetch("/api/state")
        .then(res => res.json())
        .then(state => {
            isGameOver = false;
            isAnimatingDeath = false;
            currentState = state;
            restartBtn.style.display = "none";
            drawGame(state, false, 0);
        });
}

function handleGameState(state) {
    currentState = state;

    if (state.gameOver) {
        isGameOver = true;
        isAnimatingDeath = true;
        playDeathSound();
        animateDeath(state, () => {
            isAnimatingDeath = false;
        });
        restartBtn.style.display = "inline-block";
        return;
    }

    if (state.collision) {
        playHitSound();
    } else if (state.reachedGoal) {
        playGoalSound();

    } else {
        playMoveSound();
    }

    drawGame(state, false, 0);
}