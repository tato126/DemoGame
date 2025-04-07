package com.example.demo.con;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShutdownController {

    @PostMapping("/exit")
    public void shutdown() {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
                System.exit(0);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        });
        thread.setDaemon(false);
        thread.start();
    }
}
