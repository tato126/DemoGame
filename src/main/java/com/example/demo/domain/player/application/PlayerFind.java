package com.example.demo.domain.player.application;

import com.example.demo.domain.player.Player;
import com.example.demo.domain.player.PlayerId;

import java.util.Collection;
import java.util.Optional;

public interface PlayerFind {

    Optional<Player> byId(PlayerId playerId);

    Collection<Player> findAll();

}
