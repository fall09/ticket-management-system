package com.pia.ticketmanagement.service;

import com.pia.ticketmanagement.dto.response.CategoryResponse;
import com.pia.ticketmanagement.dto.response.SubCategoryResponse;
import com.pia.ticketmanagement.exception.NotFoundException;
import com.pia.ticketmanagement.model.TicketSubCategory;
import com.pia.ticketmanagement.repository.TicketCategoryRepository;
import com.pia.ticketmanagement.repository.TicketSubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketCategoryService {

    private final TicketCategoryRepository categoryRepository;
    private final TicketSubCategoryRepository subCategoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .subCategoryCount(subCategoryRepository.countByCategoryId(category.getId()))
                        .build())
                .toList();
    }

    public List<SubCategoryResponse> getSubCategoriesByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Category not found.");
        }

        return subCategoryRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToSubCategoryResponse)
                .toList();
    }

    private SubCategoryResponse mapToSubCategoryResponse(TicketSubCategory subCategory) {
        return SubCategoryResponse.builder()
                .id(subCategory.getId())
                .name(subCategory.getName())
                .locationRequired(subCategory.isLocationRequired())
                .build();
    }
}