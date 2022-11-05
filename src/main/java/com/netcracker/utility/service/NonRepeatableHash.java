package com.netcracker.utility.service;

public record NonRepeatableHash(String hash) {
    public String value() {
        return hash;
    }
}
