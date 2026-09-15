package com.example.budgetbook.category;

import com.example.budgetbook.record.RecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RecordRepository recordRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test // 빈 이름으로 수정 시 예외 발생
    void updateCategoryWithBlankThrowsException(){
        assertThrows(IllegalArgumentException.class, ()->{
            categoryService.updateCategory(1L," ");
        });
    }

    @Test // 새로운 이름으로 정상 수정
    void updateCategoryWithValidName() {
        Category category = new Category();
        category.setName("식비");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("외식비")).thenReturn(false);

        categoryService.updateCategory(1L,"외식비");

        assertEquals("외식비",category.getName());
        verify(categoryRepository).save(category);
    }

    @Test // 중복 이름으로 수정 시 예외 발생
    void updateCategoryWithDuplicateNameThrowsException(){
        Category category = new Category();
        category.setName("식비");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("교통비")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, ()->{
            categoryService.updateCategory(1L,"교통비");
        });
        verify(categoryRepository,never()).save(any(Category.class));
        assertEquals("식비",category.getName());
    }

    @Test // 기존 이름 그대로 수정 시 정상 저장
    void updateCategoryWithSameNameSavesSuccessfully(){
        Category category = new Category();
        category.setName("식비");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        categoryService.updateCategory(1L,"식비");

        assertEquals("식비",category.getName());
        verify(categoryRepository).save(category);
    }

    @Test // null 이름으로 수정 시 예외 발생
    void updateCategoryWithNullNameThrowsException(){
        assertThrows(IllegalArgumentException.class, ()->{
            categoryService.updateCategory(1L,null);
        });

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test // 빈 문자열로 수정 시 예외 발생 및 저장 방지
    void updateCategoryWithEmptyNameThrowsException(){
        assertThrows(IllegalArgumentException.class, ()->{
           categoryService.updateCategory(1L,"");
        });

        verify(categoryRepository,never()).save(any(Category.class));
    }

    @Test // 존재하지 않는 ID로 수정 시 예외 발생
    void updateCategoryWithNonexistentIdThrowsException(){
        Category category = new Category();
        category.setName("식비");
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, ()->{
            categoryService.updateCategory(1L,"식비");
        });
        verify(categoryRepository,never()).save(any(Category.class));
    }

    @Test // 존재하는 ID로 카테고리 조회
    void findByIdReturnsCategory(){
        Category category = new Category();
        category.setName("식비");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Category result = categoryService.findById(1L);

        assertSame(category,result);
    }

    @Test // 존재하지 않는 ID 조회 시 예외 발생
    void findByIdWithNonexistentIdThrowsException(){
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, ()->{
           categoryService.findById(1L);
        });
    }

    @Test // 새로운 이름으로 카테고리 등록
    void addCategoryWithNewNameSavesSuccessfully(){
        Category category = new Category();
        category.setName("식비");

        //db에는 없다고 가정
        when(categoryRepository.existsByName("식비")).thenReturn(false);
        boolean result = categoryService.addCategory(category);

        assertTrue(result);
        verify(categoryRepository).save(category);
    }

    @Test // 중복 이름으로 등록
    void addCategoryWithDuplicateNameReturnsFalse(){
        Category category = new Category();
        category.setName("식비");

        when(categoryRepository.existsByName("식비")).thenReturn(true);
        boolean result = categoryService.addCategory(category);

        assertFalse(result);
        verify(categoryRepository,never()).save(any(Category.class));
    }

    @Test // 사용중이 카테고리 삭제 불가
    void deleteCategoryInUseReturnsFalse(){
        when(recordRepository.existsByCategoryId(1L)).thenReturn(true);
        boolean result = categoryService.deleteCategory(1L);
        assertFalse(result);
        verify(recordRepository,never()).existsByCategoryId(1L);
    }

    @Test // 사용하지 않는 카테고리 삭제 가능
    void deleteCategoryNotInUseReturnsTrue(){
        when(recordRepository.existsByCategoryId(1L)).thenReturn(false);
        boolean result = categoryService.deleteCategory(1L);
        assertTrue(result);
        verify(recordRepository).existsByCategoryId(1L);
    }

}
