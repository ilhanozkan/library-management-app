package com.ilhanozkan.libraryManagementSystem.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookQuantityUpdateDTO {
    @NotNull(message = "New available quantity is required")
    private Integer availableQuantity;
} 