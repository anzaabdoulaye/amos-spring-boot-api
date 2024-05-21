package com.amos.api.gestiondestock.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lignecommandeclient")
public class LigneCommandeClient extends AbstractEntity {

  @ManyToOne
  @JoinColumn(name = "idarticle")
  private Article article;

  @ManyToOne
  @JoinColumn(name = "idcommandeclient")
  private CommandeClient commandeClient;

  @Column(name = "quantite")
  private BigDecimal quantite;

  @Column(name = "prixunitaire")
  private BigDecimal prixUnitaire;

  @Column(name = "identreprise")
  private Integer idEntreprise;

}
