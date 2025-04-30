package com.example.demo.domain.projectile;

import java.util.Objects;

public class ProjectileId {

    private String value;

    public ProjectileId(String value) {
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("[Exception] ProjectileId must be not null or Blank.");
        }
        this.value = value;
    }

    // for hibernate
    @SuppressWarnings("unused")
    public ProjectileId() {
    }

    public static ProjectileId of(String value) {
        return new ProjectileId(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        return Objects.equals(value, ((ProjectileId) obj).value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
