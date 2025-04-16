package com.example.demo.core.user.domain;

import com.example.demo.core.user.domain.player.PlayerId;

public interface IdGenerator {

    PlayerId generatePlayerId();

    EnemyId generateEnemyId();
}
