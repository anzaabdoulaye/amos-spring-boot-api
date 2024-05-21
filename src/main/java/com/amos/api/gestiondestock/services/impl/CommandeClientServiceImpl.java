package com.amos.api.gestiondestock.services.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amos.api.gestiondestock.dto.*;
import com.amos.api.gestiondestock.exception.EntityNotFoundException;
import com.amos.api.gestiondestock.exception.ErrorCodes;
import com.amos.api.gestiondestock.exception.InvalidEntityException;
import com.amos.api.gestiondestock.exception.InvalidOperationException;
import com.amos.api.gestiondestock.model.*;
import com.amos.api.gestiondestock.repository.ArticleRepository;
import com.amos.api.gestiondestock.repository.ClientRepository;
import com.amos.api.gestiondestock.repository.CommandeClientRepository;
import com.amos.api.gestiondestock.repository.LigneCommandeClientRepository;
import com.amos.api.gestiondestock.services.CommandeClientService;
import com.amos.api.gestiondestock.services.MvtStkService;
import com.amos.api.gestiondestock.validator.ArticleValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class CommandeClientServiceImpl implements CommandeClientService {

  private CommandeClientRepository commandeClientRepository;
  private LigneCommandeClientRepository ligneCommandeClientRepository;
  private ClientRepository clientRepository;
  private ArticleRepository articleRepository;

  private MvtStkService mvtStkService;

  @Autowired
  public CommandeClientServiceImpl(CommandeClientRepository commandeClientRepository, LigneCommandeClientRepository ligneCommandeClientRepository, ClientRepository clientRepository, ArticleRepository articleRepository, MvtStkService mvtStkService) {
    this.commandeClientRepository = commandeClientRepository;
    this.ligneCommandeClientRepository = ligneCommandeClientRepository;
    this.clientRepository = clientRepository;
    this.articleRepository = articleRepository;
    this.mvtStkService = mvtStkService;
  }

  @Override
  public CommandeClientDto save(CommandeClientDto dto) {
    //verifie si le client existe dans la BDD
    Optional<Client> client = clientRepository.findById(dto.getClient().getId());

    if (client.isEmpty()) {
      log.warn("Client with ID {} was not found in the BDD", dto.getClient().getId());
      throw new EntityNotFoundException("Aucun client avec l'ID" + dto.getClient().getId() +
              "n'a été trouvé dans la BDD", ErrorCodes.CLIENT_NOT_FOUND);
    }
    if (dto.getId() != null && dto.isCommandeLivree()) {
      throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livree", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
    }
    //Verifie si l'article est present dans la BDD
    //une liste d'erreur lié aux articles
    //Si la ligne n'est pas null on verifie si ses articles aussi ne sont pas null
    List<String> articleErrors = new ArrayList<>();
    if (dto.getLigneCommandeClients() != null) {
      dto.getLigneCommandeClients().forEach(ligCmdClt -> {
        if (ligCmdClt.getArticle() != null) {
          Optional<Article> article = articleRepository.findById(ligCmdClt.getArticle().getId());
          if (article.isEmpty()) {
            articleErrors.add("L'article avec l'ID" + ligCmdClt.getArticle().getId() + "n'existe pas dans la base de donnée");
          } else {
            articleErrors.add("Impossible d'enregister une commande avec un article null");
          }
        }
      });
    }
    if (!articleErrors.isEmpty()) {
      log.warn("");
      throw new InvalidEntityException("Article n'est pas dans la BDD", ErrorCodes.ARTICLE_NOT_FOUND, articleErrors);
    }

    CommandeClient savedCmdClt = commandeClientRepository.save(CommandeClientDto.toEntity(dto));
    //on verifie si la ligne commande client n'est pas null

    if (dto.getLigneCommandeClients() != null) {
      dto.getLigneCommandeClients().forEach(ligCmdClt -> {
        LigneCommandeClient ligneCommandeClient = LigneCommandeClientDto.toEntity(ligCmdClt);
        //modifier avec set en attribuant une ligne à une commande
        ligneCommandeClient.setCommandeClient(savedCmdClt);
        //enregistrement avec le repo avec save qui prend en param une lignCommClient
        ligneCommandeClientRepository.save(ligneCommandeClient);
      });
    }

    return CommandeClientDto.fromEntity(savedCmdClt);
  }

  @Override
  public CommandeClientDto findById(Integer id) {
    if (id == null) {
      log.error("CommandeClient ID is null");
      return null;
    }
    return commandeClientRepository.findById(id)
            .map(CommandeClientDto::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucune commandeClient avec l'ID = " + id + " n' ete trouve dans la BDD",
                    ErrorCodes.COMMANDE_CLIENT_NOT_FOUND)
            );
  }

  @Override
  public CommandeClientDto findByCode(String code) {
    if (!StringUtils.hasLength(code)) {
      log.error("Commande client CODE is null");
      return null;
    }
    return commandeClientRepository.findCommandeClientByCode(code)
            .map(CommandeClientDto::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucune commande client avec le CODE = " + code + " n' ete trouve dans la BDD",
                    ErrorCodes.COMMANDE_CLIENT_NOT_FOUND)
            );

  }

  @Override
  public List<CommandeClientDto> findAll() {
    return commandeClientRepository.findAll().stream()
            .map(CommandeClientDto::fromEntity)
            .collect(Collectors.toList());
  }


  @Override
  public void delete(Integer id) {
    if (id == null) {
      log.error("Commande client ID is null");
      return;
    }
    commandeClientRepository.deleteById(id);
  }

  @Override
  public List<LigneCommandeClientDto> findAllLignesCommandesClientByCommandeClientId(Integer idCommande) {
    return ligneCommandeClientRepository.findAllByCommandeClientId(idCommande).stream()
            .map(LigneCommandeClientDto::fromEntity)
            .collect(Collectors.toList());
  }

  @Override
  public CommandeClientDto updateEtatCommande(Integer idCommande, EtatCommande etatCommande) {
    checkIdCommande(idCommande);
    if (!StringUtils.hasLength(String.valueOf(etatCommande))) {
      log.error("L'etat de la commande client is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec un etat null",
              ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
    }
    CommandeClientDto commandeClient = checkEtatCommande(idCommande);
    commandeClient.setEtatCommande(etatCommande);

    CommandeClient savedCmdClt = commandeClientRepository.save(CommandeClientDto.toEntity(commandeClient));
    if (commandeClient.isCommandeLivree()) {
      updateMvtStk(idCommande);
    }

    return CommandeClientDto.fromEntity(savedCmdClt);
  }

  @Override
  public CommandeClientDto updateQuantiteCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
    checkIdCommande(idCommande);
    checkIdLigneCommande(idLigneCommande);

    if (quantite == null || quantite.compareTo(BigDecimal.ZERO) == 0) {
      log.error("L'ID de la ligne commande is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec une quantite null ou ZERO",
              ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
    }

    CommandeClientDto commandeClient = checkEtatCommande(idCommande);
    Optional<LigneCommandeClient> ligneCommandeClientOptional = findLigneCommandeClient(idLigneCommande);

    LigneCommandeClient ligneCommandeClient = ligneCommandeClientOptional.get();
    ligneCommandeClient.setQuantite(quantite);
    ligneCommandeClientRepository.save(ligneCommandeClient);

    return commandeClient;
  }

  @Override
  public CommandeClientDto updateClient(Integer idCommande, Integer idClient) {
    checkIdCommande(idCommande);
    if (idClient == null) {
      log.error("L'ID du client is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec un ID client null",
              ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
    }
    CommandeClientDto commandeClient = checkEtatCommande(idCommande);
    Optional<Client> clientOptional = clientRepository.findById(idClient);
    if (clientOptional.isEmpty()) {
      throw new EntityNotFoundException(
              "Aucun client n'a ete trouve avec l'ID " + idClient, ErrorCodes.CLIENT_NOT_FOUND);
    }
    commandeClient.setClient(ClientDto.fromEntity(clientOptional.get()));

    return CommandeClientDto.fromEntity(
            commandeClientRepository.save(CommandeClientDto.toEntity(commandeClient))
    );
  }

  @Override
  public CommandeClientDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer idArticle) {
    checkIdCommande(idCommande);
    checkIdLigneCommande(idLigneCommande);
    checkIdArticle(idArticle, "nouvel");

    CommandeClientDto commandeClient = checkEtatCommande(idCommande);

    Optional<LigneCommandeClient> ligneCommandeClient = findLigneCommandeClient(idLigneCommande);

    Optional<Article> articleOptional = articleRepository.findById(idArticle);
    if (articleOptional.isEmpty()) {
      throw new EntityNotFoundException(
              "Aucune article n'a ete trouve avec l'ID " + idArticle, ErrorCodes.ARTICLE_NOT_FOUND);
    }

    List<String> errors = ArticleValidator.validate(ArticleDto.fromEntity(articleOptional.get()));
    if (!errors.isEmpty()) {
      throw new InvalidEntityException("Article invalid", ErrorCodes.ARTICLE_NOT_VALID, errors);
    }

    LigneCommandeClient ligneCommandeClientToSaved = ligneCommandeClient.get();
    ligneCommandeClientToSaved.setArticle(articleOptional.get());
    ligneCommandeClientRepository.save(ligneCommandeClientToSaved);

    return commandeClient;
  }

  @Override
  public CommandeClientDto deleteArticle(Integer idCommande, Integer idLigneCommande) {
    checkIdCommande(idCommande);
    checkIdLigneCommande(idLigneCommande);

    CommandeClientDto commandeClient = checkEtatCommande(idCommande);
    // Just to check the LigneCommandeClient and inform the client in case it is absent
    findLigneCommandeClient(idLigneCommande);
    ligneCommandeClientRepository.deleteById(idLigneCommande);

    return commandeClient;
  }

  private CommandeClientDto checkEtatCommande(Integer idCommande) {
    CommandeClientDto commandeClient = findById(idCommande);
    if (commandeClient.isCommandeLivree()) {
      throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livree", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
    }
    return commandeClient;
  }

  private Optional<LigneCommandeClient> findLigneCommandeClient(Integer idLigneCommande) {
    Optional<LigneCommandeClient> ligneCommandeClientOptional = ligneCommandeClientRepository.findById(idLigneCommande);
    if (ligneCommandeClientOptional.isEmpty()) {
      throw new EntityNotFoundException(
              "Aucune ligne commande client n'a ete trouve avec l'ID " + idLigneCommande, ErrorCodes.COMMANDE_CLIENT_NOT_FOUND);
    }
    return ligneCommandeClientOptional;
  }

  private void checkIdCommande(Integer idCommande) {
    if (idCommande == null) {
      log.error("Commande client ID is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec un ID null",
              ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
    }
  }

  private void checkIdLigneCommande(Integer idLigneCommande) {
    if (idLigneCommande == null) {
      log.error("L'ID de la ligne commande is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec une ligne de commande null",
              ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
    }
  }

  private void checkIdArticle(Integer idArticle, String msg) {
    if (idArticle == null) {
      log.error("L'ID de " + msg + " is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec un " + msg + " ID article null",
              ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
    }
  }

  private void updateMvtStk(Integer idCommande) {
    List<LigneCommandeClient> ligneCommandeClients = ligneCommandeClientRepository.findAllByCommandeClientId(idCommande);
    ligneCommandeClients.forEach(lig -> {
      effectuerSortie(lig);
    });
  }

  private void effectuerSortie(LigneCommandeClient lig) {
    MvtStkDto mvtStkDto = MvtStkDto.builder()
            .article(ArticleDto.fromEntity(lig.getArticle()))
            .dateMvt(Instant.now())
            .typeMvt(TypeMvtStk.SORTIE)
            .sourceMvt(SourceMvtStk.COMMANDE_CLIENT)
            .quantite(lig.getQuantite())
            .idEntreprise(lig.getIdEntreprise())
            .build();
    mvtStkService.sortieStock(mvtStkDto);
  }

}