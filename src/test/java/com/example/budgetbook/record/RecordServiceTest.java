package com.example.budgetbook.record;

import com.example.budgetbook.category.CategoryRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private RecordService recordService;




}
