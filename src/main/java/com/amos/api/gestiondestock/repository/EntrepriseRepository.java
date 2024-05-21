package com.amos.api.gestiondestock.repository;

import com.amos.api.gestiondestock.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Integer> {

}
