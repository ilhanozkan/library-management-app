package com.ilhanozkan.libraryManagementSystem.controller;

import com.ilhanozkan.libraryManagementSystem.model.enums.BookGenre;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enums")
@Tag(name = "Enum Values", description = "APIs for retrieving enum values used in the application")
public class EnumController {

    @Operation(summary = "Get all book genres", description = "Retrieves a list of all valid book genres")
    @GetMapping("/book-genres")
    public ResponseEntity<List<String>> getAllBookGenres() {
        List<String> genres = Arrays.stream(BookGenre.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(genres);
    }
} 