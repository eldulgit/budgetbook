package com.example.budgetbook.record;

import com.example.budgetbook.category.Category;
import com.example.budgetbook.category.CategoryRepository;
import com.example.budgetbook.domain.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final CategoryRepository categoryRepository;

    public RecordService(RecordRepository recordRepository, CategoryRepository categoryRepository) {
        this.recordRepository = recordRepository;
        this.categoryRepository = categoryRepository;
    }

    private LocalDate[] getCurrentMonthRange(){
        LocalDate now =  LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        return new LocalDate[]{startDate,endDate};
    }

    public List<Record> findAll() {
        return recordRepository.findAllByOrderByDateDesc();
    }

    public void addRecord(RecordForm recordForm) {

        Record record = new Record();
        Category category = categoryRepository.findById(recordForm.getCategoryId())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 카테고리입니다"));

        record.setDate(recordForm.getDate());
        record.setTitle(recordForm.getTitle());
        record.setAmount(recordForm.getAmount());
        record.setCategory(category);
        record.setType(recordForm.getType());

        recordRepository.save(record);
    }

    public Record findById(Long id) {
        return recordRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지 않는 내역입니다"));
    }

    public void deleteRecord(Long id) {
        recordRepository.deleteById(id);
    }

    public void editRecord(RecordForm recordForm) {

        Record findRecord = findById(recordForm.getId());
        Category category = categoryRepository.findById(recordForm.getCategoryId())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 카테고리입니다"));
        findRecord.setDate(recordForm.getDate());
        findRecord.setTitle(recordForm.getTitle());
        findRecord.setAmount(recordForm.getAmount());
        findRecord.setCategory(category);
        findRecord.setType(recordForm.getType());

        recordRepository.save(findRecord);
    }

    public List<Record> findExpenseRecordsOrderByDate() {
        return recordRepository.findByTypeOrderByDateAsc(RecordType.EXPENSE);
    }

    public Map<String, Integer> getMonthlyExpense() {
        List<Record> expenseRecords = findExpenseRecordsOrderByDate();

        Map<String, Integer> monthlyExpenses = new LinkedHashMap<>();

        LocalDate now = LocalDate.now();

        LocalDate startMonth = now.minusMonths(4).withDayOfMonth(1);

        for (Record record : expenseRecords) {

            if (record.getDate().isBefore(startMonth)) {
                continue;
            }

            String month = record.getDate().getYear() + "-" + record.getDate().getMonthValue();
            int amount =record.getAmount();

            if (monthlyExpenses.containsKey(month)) {
                int currentAmount = monthlyExpenses.get(month);
                monthlyExpenses.put(month, currentAmount + amount);
            } else {
                monthlyExpenses.put(month, amount);
            }

        }
        return monthlyExpenses;
    }

    public Map<String, Integer> getDailyExpense() {
        List<Record> expenseRecords = findExpenseRecordsOrderByDate();

        Map<String, Integer> dailyExpenses = new LinkedHashMap<>();

        LocalDate[] monthRange = getCurrentMonthRange();
        LocalDate startDate = monthRange[0];
        LocalDate endDate = monthRange[1];

        int total = 0;

        for (Record record : expenseRecords) {

            if (record.getDate().isBefore(startDate) || record.getDate().isAfter(endDate)) {
                continue;
            }

            String day = record.getDate().getYear() + "-"
                    + record.getDate().getMonthValue() + "-"
                    + record.getDate().getDayOfMonth();

            int amount = record.getAmount();

            total += amount;
            dailyExpenses.put(day, total);
        }
        return dailyExpenses;
    }

    public int getCurrentMonthIncome(){
        LocalDate[] monthRange = getCurrentMonthRange();
        LocalDate startDate = monthRange[0];
        LocalDate endDate = monthRange[1];

        return recordRepository.sumAmountByDateBetweenAndType(startDate, endDate, RecordType.INCOME);
    }

    public int getCurrentMonthExpense(){
        LocalDate[] monthRange = getCurrentMonthRange();
        LocalDate startDate = monthRange[0];
        LocalDate endDate = monthRange[1];

        return recordRepository.sumAmountByDateBetweenAndType(startDate, endDate, RecordType.EXPENSE);
    }

    public int getCurrentMonthBalance(){
        return getCurrentMonthIncome()-getCurrentMonthExpense();
    }

    public Page<Record> searchRecords(LocalDate startDate,
                                      LocalDate endDate,
                                      RecordType type,
                                      Long categoryId,
                                      String keyword,
                                      Pageable pageable
    ) {
        if(keyword!=null&&keyword.isBlank()){
            keyword=null;
        }
        return recordRepository.searchRecords(startDate, endDate,type, categoryId, keyword, pageable);
    }

    public Map<String, Integer> getCurrentMonthCategoryExpenses() {
        Map<String, Integer> categoryExpenses = new LinkedHashMap<>();

        LocalDate[] monthRange = getCurrentMonthRange();
        LocalDate startDate = monthRange[0];
        LocalDate endDate = monthRange[1];

        List<Object[]> results = recordRepository.categoryExpense(startDate,endDate,RecordType.EXPENSE);

        for (Object[] result : results) {
            String categoryName = (String) result[0];
            Long amount = (Long)result[1];
            categoryExpenses.put(categoryName, amount.intValue());
        }

        return categoryExpenses;
    }
}
