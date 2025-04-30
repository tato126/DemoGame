package com.example.demo.domain.projectile.application;

import com.example.demo.domain.projectile.ProjectileId;

public interface ProjectileCleanUp {

    void remove(ProjectileId projectileId);

    void clearAll();
}
