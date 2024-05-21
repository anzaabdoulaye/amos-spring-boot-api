package com.amos.api.gestiondestock.repository;

import java.util.List;
import java.util.Optional;

import com.amos.api.gestiondestock.model.CommandeClient;
import com.amos.api.gestiondestock.model.CommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeFournisseurRepository extends JpaRepository<CommandeFournisseur, Integer> {

  Optional<CommandeFournisseur> findCommandeFournisseurByCode(String code);

  List<CommandeClient> findAllByFournisseurId(Integer id);
}
