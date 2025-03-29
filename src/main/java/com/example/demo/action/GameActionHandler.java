package com.example.demo.action;

import com.example.demo.domain.GameState;
import com.example.demo.domain.Position;
import com.example.demo.gameObject.Player;
import com.example.demo.rule.GameRuleEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 게임 내 움직임에 대한 정의 클래스.
 *
 * @author chan
 */
@Component
public class GameActionHandler {

    private static final Logger log = LoggerFactory.getLogger(GameActionHandler.class);
    private final GameRuleEvaluator rule = new GameRuleEvaluator();

    /**
     * 플레이어 이동 담당 메서드.
     *
     * @param direction 움직이는 방향.
     */
    public void move(GameState gameState, String direction) {

        Player player = gameState.getPlayer();
        Player movedPlayer = player.move(direction);
        Position nextPosition = movedPlayer.getPosition();

        log.debug("이동 요청: {}", direction);
        log.debug("다음 위치: {}", nextPosition);

        // 캔버스 영역 밖이면 무시
        if (!gameState.getCanvas().isWithinBounds(nextPosition, player.getSize())) {
            return;
        }

        // 장애물 충돌 체크.
        if (rule.isColliding(nextPosition, gameState.getObstacles(), player.getSize())) {
            log.debug("!!!충돌 발생 - 이동 불가!!!");
            gameState.damagePlayer(); // 체력 감소 처리.
            gameState.checkCollision();
            log.debug("SOS : Reduce Hp!!!!!!!!!!! : {}", player.getHp());
            return;
        }

        gameState.applyPlayerMovement(movedPlayer);
        gameState.clearCollision();

        if (rule.isReachedGoal(nextPosition, gameState.getDot().getPosition())) {
            gameState.generateNewDot();
        } else {
            gameState.checkReachedGoal();
        }
    }
}
