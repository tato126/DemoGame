package com.example.demo.domain.weapon;

import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;

public interface Weapon {

    void shoot(Object ownerId, Position startPosition, Direction targetDirection);
}
