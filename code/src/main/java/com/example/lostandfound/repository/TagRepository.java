package com.example.lostandfound.repository;

import com.example.lostandfound.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByTagNameIgnoreCase(String tagName);
    Optional<Tag> findByTagNameIgnoreCase(String tagName);
}