package com.example.demo.core.user.domain.support;

import com.example.demo.core.user.domain.EnemyId;
import com.example.demo.core.user.domain.player.PlayerId;
import com.example.demo.core.user.domain.IdGenerator;
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
}
