package com.tdcpoa.controller;

import com.tdcpoa.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @GetMapping
    public Item getItem() {
        logger.debug("ItemController.getItem() called");
        Item item = new Item(
            1L,
            "Sample Item",
            "This is a hardcoded sample item description",
            LocalDateTime.now()
        );
        logger.debug("Returning item: {}", item);
        return item;
    }
}
