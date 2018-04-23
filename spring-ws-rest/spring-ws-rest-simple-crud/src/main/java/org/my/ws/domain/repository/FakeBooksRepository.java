package org.my.ws.domain.repository;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FakeBooksRepository implements BooksRepository {

    private static List<String> booksStorage = Lists.newArrayList(
            "id: 0",
            "id: 1",
            "id: 2",
            "id: 3",
            "id: 4",
            "id: 5",
            "id: 6",
            "id: 7",
            "id: 8",
            "id: 9");

    @Override
    public List<String> findAll() {
        System.out.println("FIND_ALL result: " + booksStorage);
        return booksStorage;
    }

    @Override
    public List<String> findOne(int id) {
        String book = booksStorage.get(id);

        System.out.println("FIND_BY_ID result: " + book);
        return Lists.newArrayList(book);
    }

    @Override
    public List<String> save(String book) {
        booksStorage.add(book);

        System.out.println("SAVE result: " + book);
        return findAll();
    }

    @Override
    public List<String> update(int id, String book) {
        String record = booksStorage.set(id, book);

        System.out.println("UPDATE result: " + record);
        return findAll();
    }

    @Override
    public List<String> delete(int id) {
        booksStorage.remove(id);

        System.out.println("DELETE result: done");
        return findAll();
    }
}
