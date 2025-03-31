package com.example.demo.gameObject.obstacle;

import com.example.demo.domain.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 장애물 판정 처리 클래스/
 *
 * @author chan
 */
@Component
public class ObstacleFactory {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public List<Obstacle> generateDefaultObstacles() {
        List<Obstacle> obstacleList = new ArrayList<>();
        obstacleList.add(new Obstacle(new Position(100, 100)));
        obstacleList.add(new Obstacle(new Position(150, 50)));
        obstacleList.add(new Obstacle(new Position(50, 200)));

        return obstacleList;
    }

    /**
     * 캔버스 내에 무작위로 생성
     *
     * @param count
     * @param wide
     * @param height
     * @param step
     * @param playerPosition
     * @param dotPosition
     * @return
     */
    public List<Obstacle> generateRandomObstacles(int count, int wide, int height, int step, Position playerPosition, Position dotPosition) {
        List<Obstacle> obstacleList = new ArrayList<>();
        Random random = new Random();

        int maxX = (wide - step) / step;
        int maxY = (height - step) / step;

        while (obstacleList.size() < count) {
            int x = random.nextInt(maxX) * step;
            int y = random.nextInt(maxY) * step;
            Position randomObstacle = new Position(x, y);

            boolean overlaps =
                    randomObstacle.equals(playerPosition) ||
                            randomObstacle.equals(dotPosition) ||
                            obstacleList.stream().anyMatch(ob -> ob.getPosition().equals(randomObstacle));

            if (!overlaps) {
                obstacleList.add(new Obstacle(randomObstacle));
            }
        }

        return obstacleList;
    }

    public List<Obstacle> generateObstaclesNearPath(int count, int step, int width, int height, Position playerPosition, Position dotPosition) {

        log.debug("<<<<<<<<<<<<<<<< GenerateObstaclesNearPath >>>>>>>>>>>>>>>>>>>");
        List<Obstacle> obstacles = new ArrayList<>();
        Random random = new Random();

        int maxX = (width - step) / step;
        log.debug("generateObstaclesNearPath : MaxX {}", maxX);
        int maxY = (height - step) / step;
        log.debug("generateObstaclesNearPath : MaxY {}", maxY);

        int attempts = 0;
        while (obstacles.size() < count && attempts < 100) {
            attempts++;

            double t = random.nextDouble();
            int rawX = (int) (playerPosition.getX() + t * (dotPosition.getX() - playerPosition.getX()));
            int rawY = (int) (playerPosition.getY() + t * (dotPosition.getY() - playerPosition.getY()));


            int pathX = (rawX / step) * step;
            int pathY = (rawY / step) * step;

            int offsetX = (random.nextInt(3) - 1) * step;
            int offsetY = (random.nextInt(3) - 1) * step;

            int finalX = pathX + offsetX;
            int finalY = pathY + offsetY;

            finalX = Math.max(0, Math.min(finalX, width - step));
            finalY = Math.max(0, Math.min(finalY, height - step));

            log.debug("pathX,Y: ({}, {})", pathX, pathY);
            log.debug("final position: ({}, {})", finalX, finalY);

            Position position = new Position(finalX, finalY);

            boolean overlaps =
                    position.equals(playerPosition) ||
                            position.equals(dotPosition) ||
                            obstacles.stream().anyMatch(ob -> ob.getPosition().equals(position));

            if (!overlaps) {
                obstacles.add(new Obstacle(position));
            }
        }

        return obstacles;
    }

}
