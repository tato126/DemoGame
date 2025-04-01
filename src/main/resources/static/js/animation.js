import { drawGame } from './renderer.js';

let process = 0;
let currentSate = null;
let callback = null;

export function animateDeath(state, onFinish) {
    currentSate = state;
    process = 0;
    callback = onFinish;
    requestAnimationFrame(step);
}

function step() {
    process += 0.01;
    drawGame(currentSate, true, process);

    if (process < 1) {
        requestAnimationFrame(step);
    } else {
        callback?.(); // 사망 애니메이션 완료 콜백 실행.
    }
}