package com.example.demo.domain;

import com.example.demo.canvas.Canvas;
import com.example.demo.gameObject.Dot;
import com.example.demo.gameObject.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 게임 위치와 이동 클래스.
 *
 * @author chan
 */
@Component
public class GameState {

    private static final Logger log = LoggerFactory.getLogger(GameState.class);

    private final Canvas canvas = new Canvas();

    private Player player = new Player(new Position(150, 150));
    private Dot dot = generateRandomDot();


    private boolean reachedGoal = false; // 도달 여부 플래그.

    private int score = 0; // 총 점수

    public GameState() {
        log.debug("게임 시작 - 시작 위치: {}", player.getPosition());
        log.debug("목표물 생성 위치: {}", dot.getPosition());
    }

    /**
     * 플레이어의 움직임.
     *
     * @param direction 방향키
     */
    public void move(String direction) {

        Player playerDirection = player.move(direction);
        log.debug("player direction : {}", direction);

        Position nextPosition = playerDirection.getPosition();
        log.debug("Next Position : {}", nextPosition);


        if (canvas.isWithinBounds(nextPosition, playerDirection.getSize())) {
            if (!nextPosition.equals(player.getPosition())) {
                player = playerDirection;
                reachedGoal = false;

                if (nextPosition.equals(dot.getPosition())) {
                    score += 10;
                    dot = generateRandomDot();
                    reachedGoal = true;
                }
            }
            log.debug("== ReachedGoal : {} ==", reachedGoal);
            log.debug("== User Score : {} ==", score);
        }
    }

    /**
     * 목적지 점 객체를 무작위 위치에 생성한다.
     *
     * @return 무작위 위치에 생성된 새로운 목적지.
     */
    private Dot generateRandomDot() {

        int step = player.getSize();

        int maxX = (canvas.getWidth() - step) / step;
        log.debug("==== RandomDot ==== MAX_WIDTH RANGE : {}", maxX);

        int maxY = (canvas.getHeight() - step) / step;
        log.debug("==== RandomDot ==== MAX_HEIGHT RANGE : {}", maxY);

        int dotX = new Random().nextInt(maxX) * step;
        int dotY = new Random().nextInt(maxY) * step;

        log.debug("==== RandomDto location ==== : X : {}, Y : {}", dotX, dotY);

        return new Dot(new Position(dotX, dotY));
    }

    public boolean isReachedGoal() {
        return reachedGoal;
    }

    public int getPlayerX() {
        return player.getPosition().getX();
    }

    public int getPlayerY() {
        return player.getPosition().getY();
    }

    public int getDotX() {
        return dot.getPosition().getX();
    }

    public int getDotY() {
        return dot.getPosition().getY();
    }

    public int getScore() {
        return score;
    }
}
