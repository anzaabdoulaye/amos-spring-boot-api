package com.amos.api.gestiondestock.repository;

import java.util.List;
import java.util.Optional;

import com.amos.api.gestiondestock.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

  Optional<Article> findArticleByCodeArticle(String codeArticle);

  List<Article> findAllByCategoryId(Integer idCategory);


}
