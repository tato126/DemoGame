package com.example.demo.domain.enemy.application;

import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.enemy.EnemyId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultEnemyManager implements EnemyRegistry, EnemyFind, EnemyCleanUp {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<EnemyId, Enemy> enemies = new ConcurrentHashMap<>();

    @Override
    public void addOrUpdate(Enemy enemy) {
        Objects.requireNonNull(enemy, "Enemy cannot be null for addOrUpdate");
        Objects.requireNonNull(enemy.getId(), "Enemy Id cannot be null for addOrUpdate");

        enemies.put(enemy.getId(), enemy);
        log.debug("[EnemyManager] Enemy added or updated: {}", enemy.getId());
    }

    @Override
    public Optional<Enemy> findById(EnemyId enemyId) {
        Objects.requireNonNull(enemyId, "Enemy ID cannot be null for findById");
        return Optional.ofNullable(enemies.get(enemyId));
    }

    @Override
    public Collection<Enemy> findAll() {
        return Collections.unmodifiableCollection(enemies.values());
    }

    @Override
    public void remove(EnemyId enemyId) {
        Objects.requireNonNull(enemyId, "Enemy ID cannot be null for remove");
        Enemy removedEnemy = enemies.remove(enemyId);
        if (removedEnemy != null) {
            log.debug("[EnemyManager] Enemy removed: {}", enemyId);
        } else {
            log.warn("[EnemyManager] Attempted to remove non-existent enemy: {}", enemyId);
        }
    }

    @Override
    public void clearAll() {
        int count = enemies.size();
        enemies.clear();
        log.debug("[EnemyManager] All enemies cleared ({} enemies removed).", count);
    }


}
