package com.example.demo.domain.support;

import com.example.demo.domain.enemy.EnemyId;
import com.example.demo.domain.player.PlayerId;

public interface IdGenerator {

    PlayerId generatePlayerId();

    EnemyId generateEnemyId();
}
