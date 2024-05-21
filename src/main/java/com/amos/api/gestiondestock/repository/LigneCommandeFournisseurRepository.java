package com.amos.api.gestiondestock.repository;

import java.util.List;

import com.amos.api.gestiondestock.model.LigneCommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneCommandeFournisseurRepository extends JpaRepository<LigneCommandeFournisseur, Integer> {

  List<LigneCommandeFournisseur> findAllByCommandeFournisseurId(Integer idCommande);

  List<LigneCommandeFournisseur> findAllByArticleId(Integer idCommande);
}
