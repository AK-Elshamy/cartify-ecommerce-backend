package com.elshamy.cartify.service.category;

import com.elshamy.cartify.dtos.CategoryDTO;
import com.elshamy.cartify.exception.ResourceNotFoundException;
import com.elshamy.cartify.model.Category;
import com.elshamy.cartify.repository.CategoryRepository;
import com.elshamy.cartify.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CategoryDTO.CategoryDTOResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryDTO.CategoryDTOResponse::from)
                .toList();
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category with id " + categoryId + " not found")
        );
    }

    @Override
    public CategoryDTO.CategoryProductsResponse getCategoryById(Long categoryId, Pageable pageable) {
        var category = getCategory(categoryId);

        var products = productRepository.findByCategory_Id(categoryId, pageable);

        return CategoryDTO.CategoryProductsResponse.from(category, products);
    }

    @Override
    public CategoryDTO.CategoryDTOResponse addCategory(CategoryDTO.CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        return CategoryDTO.CategoryDTOResponse.from(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO.CategoryDTOResponse updateCategory(Long categoryId, CategoryDTO.UpdateCategoryRequest request) {
        var category = getCategory(categoryId);
        category.setName(request.name());
        return CategoryDTO.CategoryDTOResponse.from(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        var category = getCategory(categoryId);
        if (productRepository.existsByCategoryId(categoryId)) {
            throw new IllegalArgumentException(
                    "Cannot delete category because it contains products"
            );
        }
        categoryRepository.delete(category);
    }
}