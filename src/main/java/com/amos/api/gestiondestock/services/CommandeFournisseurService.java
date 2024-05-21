package com.amos.api.gestiondestock.services;

import com.amos.api.gestiondestock.dto.CommandeFournisseurDto;
import com.amos.api.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.amos.api.gestiondestock.model.EtatCommande;

import java.math.BigDecimal;
import java.util.List;

public interface CommandeFournisseurService {

  CommandeFournisseurDto save(CommandeFournisseurDto dto);

  CommandeFournisseurDto updateEtatCommande(Integer idCommande, EtatCommande etatCommande);

  CommandeFournisseurDto updateQuantiteCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite);

  CommandeFournisseurDto updateFournisseur(Integer idCommande, Integer idFournisseur);

  CommandeFournisseurDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer idArticle);

  // Delete article ==> delete LigneCommandeFournisseur
  CommandeFournisseurDto deleteArticle(Integer idCommande, Integer idLigneCommande);

  CommandeFournisseurDto findById(Integer id);

  CommandeFournisseurDto findCommandeFournisseurByCode(String code);

  List<CommandeFournisseurDto> findAll();
  CommandeFournisseurDto findByCode(String code);

  List<LigneCommandeFournisseurDto> findAllLignesCommandesFournisseurByCommandeFournisseurId(Integer idCommande);

  void delete(Integer id);

}
