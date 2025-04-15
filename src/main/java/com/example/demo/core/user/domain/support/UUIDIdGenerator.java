package com.example.demo.core.user.domain.support;

import com.example.demo.core.user.domain.player.PlayerId;
import com.example.demo.core.user.domain.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDIdGenerator implements IdGenerator {


    @Override
    public PlayerId generateId() {
        return new PlayerId(UUID.randomUUID().toString());
    }
}
