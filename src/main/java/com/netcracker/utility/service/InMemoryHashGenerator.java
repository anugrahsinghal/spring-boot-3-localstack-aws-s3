package com.netcracker.utility.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InMemoryHashGenerator implements HashGenerator {

    AtomicInteger uniqueHash = new AtomicInteger(1_000_000);

    @Override
    public NonRepeatableHash getNonRepeatableHash(String fileName, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NonRepeatableHash getNonRepeatableHash(String fileName) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public NonRepeatableHash getNonRepeatableHash() {
        return new NonRepeatableHash(String.valueOf(uniqueHash.getAndIncrement()));
    }
}
