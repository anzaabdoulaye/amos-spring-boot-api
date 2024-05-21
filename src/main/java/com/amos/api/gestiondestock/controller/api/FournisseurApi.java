package com.amos.api.gestiondestock.controller.api;

import com.amos.api.gestiondestock.dto.FournisseurDto;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.amos.api.gestiondestock.utils.Constants.FOURNISSEUR_ENDPOINT;

@Api("fournisseur")
public interface FournisseurApi {

  @PostMapping(FOURNISSEUR_ENDPOINT + "/create")
  FournisseurDto save(@RequestBody FournisseurDto dto);

  @GetMapping(FOURNISSEUR_ENDPOINT + "/{idFournisseur}")
  FournisseurDto findById(@PathVariable("idFournisseur") Integer id);

  @GetMapping(FOURNISSEUR_ENDPOINT + "/all")
  List<FournisseurDto> findAll();

  @DeleteMapping(FOURNISSEUR_ENDPOINT + "/delete/{idFournisseur}")
  void delete(@PathVariable("idFournisseur") Integer id);

}
