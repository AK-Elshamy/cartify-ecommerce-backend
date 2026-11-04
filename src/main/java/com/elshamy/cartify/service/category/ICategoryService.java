package com.elshamy.cartify.service.category;

import com.elshamy.cartify.dtos.CategoryDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    List<CategoryDTO.CategoryDTOResponse> getAllCategories();

    CategoryDTO.CategoryProductsResponse getCategoryById(Long categoryId, Pageable pageable);

    CategoryDTO.CategoryDTOResponse addCategory(CategoryDTO.CreateCategoryRequest request);

    CategoryDTO.CategoryDTOResponse updateCategory(Long categoryId, CategoryDTO.UpdateCategoryRequest request);

    void deleteCategory(Long categoryId);
}