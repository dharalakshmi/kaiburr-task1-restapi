package com.kaiburr.dto;

public class ExecuteRequest {
    private String command;

    public ExecuteRequest() {}
    public ExecuteRequest(String command) { this.command = command; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
}
