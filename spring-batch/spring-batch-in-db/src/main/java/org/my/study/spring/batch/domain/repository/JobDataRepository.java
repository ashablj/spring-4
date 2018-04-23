package org.my.study.spring.batch.domain.repository;

/**
 */
public interface JobDataRepository<V> {

    void put(String key, V value);

    V get(String key);
}
