package com.example.demo.con;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShutdownController.class)
class ShutdownControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shutdownEndpoint_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/exit"))
                .andExpect(status().isOk());
    }

}