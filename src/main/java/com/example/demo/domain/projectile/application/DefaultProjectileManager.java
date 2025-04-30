package com.example.demo.domain.projectile.application;

import com.example.demo.domain.projectile.Projectile;
import com.example.demo.domain.projectile.ProjectileId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultProjectileManager implements ProjectileCleanUp, ProjectileFind, ProjectileRegistry {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<ProjectileId, Projectile> projectiles = new ConcurrentHashMap<>();

    @Override
    public void addOrUpdate(Projectile projectile) {
        Objects.requireNonNull(projectile, "Projectile must not be null");
        Objects.requireNonNull(projectile.getId(), "ProjectileId must not be null.");

        projectiles.put(projectile.getId(), projectile);
        log.debug("[ProjectileManager] Projectile added or update: {}", projectile.getId());
    }

    @Override
    public Optional<Projectile> findById(ProjectileId projectileId) {
        Objects.requireNonNull(projectileId, "ProjectileId must not be null.");
        return Optional.ofNullable(projectiles.get(projectileId));
    }

    @Override
    public Collection<Projectile> findAll() {
        return Collections.unmodifiableCollection(projectiles.values());
    }

    @Override
    public void remove(ProjectileId projectileId) {
        Objects.requireNonNull(projectileId, "ProjectileId must not be null");

        Projectile removeProjectile = projectiles.remove(projectileId);

        if (removeProjectile != null) {
            log.debug("[ProjectileManager] Projectile removed: {}", removeProjectile);
        } else {
            log.debug("[ProjectileManager] Attempted to remove non-existent projectile: {}", projectileId);
        }
    }

    @Override
    public void clearAll() {
        int count = projectiles.size();
        projectiles.clear();
        log.debug("[ProjectileManager] All projectiles cleared ({} projectiles removed)", count);
    }
}
