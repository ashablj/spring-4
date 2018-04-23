package org.my.ws.domain.repository;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.my.ws.domain.tables.Author.AUTHOR;
import static org.my.ws.domain.tables.Book.BOOK;

//@Repository
public class JooqBooksRepository implements BooksRepository {

    private final DSLContext dsl;
    private final JdbcTemplate jdbc;

    @Autowired
    public JooqBooksRepository(DSLContext dsl, JdbcTemplate jdbc) {
        this.dsl = dsl;
        this.jdbc = jdbc;
    }

/*    @Autowired
    public JooqBooksRepository(DefaultDSLContext jooq) {
        this.dsl = jooq;
    }*/

    @Override
    public List<String> findAll() {
        Result<Record> results = this.dsl.select().from(AUTHOR).fetch();
        for (Record result : results) {
            Integer id = result.getValue(AUTHOR.ID);
            String firstName = result.getValue(AUTHOR.FIRST_NAME);
            String lastName = result.getValue(AUTHOR.LAST_NAME);

            System.out.println("jOOQ Fetch " + id + " " + firstName + " " + lastName);
        }

        List<String> list = results.map(
                val -> new StringBuffer()
                        .append(val.getValue(AUTHOR.ID))
                        .append("\t")
                        .append(val.getValue(AUTHOR.FIRST_NAME))
                        .append("\t")
                        .append(val.getValue(AUTHOR.LAST_NAME))
                        .toString())
                .stream()
                .collect(Collectors.toList());

        System.out.println("jOOQ SQL result: " + list);
        return list;
    }

    @Override
    public List<String> findOne(int id) {
        Query query = this.dsl.select(BOOK.TITLE, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .from(BOOK).join(AUTHOR).on(BOOK.AUTHOR_ID.equal(AUTHOR.ID))
                .where(BOOK.PUBLISHED_IN.equal(id));//2015

        Object[] bind = query.getBindValues().toArray(new Object[]{});

        List<String> list = this.jdbc.query(query.getSQL(), bind,
                (rs, rowNum) -> rs.getString(1)
                        + " : "
                        + rs.getString(2)
                        + " "
                        + rs.getString(3));

        System.out.println("jOOQ SQL result: " + list);
        return list;
    }

    @Override
    public List<String> save(String book) {
        return null;
    }

    @Override
    public List<String> update(int id, String book) {
        return null;
    }

    @Override
    public List<String> delete(int id) {
        return null;
    }
}
