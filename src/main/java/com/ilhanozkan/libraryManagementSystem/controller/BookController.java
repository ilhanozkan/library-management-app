package com.ilhanozkan.libraryManagementSystem.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book Operations")
@RestController
@RequestMapping("/books")
public class BookController {
}
