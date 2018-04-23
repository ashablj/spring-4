package org.my.study.spring.batch.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;

import static java.lang.String.format;

/**
 */
public class DatabaseJobDataRepository<V> implements JobDataRepository<V> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void put(String key, V value) {
        String sql = format("INSERT INTO BATCH_JOB_DATA VALUES (null, '%s', '%s', 0, null);", key, value);
        jdbcTemplate.execute(sql);
    }

    @Override
    public V get(String key) {
        return (V) jdbcTemplate.queryForObject(
                "SELECT * FROM BATCH_JOB_DATA WHERE token='?';",
                new Object[]{key},
                new BeanPropertyRowMapper(Object.class));
    }
}