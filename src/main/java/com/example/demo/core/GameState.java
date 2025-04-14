package com.example.demo.core;

import com.example.demo.core.user.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


/**
 * 현재 게임 상태 저장 클래스.
 *
 * @author chan
 */
@Component
@ApplicationScope
public class GameState {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Player player;

    // 초기 상태 설정 (Bean 생성 시 호출됨)
    public GameState() {
        this.player = new Player(UUID.randomUUID().toString(), new Position(50, 50), 20);
        log.debug("Initial GameState created. Player ID: {}", this.player.getId());
    }

    public Player getPlayer() {
        return player;
    }

    // 플레이어 상태 업데이트 (불변 객체 교체 방식)
    public void updatePlayer(Player newPlayer) {
        Objects.requireNonNull(newPlayer);

        // 현재는 플레이어가 한 명이므로 ID 비교 후 교체 (나중에는 ID로 찾아야 함)
        if (this.player != null && this.player.getId().equals(newPlayer.getId())) {
            this.player = newPlayer;
        } else {
            // ID가 다르거나 기존 플레이어가 없는 경우 (오류/초기화 상황)
            log.debug("Warning: Player update failed or initial player set.");
            this.player = newPlayer;
        }
    }

    // ID로 플레이어 찾기
    public Optional<Player> findPlayerById(String playerId) {
        if (this.player != null && this.player.getId().equals(playerId)) {
            return Optional.of(this.player);
        }

        return Optional.empty();
    }

    // 게임 리셋 로직
    public void reset(Player initialPlayer) {
        this.player = initialPlayer;
        log.debug("GameState reset. Player ID: {}", this.player.getId());

        // 점수 등 다른 요소 초기화
    }
}
