package com.netcracker.utility.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Profile({"default", "in-memory"})
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
