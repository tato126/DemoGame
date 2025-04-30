package com.example.demo.domain.support;

import com.example.demo.domain.enemy.EnemyId;
import com.example.demo.domain.player.PlayerId;
import com.example.demo.domain.projectile.ProjectileId;

public interface IdGenerator {

    PlayerId generatePlayerId();

    EnemyId generateEnemyId();

    ProjectileId generatedProjectileId();
}
