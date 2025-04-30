package com.example.demo.domain.projectile.application;

import com.example.demo.domain.projectile.Projectile;
import com.example.demo.domain.projectile.ProjectileId;

import java.util.Collection;
import java.util.Optional;

public interface ProjectileFind {

    Optional<Projectile> findById(ProjectileId projectileId);

    Collection<Projectile> findAll();
}
