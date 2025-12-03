package com.tdcpoa.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.tdcpoa.model.Item;

@Repository
public class ItemRepository {

    private final List<Item> items = new ArrayList<>();

    private final AtomicLong counter = new AtomicLong(0);

    public ItemRepository() {
        addItem(new Item(null, "Item 1", "Description 1", LocalDateTime.now()));
        addItem(new Item(null, "Item 2", "Description 2", LocalDateTime.now()));
        addItem(new Item(null, "Item 3", "Description 3", LocalDateTime.now()));
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        Long nextId = counter.incrementAndGet();
        item.setId(nextId);
        items.add(item);
    }

    public void deleteItem(Long id) {
        items.removeIf(item -> item.getId().equals(id));
    }

}
