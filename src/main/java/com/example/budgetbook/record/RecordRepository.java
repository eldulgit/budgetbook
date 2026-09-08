package com.example.budgetbook.record;

import com.example.budgetbook.domain.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface RecordRepository extends JpaRepository<Record, Long> {

    boolean existsByCategoryId(Long categoryId);

    List<Record> findByType(RecordType type);

    List<Record> findByTypeOrderByDateAsc(RecordType type);

    List<Record> findAllByOrderByDateDesc();

    @Query("""
            SELECT r
            FROM  Record r
            WHERE (:startDate IS NULL OR r.date >= :startDate)
              AND (:endDate IS NULL OR r.date <= :endDate)
              AND (:type IS NULL OR r.type = :type)
              AND (:categoryId IS NULL OR r.category.id = :categoryId)
              AND (:keyword IS NULL OR r.title LIKE CONCAT('%', :keyword, '%'))
            ORDER BY r.date DESC
            """)
    Page<Record> searchRecords(@Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate,
                               @Param("type") RecordType type,
                               @Param("categoryId") Long categoryId,
                               @Param("keyword") String keyword,
                               Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0)
            FROM Record r
            WHERE r.date BETWEEN :startDate AND :endDate
            AND r.type = :type
            """)
    int sumAmountByDateBetweenAndType(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("type") RecordType type);

    @Query("""
            SELECT r.category.name, COALESCE(SUM(r.amount), 0)
            FROM Record r
            WHERE r.date BETWEEN :startDate AND :endDate
              AND r.type = :type
            GROUP BY r.category.name
            ORDER BY COALESCE(SUM(r.amount), 0) DESC
            """)
    List<Object[]> categoryExpense(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("type") RecordType type);
}
