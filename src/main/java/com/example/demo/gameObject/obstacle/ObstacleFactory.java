package com.example.demo.gameObject.obstacle;

import com.example.demo.domain.Position;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class ObstacleFactory {


    public List<Obstacle> generateDefaultObstacles() {
        List<Obstacle> obstacleList = new ArrayList<>();
        obstacleList.add(new Obstacle(new Position(100, 100)));
        obstacleList.add(new Obstacle(new Position(150, 50)));
        obstacleList.add(new Obstacle(new Position(50, 200)));

        return obstacleList;
    }

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

}
