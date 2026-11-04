package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.CategoryDTO;
import com.elshamy.cartify.service.category.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO.CategoryDTOResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO.CategoryProductsResponse>
    getCategory(@PathVariable Long categoryId, @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId, pageable));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO.CategoryDTOResponse> createCategory(
            @Valid @RequestBody CategoryDTO.CreateCategoryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.addCategory(request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO.CategoryDTOResponse> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDTO.UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }
}