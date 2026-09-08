package com.example.budgetbook.category;

import com.example.budgetbook.record.RecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final RecordRepository recordRepository;

    public CategoryService(CategoryRepository categoryRepository, RecordRepository recordRepository) {
        this.categoryRepository = categoryRepository;
        this.recordRepository = recordRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public boolean addCategory(Category category) {
        if(categoryRepository.existsByName(category.getName())) {
            return false;
        }
        categoryRepository.save(category);
        return true;
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public boolean deleteCategory(Long id) {
        if (recordRepository.existsByCategoryId(id)) {
            return false;
        }
        categoryRepository.deleteById(id);
        return true;
    }
}

