package com.example.demo.domain.enemy.application;

import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.enemy.EnemyId;

import java.util.Collection;
import java.util.Optional;

public interface EnemyFind {

    Optional<Enemy> findById(EnemyId enemyId);

    Collection<Enemy> findAll();
}
