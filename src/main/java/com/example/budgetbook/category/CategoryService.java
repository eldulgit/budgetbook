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
        return categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
    }

    public boolean deleteCategory(Long id) {
        if (recordRepository.existsByCategoryId(id)) {
            return false;
        }
        categoryRepository.deleteById(id);
        return true;
    }

    public void updateCategory(Long id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("카테고리 이름을 입력해주세요.");
        }
        Category category = findById(id);
        if (!category.getName().equals(name)
                && categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }
        category.setName(name);
        categoryRepository.save(category);
    }
}

