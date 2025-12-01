package com.tdcpoa.controller;

import com.tdcpoa.model.Item;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @GetMapping
    public Item getItem() {
        return new Item(
            1L,
            "Sample Item",
            "This is a hardcoded sample item description",
            LocalDateTime.now()
        );
    }
}
