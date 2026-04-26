package com.film.service;

import com.film.dto.CategoryRequestDTO;
import com.film.dto.CategoryResponseDTO;
import com.film.entity.Category;
import com.film.exception.CategoryNotFoundException;
import com.film.exception.EmptyResultException;
import com.film.repository.CategoryRepository;
import com.film.services.CategoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setup() {
        category = Category.builder()
                .categoryId(1)
                .name("Action")
                .build();
    }

    @Test
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCategoryId()).isEqualTo(1);
    }

    @Test
    void testGetAllCategories_Empty() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> categoryService.getAllCategories())
                .isInstanceOf(EmptyResultException.class);
    }

    @Test
    void testGetCategoryById() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        CategoryResponseDTO result = categoryService.getCategoryById(1);

        assertThat(result.getCategoryId()).isEqualTo(1);
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(1))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void testCreateCategory() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Action");

        when(categoryRepository.existsByName("Action")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDTO result = categoryService.createCategory(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Action");
    }

    @Test
    void testCreateCategory_AlreadyExists() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Action");

        when(categoryRepository.existsByName("Action")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testReplaceCategory() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Drama");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Drama")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDTO result = categoryService.replaceCategory(1, request);

        assertThat(result).isNotNull();
    }

    @Test
    void testReplaceCategory_NotFound() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Drama");

        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.replaceCategory(1, request))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void testReplaceCategory_NameExists() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Action");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Action")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.replaceCategory(1, request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}