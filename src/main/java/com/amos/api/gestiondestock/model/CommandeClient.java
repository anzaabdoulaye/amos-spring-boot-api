package com.amos.api.gestiondestock.model;

import java.time.Instant;
import java.util.List;

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
@Table(name = "commandeclient")
public class CommandeClient extends AbstractEntity {

  @Column(name = "code")
  private String code;

  @Column(name = "datecommande")
  private Instant dateCommande;

  @Column(name = "etatcommande")
  @Enumerated(EnumType.STRING)
  private EtatCommande etatCommande;

  @Column(name = "identreprise")
  private Integer idEntreprise;

  @ManyToOne
  @JoinColumn(name = "idclient")
  private Client client;

  @OneToMany(mappedBy = "commandeClient")
  private List<LigneCommandeClient> ligneCommandeClients;

}
