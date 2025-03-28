package com.example.demo.gameObject.obstacle;

import com.example.demo.domain.Position;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ObstacleFactory {

    public List<Obstacle> generateDefaultObstacles() {
        List<Obstacle> obstacleList = new ArrayList<>();
        obstacleList.add(new Obstacle(new Position(100, 100)));
        obstacleList.add(new Obstacle(new Position(150, 50)));
        obstacleList.add(new Obstacle(new Position(50, 200)));

        return obstacleList;
    }

}
