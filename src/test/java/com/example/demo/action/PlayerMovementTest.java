package com.example.demo.action;

import com.example.demo.domain.Position;
import com.example.demo.gameObject.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PlayerMovementTest {

    @Test
    void moveUp_shouldDecreaseY() {
        Player player = new Player(new Position(100, 100));
        Player moved = player.move("up");
        assertEquals(100, moved.getPosition().getX());
        assertEquals(90, moved.getPosition().getY());
    }

    @Test
    void moveDown_shouldIncreaseY() {
        Player player = new Player(new Position(100, 100));
        Player moved = player.move("down");
        assertEquals(100, moved.getPosition().getX());
        assertEquals(110, moved.getPosition().getY());
    }

    @Test
    void moveLeft_shouldDecreaseX() {
        Player player = new Player(new Position(100, 100));
        Player moved = player.move("left");
        assertEquals(90, moved.getPosition().getX());
        assertEquals(100, moved.getPosition().getY());
    }

    @Test
    void moveRight_shouldIncreaseX() {
        Player player = new Player(new Position(100, 100));
        Player moved = player.move("right");
        assertEquals(110, moved.getPosition().getX());
        assertEquals(100, moved.getPosition().getY());
    }
}
