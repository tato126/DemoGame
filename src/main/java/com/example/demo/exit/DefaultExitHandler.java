package com.example.demo.exit;

public class DefaultExitHandler implements ExitHandler{

    @Override
    public void exit(int status) {
        System.exit(status);
    }
}
