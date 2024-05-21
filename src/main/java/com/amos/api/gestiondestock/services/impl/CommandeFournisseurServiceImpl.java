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
import com.amos.api.gestiondestock.repository.*;
import com.amos.api.gestiondestock.services.CommandeFournisseurService;
import com.amos.api.gestiondestock.services.MvtStkService;
import com.amos.api.gestiondestock.validator.ArticleValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class CommandeFournisseurServiceImpl implements CommandeFournisseurService {


    private CommandeFournisseurRepository commandeFournisseurRepository;

    private LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository;
    private FournisseurRepository fournisseurRepository;

    private ArticleRepository articleRepository;

    private MvtStkService mvtStkService;

    @Autowired
  public CommandeFournisseurServiceImpl(CommandeFournisseurRepository commandeFournisseurRepository, LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository, FournisseurRepository fournisseurRepository, ArticleRepository articleRepository, MvtStkService mvtStkService) {
    this.commandeFournisseurRepository = commandeFournisseurRepository;
    this.ligneCommandeFournisseurRepository = ligneCommandeFournisseurRepository;
    this.fournisseurRepository = fournisseurRepository;
    this.articleRepository = articleRepository;
      this.mvtStkService = mvtStkService;
    }

  @Override
  public CommandeFournisseurDto save(CommandeFournisseurDto dto) {
    //verifie si le fournisseur existe dans la BDD
    Optional<Fournisseur> fournisseur = fournisseurRepository.findById(dto.getFournisseur().getId());

    if (fournisseur.isEmpty()){
      log.warn("Fournisseur with ID {} was not found in the BDD", dto.getFournisseur().getId());
      throw new EntityNotFoundException("Aucun fournisseur avec l'ID" + dto.getFournisseur().getId() +
              "n'a été trouvé dans la BDD", ErrorCodes.FOURNISSEUR_NOT_FOUND);
    }
    //Verifie si l'article est present dans la BDD
    //une liste d'erreur lié aux articles
    //Si la ligne n'est pas null on verifie si ses articles aussi ne sont pas null
    List<String> articleErrors = new ArrayList<>();
    if (dto.getLigneCommandeFournisseurs() != null){
      dto.getLigneCommandeFournisseurs().forEach(ligCmdClt -> {
        if (ligCmdClt.getArticle() != null) {
          Optional<Article> article = articleRepository.findById(ligCmdClt.getArticle().getId());
          if (article.isEmpty()){
            articleErrors.add("L'article avec l'ID"+ligCmdClt.getArticle().getId()+"n'existe pas dans la base de donnée");
          }else {
            articleErrors.add("Impossible d'enregister une commande avec un article null");
          }
        }
      });    }
    if (!articleErrors.isEmpty()){
      log.warn("");
      throw new InvalidEntityException("Article n'est pas dans la BDD",ErrorCodes.ARTICLE_NOT_FOUND,articleErrors);
    }

    CommandeFournisseur savedCmdClt = commandeFournisseurRepository.save(CommandeFournisseurDto.toEntity(dto));
    //on verifie si la ligne commande client n'est pas null

    if (dto.getLigneCommandeFournisseurs() != null){
      dto.getLigneCommandeFournisseurs().forEach(ligCmdClt -> {
        LigneCommandeFournisseur ligneCommandeFournisseur = LigneCommandeFournisseurDto.toEntity(ligCmdClt);
        //modifier avec set en attribuant une ligne à une commande
        ligneCommandeFournisseur.setCommandeFournisseur(savedCmdClt);
        //enregistrement avec le repo avec save qui prend en param une lignCommClient
        ligneCommandeFournisseurRepository.save(ligneCommandeFournisseur);
      });
    }

    return CommandeFournisseurDto.fromEntity(savedCmdClt);
  }

  @Override
  public CommandeFournisseurDto findById(Integer id) {
    if (id == null){
      log.error("CommandeClient ID is null");
      return null;
    }
    return commandeFournisseurRepository.findById(id)
            .map(CommandeFournisseurDto::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucune commandeClient avec l'ID = " + id + " n' ete trouve dans la BDD",
                    ErrorCodes.COMMANDE_CLIENT_NOT_FOUND)
            );
  }

  @Override
  public CommandeFournisseurDto findCommandeFournisseurByCode(String code) {
    if (!StringUtils.hasLength(code)) {
      log.error("Commande fournisseur CODE is null");
      return null;
    }
    return commandeFournisseurRepository.findCommandeFournisseurByCode(code)
            .map(CommandeFournisseurDto::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucune commande fournisseur avec le CODE = " + code + " n' ete trouve dans la BDD",
                    ErrorCodes.COMMANDE_FOURNISSEUR_NOT_FOUND)
            );

  }

  @Override
  public CommandeFournisseurDto findByCode(String code) {
    if (!StringUtils.hasLength(code)) {
      log.error("Commande fournisseur CODE is NULL");
      return null;
    }
    return commandeFournisseurRepository.findCommandeFournisseurByCode(code)
            .map(CommandeFournisseurDto::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucune commande fournisseur n'a ete trouve avec le CODE " + code, ErrorCodes.COMMANDE_FOURNISSEUR_NOT_FOUND
            ));
  }

  @Override
  public List<CommandeFournisseurDto> findAll() {
    return commandeFournisseurRepository.findAll().stream()
            .map(CommandeFournisseurDto::fromEntity)
            .collect(Collectors.toList());
  }


  @Override
  public List<LigneCommandeFournisseurDto> findAllLignesCommandesFournisseurByCommandeFournisseurId(Integer idCommande) {
    return ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(idCommande).stream()
            .map(LigneCommandeFournisseurDto::fromEntity)
            .collect(Collectors.toList());
  }


  @Override
  public void delete(Integer id) {
    if (id == null) {
      log.error("Commande fournisseur ID is null");
      return ;
    }
    commandeFournisseurRepository.deleteById(id);
  }

  @Override
  public CommandeFournisseurDto updateEtatCommande(Integer idCommande, EtatCommande etatCommande) {
    checkIdCommande(idCommande);
    if (!StringUtils.hasLength(String.valueOf(etatCommande))) {
      log.error("L'etat de la commande fournisseur is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec un etat null",
              ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
    }
    CommandeFournisseurDto commandeFournisseur = checkEtatCommande(idCommande);
    commandeFournisseur.setEtatCommande(etatCommande);

    CommandeFournisseur savedCommande = commandeFournisseurRepository.save(CommandeFournisseurDto.toEntity(commandeFournisseur));
    if (commandeFournisseur.isCommandeLivree()) {
      updateMvtStk(idCommande);
    }
    return CommandeFournisseurDto.fromEntity(savedCommande);
  }

  @Override
  public CommandeFournisseurDto updateQuantiteCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
    checkIdCommande(idCommande);
    checkIdLigneCommande(idLigneCommande);

    if (quantite == null || quantite.compareTo(BigDecimal.ZERO) == 0) {
      log.error("L'ID de la ligne commande is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec une quantite null ou ZERO",
              ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
    }

    CommandeFournisseurDto commandeFournisseur = checkEtatCommande(idCommande);
    Optional<LigneCommandeFournisseur> ligneCommandeFournisseurOptional = findLigneCommandeFournisseur(idLigneCommande);

    LigneCommandeFournisseur ligneCommandeFounisseur = ligneCommandeFournisseurOptional.get();
    ligneCommandeFounisseur.setQuantite(quantite);
    ligneCommandeFournisseurRepository.save(ligneCommandeFounisseur);

    return commandeFournisseur;
  }

  @Override
  public CommandeFournisseurDto updateFournisseur(Integer idCommande, Integer idFournisseur) {
    checkIdCommande(idCommande);
    if (idFournisseur == null) {
      log.error("L'ID du fournisseur is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec un ID fournisseur null",
              ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
    }
    CommandeFournisseurDto commandeFournisseur = checkEtatCommande(idCommande);
    Optional<Fournisseur> fournisseurOptional = fournisseurRepository.findById(idFournisseur);
    if (fournisseurOptional.isEmpty()) {
      throw new EntityNotFoundException(
              "Aucun fournisseur n'a ete trouve avec l'ID " + idFournisseur, ErrorCodes.FOURNISSEUR_NOT_FOUND);
    }
    commandeFournisseur.setFournisseur(FournisseurDto.fromEntity(fournisseurOptional.get()));

    return CommandeFournisseurDto.fromEntity(
            commandeFournisseurRepository.save(CommandeFournisseurDto.toEntity(commandeFournisseur))
    );
  }

  @Override
  public CommandeFournisseurDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer idArticle) {
    checkIdCommande(idCommande);
    checkIdLigneCommande(idLigneCommande);
    checkIdArticle(idArticle, "nouvel");

    CommandeFournisseurDto commandeFournisseur = checkEtatCommande(idCommande);

    Optional<LigneCommandeFournisseur> ligneCommandeFournisseur = findLigneCommandeFournisseur(idLigneCommande);

    Optional<Article> articleOptional = articleRepository.findById(idArticle);
    if (articleOptional.isEmpty()) {
      throw new EntityNotFoundException(
              "Aucune article n'a ete trouve avec l'ID " + idArticle, ErrorCodes.ARTICLE_NOT_FOUND);
    }

    List<String> errors = ArticleValidator.validate(ArticleDto.fromEntity(articleOptional.get()));
    if (!errors.isEmpty()) {
      throw new InvalidEntityException("Article invalid", ErrorCodes.ARTICLE_NOT_VALID, errors);
    }

    LigneCommandeFournisseur ligneCommandeFournisseurToSaved = ligneCommandeFournisseur.get();
    ligneCommandeFournisseurToSaved.setArticle(articleOptional.get());
    ligneCommandeFournisseurRepository.save(ligneCommandeFournisseurToSaved);

    return commandeFournisseur;
  }

  @Override
  public CommandeFournisseurDto deleteArticle(Integer idCommande, Integer idLigneCommande) {
    checkIdCommande(idCommande);
    checkIdLigneCommande(idLigneCommande);

    CommandeFournisseurDto commandeFournisseur = checkEtatCommande(idCommande);
    // Just to check the LigneCommandeFournisseur and inform the fournisseur in case it is absent
    findLigneCommandeFournisseur(idLigneCommande);
    ligneCommandeFournisseurRepository.deleteById(idLigneCommande);

    return commandeFournisseur;
  }

  private CommandeFournisseurDto checkEtatCommande(Integer idCommande) {
    CommandeFournisseurDto commandeFournisseur = findById(idCommande);
    if (commandeFournisseur.isCommandeLivree()) {
      throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livree", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
    }
    return commandeFournisseur;
  }

  private Optional<LigneCommandeFournisseur> findLigneCommandeFournisseur(Integer idLigneCommande) {
    Optional<LigneCommandeFournisseur> ligneCommandeFournisseurOptional = ligneCommandeFournisseurRepository.findById(idLigneCommande);
    if (ligneCommandeFournisseurOptional.isEmpty()) {
      throw new EntityNotFoundException(
              "Aucune ligne commande fournisseur n'a ete trouve avec l'ID " + idLigneCommande, ErrorCodes.COMMANDE_FOURNISSEUR_NOT_FOUND);
    }
    return ligneCommandeFournisseurOptional;
  }

  private void checkIdCommande(Integer idCommande) {
    if (idCommande == null) {
      log.error("Commande fournisseur ID is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec un ID null",
              ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
    }
  }

  private void checkIdLigneCommande(Integer idLigneCommande) {
    if (idLigneCommande == null) {
      log.error("L'ID de la ligne commande is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec une ligne de commande null",
              ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
    }
  }

  private void checkIdArticle(Integer idArticle, String msg) {
    if (idArticle == null) {
      log.error("L'ID de " + msg + " is NULL");
      throw new InvalidOperationException("Impossible de modifier l'etat de la commande avec un " + msg + " ID article null",
              ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
    }
  }

  private void updateMvtStk(Integer idCommande) {
    List<LigneCommandeFournisseur> ligneCommandeFournisseur = ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(idCommande);
    ligneCommandeFournisseur.forEach(lig -> {
      effectuerEntree(lig);
    });
  }

  private void effectuerEntree(LigneCommandeFournisseur lig) {
    MvtStkDto mvtStkDto = MvtStkDto.builder()
            .article(ArticleDto.fromEntity(lig.getArticle()))
            .dateMvt(Instant.now())
            .typeMvt(TypeMvtStk.ENTREE)
            .sourceMvt(SourceMvtStk.COMMANDE_FOURNISSEUR)
            .quantite(lig.getQuantite())
            .idEntreprise(lig.getIdEntreprise())
            .build();
    mvtStkService.entreeStock(mvtStkDto);
  }


}
