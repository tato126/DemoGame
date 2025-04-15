package com.example.demo.core.user.domain.player;

import java.util.Objects;

public class PlayerId {

    private String value;

    public PlayerId(String value) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException("[Exception] PlayerId is must not be null or blank");
        }
        this.value = value;
    }

    // for hibernate
    @SuppressWarnings("unused")
    private PlayerId() {
    }

    public static PlayerId of(String value) {
        return new PlayerId(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return Objects.equals(value, ((PlayerId) obj).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
