package com.amos.api.gestiondestock.controller;


import java.util.List;

import com.amos.api.gestiondestock.controller.api.ArticleApi;
import com.amos.api.gestiondestock.dto.ArticleDto;
import com.amos.api.gestiondestock.dto.LigneCommandeClientDto;
import com.amos.api.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.amos.api.gestiondestock.dto.LigneVenteDto;
import com.amos.api.gestiondestock.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleController implements ArticleApi {

  private ArticleService articleService;

  @Autowired
  public ArticleController(
      ArticleService articleService
  ) {
    this.articleService = articleService;
  }

  @Override
  public ArticleDto save(ArticleDto dto) {
    return articleService.save(dto);
  }

  @Override
  public ArticleDto findById(Integer id) {
    return articleService.findById(id);
  }

  @Override
  public ArticleDto findArticleByCodeArticle(String codeArticle) {
    return articleService.findArticleByCodeArticle(codeArticle);
  }

  @Override
  public List<ArticleDto> findAll() {
    return articleService.findAll();
  }

  @Override
  public List<LigneVenteDto> findHistoriqueVentes(Integer idArticle) {
    return articleService.findHistoriqueVentes(idArticle);
  }

  @Override
  public List<LigneCommandeClientDto> findHistoriaueCommandeClient(Integer idArticle) {
    return articleService.findHistoriaueCommandeClient(idArticle);
  }

  @Override
  public List<LigneCommandeFournisseurDto> findHistoriqueCommandeFournisseur(Integer idArticle) {
    return articleService.findHistoriqueCommandeFournisseur(idArticle);
  }

  @Override
  public List<ArticleDto> findAllArticleByIdCategory(Integer idCategory) {
    return articleService.findAllArticleByIdCategory(idCategory);
  }

  @Override
  public void delete(Integer id) {
    articleService.delete(id);
  }
}
