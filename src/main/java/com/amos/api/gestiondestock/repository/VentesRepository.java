package com.amos.api.gestiondestock.repository;

import java.util.Optional;

import com.amos.api.gestiondestock.model.Ventes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentesRepository extends JpaRepository<Ventes, Integer> {

  Optional<Ventes> findVentesByCode(String code);
}
