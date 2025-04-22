package com.example.demo.domain.player.application;

import com.example.demo.domain.player.PlayerId;

public interface PlayerCleanUp {

    void remove(PlayerId playerId);

    void clearAll();
}
