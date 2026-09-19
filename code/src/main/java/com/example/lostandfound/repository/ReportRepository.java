package com.example.lostandfound.repository;

import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.domain.enums.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    @Query("""
            SELECT DISTINCT r FROM Report r
            LEFT JOIN r.tags t
            WHERE (:type IS NULL OR r.type = :type)
              AND (:status IS NULL OR r.status = :status)
              AND (:categoryId IS NULL OR r.category.id = :categoryId)
              AND (:tagName IS NULL OR LOWER(t.tagName) = LOWER(:tagName))
              AND (:keyword IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Report> search(@Param("type") ReportType type,
                         @Param("status") ReportStatus status,
                         @Param("categoryId") Long categoryId,
                         @Param("tagName") String tagName,
                         @Param("keyword") String keyword,
                         Pageable pageable);
}