package com.example.demo.domain.player.application;

import com.example.demo.domain.player.Player;
import com.example.demo.domain.player.PlayerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultPlayerManager implements PlayerRegistry, PlayerFind, PlayerCleanUp {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<PlayerId, Player> players = new ConcurrentHashMap<>();

    @Override
    public void addOrUpdate(Player player) {
        Objects.requireNonNull(player, "Player cannot be null for addOrUpdate");
        Objects.requireNonNull(player.getId(), "PlayerId cannot be null for addOrUpdate");

        players.put(player.getId(), player);
        log.debug("[PlayerManager] Player added or updated: {}", player.getId());
    }

    @Override
    public Optional<Player> findById(PlayerId playerId) {
        Objects.requireNonNull(playerId, "Player ID cannot be null for findById");
        return Optional.ofNullable(players.get(playerId));
    }

    @Override
    public Collection<Player> findAll() {
        return Collections.unmodifiableCollection(players.values());
    }

    @Override
    public void remove(PlayerId playerId) {
        Objects.requireNonNull(playerId, "Player ID cannot be null for remove");
        Player removedPlayer = players.remove(playerId);
        if (removedPlayer != null) {
            log.debug("[PlayerManager] Player removed: {}", playerId);
        } else {
            log.warn("[PlayerManager] Attempted to remove non-existent player: {}", playerId);
        }
        log.debug("[PlayerManager] Get current player list: {}", players.size()); // 임시
    }

    @Override
    public void clearAll() {
        int count = players.size();
        players.clear();
        log.debug("[PlayerManager] All players cleared ({} players removed).", count);

    }


}
