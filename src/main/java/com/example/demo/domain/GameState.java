package com.example.demo.domain;

import com.example.demo.canvas.Canvas;
import com.example.demo.gameObject.Dot;
import com.example.demo.gameObject.obstacle.Obstacle;
import com.example.demo.gameObject.Player;
import com.example.demo.gameObject.obstacle.ObstacleFactory;
import com.example.demo.rule.GameRuleEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * 게임 위치와 이동 클래스.
 *
 * @author chan
 */
@Component
public class GameState {

    private static final Logger log = LoggerFactory.getLogger(GameState.class);

    private final Canvas canvas;
    private final GameRuleEvaluator rule;
    private final ObstacleFactory obstacleFactory;

    private Player player;
    private Dot dot;
    private List<Obstacle> obstacles;

    private boolean reachedGoal; // 도달 여부 플래그.
    private boolean collision; // 충돌 확인 여부 플래그.
    private boolean gameOver;
    private int score; // 총 점수

    public GameState(ObstacleFactory obstacleFactory) {
        this.canvas = new Canvas();
        this.rule = new GameRuleEvaluator();
        this.obstacleFactory = obstacleFactory;

        this.player = new Player(new Position(150, 150));
        this.dot = generateRandomDot();
        this.obstacles = obstacleFactory.generateObstaclesNearPath(3, player.getSize(), canvas.getWidth(), canvas.getHeight(), player.getPosition(), dot.getPosition());

        this.reachedGoal = false;
        this.collision = false;
        this.gameOver = false;
        this.score = 0;

        log.debug("게임 시작 - 시작 위치: {}", player.getPosition());
        log.debug("목표물 생성 위치: {}", dot.getPosition());
        log.debug("플레이어의 시작 체력 : {}", player.getHp());
        log.debug("getPlayerX : {}", getPlayerX());
        log.debug("GetObstacles : {}", getObstacles());
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

    public void damagePlayer() {
        player.reduceHp();

        if (player.isDead()) {
            gameOver = true;
        }
    }

    public void reset() {
        this.player = new Player(new Position(150, 150));
        this.dot = generateRandomDot();
        this.score = 0;
        this.collision = false;
        this.reachedGoal = false;
        this.gameOver = false;
        this.obstacles = obstacleFactory.generateObstaclesNearPath(3, player.getSize(), canvas.getWidth(), canvas.getHeight(), player.getPosition(), dot.getPosition());

        log.debug("==== 게임 상태 리셋 ====");
        log.debug("새로 생성된 장애물들: {}", obstacles);
        log.debug("목표물 생성 위치: {}", dot.getPosition());
        log.debug("플레이어의 시작 체력 : {}", player.getHp());

    }

    public void applyPlayerMovement(Player movedPlayer) {
        this.player = movedPlayer;
    }

    public void checkReachedGoal() {
        this.reachedGoal = true;
    }

    public void checkCollision() {
        this.collision = true;
    }

    public void clearCollision() {
        this.collision = false;
    }

    public void increaseScore(int amount) {
        this.score += amount;
    }

    public void generateNewDot() {
        this.dot = generateRandomDot();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isReachedGoal() {
        return reachedGoal;
    }

    public Player getPlayer() {
        return player;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public int getPlayerX() {
        return player.getPosition().getX();
    }

    public int getPlayerY() {
        return player.getPosition().getY();
    }

    public Dot getDot() {
        return dot;
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

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public boolean getCollision() {
        return collision;
    }
}
