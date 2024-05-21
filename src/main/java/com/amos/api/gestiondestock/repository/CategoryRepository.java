package com.amos.api.gestiondestock.repository;

import java.util.Optional;

import com.amos.api.gestiondestock.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

  Optional<Category> findCategoryByCode(String code);

}
