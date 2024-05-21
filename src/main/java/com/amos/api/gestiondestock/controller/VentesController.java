package com.amos.api.gestiondestock.controller;

import java.util.List;

import com.amos.api.gestiondestock.controller.api.VentesApi;
import com.amos.api.gestiondestock.dto.VentesDto;
import com.amos.api.gestiondestock.services.VentesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VentesController implements VentesApi {

  private VentesService ventesService;

  @Autowired
  public VentesController(VentesService ventesService) {
    this.ventesService = ventesService;
  }

  @Override
  public VentesDto save(VentesDto dto) {
    return ventesService.save(dto);
  }

  @Override
  public VentesDto findById(Integer id) {
    return ventesService.findById(id);
  }

  @Override
  public VentesDto findByCode(String code) {
    return ventesService.findByCode(code);
  }

  @Override
  public List<VentesDto> findAll() {
    return ventesService.findAll();
  }

  @Override
  public void delete(Integer id) {
    ventesService.delete(id);
  }
}
