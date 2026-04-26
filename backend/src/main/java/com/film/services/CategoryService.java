package com.film.services;

import com.film.dto.CategoryRequestDTO;
import com.film.dto.CategoryResponseDTO;
import com.film.entity.Category;
import com.film.exception.CategoryNotFoundException;
import com.film.exception.EmptyResultException;
import com.film.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    private CategoryResponseDTO toDTO(Category category) {
        return CategoryResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .build();
    }

    public Page<CategoryResponseDTO> findAllPaginated(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size))
                .map(this::toDTO);
    }

    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new EmptyResultException("No categories found in the database.");
        }
        return categories.stream().map(this::toDTO).toList();
    }

    public CategoryResponseDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return toDTO(category);
    }

    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with name " + request.getName() + " already exists");
        }
        Category category = Category.builder().name(request.getName()).build();
        return toDTO(categoryRepository.save(category));
    }

    public CategoryResponseDTO replaceCategory(Integer id, @Valid CategoryRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Category already exists with name: " + request.getName());
        }
        category.setName(request.getName());
        return toDTO(categoryRepository.save(category));
    }
}