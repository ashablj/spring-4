package org.my.ws.domain.repository;

import java.util.List;

/**
 */
public interface BooksRepository {

    List<String> findAll();

    List<String> findOne(int id);

    List<String> save(String book);

    List<String> update(int id, String book);

    List<String> delete(int id);
}
