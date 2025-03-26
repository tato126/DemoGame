package com.example.demo.domain;

import com.example.demo.canvas.Canvas;
import com.example.demo.gameObject.Dot;
import com.example.demo.gameObject.Obstacle;
import com.example.demo.gameObject.Player;
import com.example.demo.rule.GameRuleEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    private final Canvas canvas = new Canvas();
    private final GameRuleEvaluator rule = new GameRuleEvaluator();

    private Player player = new Player(new Position(150, 150));
    private Dot dot = generateRandomDot();

    private final List<Obstacle> obstacles = new ArrayList<>();

    private boolean reachedGoal = false; // 도달 여부 플래그.
    private boolean collision = false;
    private int score = 0; // 총 점수

    public GameState() {
        log.debug("게임 시작 - 시작 위치: {}", player.getPosition());
        log.debug("목표물 생성 위치: {}", dot.getPosition());


        obstacles.add(new Obstacle(new Position(100, 100)));
        obstacles.add(new Obstacle(new Position(150, 50)));
        obstacles.add(new Obstacle(new Position(50, 200)));

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

    public boolean isReachedGoal() {
        return reachedGoal;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public void setReachedGoal(boolean reachedGoal) {
        this.reachedGoal = reachedGoal;
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

    public void setScore(int score) {
        this.score = score;
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

    public void generateNewDot() {
        this.dot = generateRandomDot();
    }
}
