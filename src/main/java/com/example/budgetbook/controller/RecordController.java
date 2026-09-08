package com.example.budgetbook.controller;

import com.example.budgetbook.category.CategoryService;
import com.example.budgetbook.domain.RecordType;
import com.example.budgetbook.record.Record;
import com.example.budgetbook.record.RecordForm;
import com.example.budgetbook.record.RecordService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class RecordController {

    private final RecordService recordService;

    private final CategoryService categoryService;

    public RecordController(RecordService recordService, CategoryService categoryService) {
        this.recordService = recordService;
        this.categoryService = categoryService;
    }

    @GetMapping("/records")
    public String records(Model model,
                          @RequestParam(required = false)
                          @DateTimeFormat(pattern = "yyyy-MM-dd")
                          LocalDate startDate,
                          @RequestParam(required = false)
                          @DateTimeFormat(pattern = "yyyy-MM-dd")
                          LocalDate endDate,
                          @RequestParam(required = false)
                          RecordType type,
                          @RequestParam(required = false)
                          Long categoryId,
                          @RequestParam(required = false)
                          String keyword,
                          @PageableDefault(size = 10)
                          Pageable pageable

    ) {

        Page<Record> recordPage;

        recordPage = recordService.searchRecords(startDate,endDate,type,categoryId,keyword,pageable);

        model.addAttribute("records", recordPage.getContent());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("type", type);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("recordPage", recordPage);
        return "records";
    }

    @GetMapping("/records/new")
    public String newRecord(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("recordForm",new RecordForm());
        return "input";
    }

    @PostMapping("/records")
    public String saveRecord(@Valid @ModelAttribute RecordForm recordForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "input";
        }
        recordService.addRecord(recordForm);
        redirectAttributes.addFlashAttribute("message", "내역이 저장되었습니다");
        return "redirect:/records";
    }

    @PostMapping("/records/delete")
    public String deleteRecord(Long id,RedirectAttributes redirectAttributes) {
        recordService.deleteRecord(id);
        redirectAttributes.addFlashAttribute("message","내역이 삭제되었습니다.");
        return "redirect:/records";
    }

    @GetMapping("/records/edit")
    public String editRecord(Long id, Model model) {
        Record record = recordService.findById(id);
        RecordForm recordForm = new RecordForm();
        recordForm.setId(record.getId());
        recordForm.setDate(record.getDate());
        recordForm.setTitle(record.getTitle());
        recordForm.setAmount(record.getAmount());
        recordForm.setCategoryId(record.getCategory().getId());
        recordForm.setType(record.getType());
        model.addAttribute("recordForm", recordForm);
        model.addAttribute("categories", categoryService.findAll());
        return "edit";
    }

    @PostMapping("/records/update")
    public String updateRecord(@Valid @ModelAttribute RecordForm recordForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "edit";
        }
        recordService.editRecord(recordForm);
        redirectAttributes.addFlashAttribute("message", "내역이 수정되었습니다");
        return "redirect:/records";
    }
}
