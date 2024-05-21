package com.amos.api.gestiondestock.repository;

import com.amos.api.gestiondestock.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Integer> {

}
