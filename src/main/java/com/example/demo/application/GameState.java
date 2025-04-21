package com.example.demo.application;

import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.*;

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
    private Enemy enemy;

    // 초기 상태 설정 (Bean 생성 시 호출됨)
    public GameState() {
        log.debug("[Create] GameState created. Player will be initialized by GameService.");
    }

    // ID로 플레이어 찾기
    public Optional<Player> findPlayerById(String playerId) {
        if (this.player != null && this.player.getId().equals(playerId)) {
            return Optional.of(this.player);
        }

        return Optional.empty();
    }

    // 플레이어 상태 업데이트 (불변 객체 교체 방식)
    public void updatePlayer(Player newPlayer) {
        Objects.requireNonNull(newPlayer);

        // 현재는 플레이어가 한 명이므로 ID 비교 후 교체 (나중에는 ID로 찾아야 함)
        if (this.player != null && this.player.getId().equals(newPlayer.getId())) {
            this.player = newPlayer;
        } else {
            // ID가 다르거나 기존 플레이어가 없는 경우 (오류/초기화 상황)
            log.debug("[Warning]: Player update failed or initial player set.");
            this.player = newPlayer;
        }
    }

    // 게임 리셋 로직 (Player 리셋 시 Enemy 리스트도 초기화)
    public void reset(Player initialPlayer, Enemy initialEnemy) {
        this.player = initialPlayer;
        this.enemy = initialEnemy;

        log.debug("[Reset] GameState reset. Player ID: {}", this.player.getId());
        log.debug("[Reset] Enemies list cleared");

        // 필요시 여기에 초기 Enemy 생성 로직 추가 가능.

        // 점수 등 다른 요소 초기화
    }

    // 임시: 적군 객체가 필요할까? 어쩌면 차후 판정등을 위하여 필요 가능성이 있어보임
    public Optional<Enemy> findEnemyById(String enemyId) {
        if (this.enemy != null && this.enemy.getId().equals(enemyId)) {
            return Optional.of(this.enemy);
        }

        return Optional.empty();
    }

    // Enemy 상태 업데이트 (ID가 같은 기존 Enemy를 찾아 제거하고 newEnemy 추가)
    public void updateEnemy(Enemy newEnemy) {
        Objects.requireNonNull(newEnemy, "newEnemy must be not null");

        // 현재는 플레이어가 한 명이므로 ID 비교 후 교체 (나중에는 ID로 찾아야 함)
        if (this.enemy != null && this.player.getId().equals(newEnemy.getId())) {
            this.enemy = newEnemy;
        } else {
            // ID가 다르거나 기존 플레이어가 없는 경우 (오류/초기화 상황)
            log.debug("[Warning]: Enemy update failed or initial player set.");
            this.enemy = newEnemy;
        }
    }

    // Getter
    public Player getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }
}
