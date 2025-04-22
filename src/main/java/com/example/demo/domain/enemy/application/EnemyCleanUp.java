package com.example.demo.domain.enemy.application;

import com.example.demo.domain.enemy.EnemyId;

public interface EnemyCleanUp {

    void remove(EnemyId enemyId);

    void clearAll();
}
