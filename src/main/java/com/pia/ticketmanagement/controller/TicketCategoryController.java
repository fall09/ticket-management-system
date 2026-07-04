package com.pia.ticketmanagement.controller;

import com.pia.ticketmanagement.dto.response.CategoryResponse;
import com.pia.ticketmanagement.dto.response.SubCategoryResponse;
import com.pia.ticketmanagement.service.TicketCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TicketCategoryController {

    private final TicketCategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}/sub-categories")
    public List<SubCategoryResponse> getSubCategoriesByCategoryId(@PathVariable Long id) {
        return categoryService.getSubCategoriesByCategoryId(id);
    }
}