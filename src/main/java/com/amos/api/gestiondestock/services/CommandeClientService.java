package com.amos.api.gestiondestock.services;

import com.amos.api.gestiondestock.dto.CommandeClientDto;
import com.amos.api.gestiondestock.dto.LigneCommandeClientDto;
import com.amos.api.gestiondestock.model.EtatCommande;

import java.math.BigDecimal;
import java.util.List;

public interface CommandeClientService {

  CommandeClientDto save(CommandeClientDto dto);

  CommandeClientDto updateEtatCommande(Integer idCommande, EtatCommande etatCommande);

  CommandeClientDto updateQuantiteCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite);

  CommandeClientDto updateClient(Integer idCommande, Integer idClient);

  CommandeClientDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer newIdArticle);

  CommandeClientDto deleteArticle(Integer idCommande, Integer idLigneCommande);

  CommandeClientDto findById(Integer id);

  CommandeClientDto findByCode(String code);

  List<CommandeClientDto> findAll();

  List<LigneCommandeClientDto> findAllLignesCommandesClientByCommandeClientId(Integer idCommande);

  void delete(Integer id);

}
