package com.dorsetsoftware.PennyPal.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dorsetsoftware.PennyPal.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
