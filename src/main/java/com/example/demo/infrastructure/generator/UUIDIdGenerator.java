package com.example.demo.infrastructure.generator;

import com.example.demo.domain.enemy.EnemyId;
import com.example.demo.domain.player.PlayerId;
import com.example.demo.domain.projectile.ProjectileId;
import com.example.demo.domain.support.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDIdGenerator implements IdGenerator {


    @Override
    public PlayerId generatePlayerId() {
        return new PlayerId(UUID.randomUUID().toString());
    }

    @Override
    public EnemyId generateEnemyId() {
        return new EnemyId(UUID.randomUUID().toString());
    }

    @Override
    public ProjectileId generatedProjectileId() {
        return new ProjectileId(UUID.randomUUID().toString());
    }
}
