package com.example.demo.domain.enemy;

import java.util.Objects;

public class EnemyId {

    private String value;

    public EnemyId(String value) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException("[Exception] EnemyId must not be null or blank");
        }
        this.value = value;
    }

    // for hibernate
    @SuppressWarnings("unused")
    public EnemyId() {
    }

    public static EnemyId of(String value) {
        return new EnemyId(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        return Objects.equals(value, ((EnemyId) obj).value);
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
