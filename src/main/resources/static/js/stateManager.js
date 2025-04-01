let isGameOver = false;

export function handleKeyPress(e) {

   if(isGameOver) return;

   const direction = getDirection(e.key);
   if(!direction) return;

   fetch('/api/move?direction=${direction}', { method: "POST" })
    .then(res => res.json())
    .then(state => {
        if (state.gameOver) {
            isGameOver = true;
            playSound('death');
            animationDeath(state);
            return;
        }

        playSound(state);
        draw(state);
    });
}

function getDirection(key) {
    switch (key) {
        case "Up": return "up";
        case "Down": return "down";
        case "Left": return "left";
        case "Right": return "right";
        default: return null;
    }
}