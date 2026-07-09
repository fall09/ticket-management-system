package com.pia.ticketmanagement.controller;

import com.pia.ticketmanagement.dto.request.CreateCategoryRequest;
import com.pia.ticketmanagement.dto.request.CreateSubCategoryRequest;
import com.pia.ticketmanagement.dto.request.UpdateSubCategoryRequest;
import com.pia.ticketmanagement.dto.response.CategoryResponse;
import com.pia.ticketmanagement.dto.response.SubCategoryResponse;
import com.pia.ticketmanagement.dto.response.TicketSubCategoryResponse;
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

    @GetMapping("/sub-categories")
    public List<TicketSubCategoryResponse> getAllSubCategories() {
        return categoryService.getAllSubCategories();
    }
    @PostMapping
    public CategoryResponse createCategory(@RequestBody CreateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @PostMapping("/{id}/sub-categories")
    public SubCategoryResponse createSubCategory(
            @PathVariable Long id,
            @RequestBody CreateSubCategoryRequest request
    ) {
        return categoryService.createSubCategory(id, request);
    }
    @PutMapping("/sub-categories/{id}")
    public SubCategoryResponse updateSubCategory(
            @PathVariable Long id,
            @RequestBody UpdateSubCategoryRequest request
    ) {
        return categoryService.updateSubCategory(id, request);
    }


    @GetMapping("/{id}/sub-categories/new-customer")
    public List<SubCategoryResponse> getSubCategoriesForNewCustomerByCategoryId(
            @PathVariable Long id
    ) {
        return categoryService.getSubCategoriesForNewCustomerByCategoryId(id);
    }
}