package com.amos.api.gestiondestock.repository;

import java.util.List;
import java.util.Optional;

import com.amos.api.gestiondestock.model.CommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeClientRepository extends JpaRepository<CommandeClient, Integer> {

  Optional<CommandeClient> findCommandeClientByCode(String code);

  List<CommandeClient> findAllByClientId(Integer id);
}
