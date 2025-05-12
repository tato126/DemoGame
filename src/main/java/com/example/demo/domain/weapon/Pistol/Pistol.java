package com.example.demo.domain.weapon.Pistol;

import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.projectile.Projectile;
import com.example.demo.domain.projectile.ProjectileId;
import com.example.demo.domain.projectile.application.ProjectileRegistry;
import com.example.demo.domain.support.IdGenerator;
import com.example.demo.domain.weapon.Weapon;
import com.example.demo.infrastructure.config.properties.PistolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Pistol implements Weapon {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final IdGenerator idGenerator;
    private final ProjectileRegistry registry;
    private final PistolProperties pistolProperties;

    public Pistol(IdGenerator idGenerator, ProjectileRegistry registry, PistolProperties pistolProperties) {
        this.idGenerator = idGenerator;
        this.registry = registry;
        this.pistolProperties = pistolProperties;
    }

    @Override
    public void shoot(Object ownerId, Position startPosition, Direction targetDirection) {
        // TODO: 여기에 무기 관련 로직 추가 가능 (예: 쿨다운 체크, 탄약체크)

        log.debug("Player/Enemy {} shooting from {} towards {}", ownerId, startPosition, targetDirection);

        ProjectileId projectileId = idGenerator.generatedProjectileId();

        int projectileSize = pistolProperties.size();
        int projectileSpeed = pistolProperties.speed(); // 초당 픽셀 이동 속도 또는 업데이트당 이동 거리

        // 투사체 생성 (속도 벡터 혹은 방향과 속력으로 관리)
        Projectile newProjectile = new Projectile(projectileId, ownerId, startPosition, targetDirection, projectileSize, projectileSpeed);
        newProjectile.move();

        registry.addOrUpdate(newProjectile);
        log.debug("Projectile {} created and registered by owner {}", projectileId, ownerId);
    }
}
