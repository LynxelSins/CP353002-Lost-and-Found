package com.example.lostandfound.repository;

import com.example.lostandfound.domain.entity.ReportImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {
}