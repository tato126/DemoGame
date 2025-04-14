package com.example.demo.core.user.domain.support;

import com.example.demo.core.user.domain.PlayerId;
import com.example.demo.core.user.domain.PlayerIdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDPlayerIdGenerator implements PlayerIdGenerator {


    @Override
    public PlayerId generateId() {
        return new PlayerId(UUID.randomUUID().toString());
    }
}
