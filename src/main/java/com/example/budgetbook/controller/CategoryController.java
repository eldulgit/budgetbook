package com.example.budgetbook.controller;

import com.example.budgetbook.category.Category;
import com.example.budgetbook.category.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String category(Model model) {

        model.addAttribute("categories", categoryService.findAll());
        return "category";
    }

    @PostMapping("/categories")
    public String saveCategory(@ModelAttribute Category category,
                               RedirectAttributes redirectAttributes
                               ) {
        boolean result = categoryService.addCategory(category);
        if(!result) {
            redirectAttributes.addFlashAttribute("message","이미 존재하는 카테고리 입니다.");
        }
        return "redirect:/categories";
    }

    @PostMapping("/categories/delete")
    public String deleteCategory(Long id, RedirectAttributes redirectAttributes) {
        boolean result = categoryService.deleteCategory(id);
        if(!result) {
            redirectAttributes.addFlashAttribute("message", "사용 중인 카테고리는 삭제할 수 없습니다.");
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit")
    public String editCategory(Long id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "edit-category";
    }

    @PostMapping("/categories/update")
    public String updateCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        categoryService.updateCategory(category.getId(), category.getName());
        redirectAttributes.addFlashAttribute("message", "카테고리가 수정되었습니다");
        return "redirect:/categories";
    }
}
