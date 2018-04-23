package org.my.study.spring.batch.service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Contains repository in memory of importJob send coupons. <p>
 * It use recommended temporary for tests.
 */
public class InMemoryJobRepository<T> {

    private final ConcurrentMap<String, Collection<T>> jobDataRepository;

    public InMemoryJobRepository() {
        jobDataRepository = new ConcurrentHashMap<>();
    }

    public void put(String key, Collection<T> value) {
        jobDataRepository.put(key, value);
    }

    public Collection<T> get(String key) {
        return jobDataRepository.get(key);
    }
}