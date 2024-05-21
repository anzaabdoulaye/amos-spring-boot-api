package com.amos.api.gestiondestock.controller.api;

import com.amos.api.gestiondestock.dto.VentesDto;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.amos.api.gestiondestock.utils.Constants.VENTES_ENDPOINT;

@Api("ventes")
public interface VentesApi {

  @PostMapping(VENTES_ENDPOINT + "/create")
  VentesDto save(@RequestBody VentesDto dto);

  @GetMapping(VENTES_ENDPOINT + "/{idVente}")
  VentesDto findById(@PathVariable("idVente") Integer id);

  @GetMapping(VENTES_ENDPOINT + "/{codeVente}")
  VentesDto findByCode(@PathVariable("codeVente") String code);

  @GetMapping(VENTES_ENDPOINT + "/all")
  List<VentesDto> findAll();

  @DeleteMapping(VENTES_ENDPOINT + "/delete/{idVente}")
  void delete(@PathVariable("idVente") Integer id);

}
