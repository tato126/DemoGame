export const moveSound = new Audio("/sound/move.mp3");
export const goalSound = new Audio("/sound/goal.mp3");
export const hitSound = new Audio("/sound/hit.mp3");
export const deathSound = new Audio("/sound/death.mp3");

export function loadSounds() {
    moveSound.load();
    goalSound.load();
    hitSound.load();
}

export function playMoveSound() {
    moveSound.currentTime = 0;
    moveSound.play();
}

export function playGoalSound() {
    goalSound.currentTime = 0;
    goalSound.play();
}

export function playHitSound() {
    hitSound.pause();
    hitSound.currentTime = 0;
    hitSound.play();
}

export function playDeathSound() {
    deathSound.currentTime = 0;
    deathSound.play();
}

