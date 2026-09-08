package com.example.budgetbook.record;

import com.example.budgetbook.domain.RecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class RecordForm {

    private Long id;

    @NotNull(message = "날짜를 입력해 주세요")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @NotBlank(message = "제목을 입력해 주세요")
    private String title;
    @NotNull(message = "금액을 입력해 주세요")
    @Positive(message = "금액은 0보다 커야합니다")
    private Integer amount;
    @NotNull(message = "카테고리를 선택해주세요")
    private Long categoryId;
    @NotNull(message = "지출/수입을 선택해주세요")
    private RecordType type;

    public RecordForm() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public RecordType getType() {
        return type;
    }

    public void setType(RecordType type) {
        this.type = type;
    }
}
