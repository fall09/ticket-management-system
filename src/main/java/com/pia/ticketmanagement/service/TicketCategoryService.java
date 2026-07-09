package com.pia.ticketmanagement.service;

import com.pia.ticketmanagement.dto.request.CreateCategoryRequest;
import com.pia.ticketmanagement.dto.request.CreateSubCategoryRequest;
import com.pia.ticketmanagement.dto.request.UpdateSubCategoryRequest;
import com.pia.ticketmanagement.dto.response.CategoryResponse;
import com.pia.ticketmanagement.dto.response.SubCategoryResponse;
import com.pia.ticketmanagement.exception.NotFoundException;
import com.pia.ticketmanagement.model.TicketCategory;
import com.pia.ticketmanagement.model.TicketSubCategory;
import com.pia.ticketmanagement.repository.TicketCategoryRepository;
import com.pia.ticketmanagement.repository.TicketSubCategoryRepository;
import com.pia.ticketmanagement.dto.response.TicketSubCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
                .defaultPriority(subCategory.getDefaultPriority())
                .availableForNewCustomer(subCategory.isAvailableForNewCustomer())
                .build();
    }
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        TicketCategory category = TicketCategory.builder()
                .name(request.getName())
                .build();

        TicketCategory saved = categoryRepository.save(category);

        return CategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .subCategoryCount(0L)
                .build();
    }

    public SubCategoryResponse createSubCategory(Long categoryId, CreateSubCategoryRequest request) {
        TicketCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found."));

        TicketSubCategory subCategory = TicketSubCategory.builder()
                .name(request.getName())
                .locationRequired(request.isLocationRequired())
                .defaultPriority(request.getDefaultPriority())
                .availableForNewCustomer(request.isAvailableForNewCustomer())
                .category(category)
                .build();

        TicketSubCategory saved = subCategoryRepository.save(subCategory);

        return mapToSubCategoryResponse(saved);
    }
    public SubCategoryResponse updateSubCategory(Long subCategoryId, UpdateSubCategoryRequest request) {
        TicketSubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new NotFoundException("Sub category not found."));

        subCategory.setName(request.getName());
        subCategory.setLocationRequired(request.isLocationRequired());
        subCategory.setDefaultPriority(request.getDefaultPriority());
        subCategory.setAvailableForNewCustomer(request.isAvailableForNewCustomer());

        TicketSubCategory saved = subCategoryRepository.save(subCategory);

        return mapToSubCategoryResponse(saved);
    }
    public List<TicketSubCategoryResponse> getAllSubCategories() {
        return subCategoryRepository.findAll()
                .stream()
                .map(sub -> TicketSubCategoryResponse.builder()
                        .id(sub.getId())
                        .name(sub.getName())
                        .categoryId(sub.getCategory().getId())
                        .categoryName(sub.getCategory().getName())
                        .locationRequired(sub.isLocationRequired())
                        .defaultPriority(sub.getDefaultPriority())
                        .availableForNewCustomer(sub.isAvailableForNewCustomer())
                        .build())
                .toList();
    }

    public List<SubCategoryResponse> getSubCategoriesForNewCustomerByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Category not found.");
        }

        return subCategoryRepository
                .findByCategoryIdAndAvailableForNewCustomerTrue(categoryId)
                .stream()
                .map(this::mapToSubCategoryResponse)
                .toList();
    }
}