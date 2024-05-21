package com.amos.api.gestiondestock.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ventes")
public class Ventes extends AbstractEntity {

  @Column(name = "code")
  private String code;

  @Column(name = "datevente")
  private Instant dateVente;

  @Column(name = "commentaire")
  private String commentaire;

  @Column(name = "identreprise")
  private Integer idEntreprise;

  @OneToMany(mappedBy = "vente")
  private List<LigneVente> ligneVentes;
}
