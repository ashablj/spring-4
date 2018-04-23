package org.my.ws.controller;

import org.my.ws.domain.repository.BooksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BooksController.class);

    @Autowired
    private BooksRepository booksRepository;

    //    http://localhost:9000/books/3
    @RequestMapping(method = RequestMethod.GET)
    public Collection<String> getAll() {
        List<String> all = booksRepository.findAll();
        LOGGER.debug("Rendering all values {}", all);
        return all;
    }

    //    http://localhost:9000/books/3
    @RequestMapping(method = RequestMethod.GET, value = "{id}")
    public Collection<String> getById(@PathVariable int id) {
        List<String> all = booksRepository.findOne(id);
        LOGGER.debug("Rendering all values {}", all);
        return all;
    }

    //    http://localhost:9000/books       body=sadfsdgfsa
    @RequestMapping(method = RequestMethod.POST)
    public List<String> create(@RequestBody @Valid final String book) {
        return booksRepository.save(book);
    }

    //    http://localhost:9000/books/3
    @RequestMapping(method = RequestMethod.PUT, value = "{id}")
    public List<String> update(@PathVariable int id, @RequestBody @Valid final String book) {
        return booksRepository.update(id, book);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{id}")
    public List<String> delete(@PathVariable int id) {
        return booksRepository.delete(id);
    }
}