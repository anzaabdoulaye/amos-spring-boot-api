package com.amos.api.gestiondestock.validator;

import java.util.ArrayList;
import java.util.List;

import com.amos.api.gestiondestock.dto.CategoryDto;
import org.springframework.util.StringUtils;

public class CategoryValidator {

  public static List<String> validate(CategoryDto categoryDto) {
    List<String> errors = new ArrayList<>();

    if (categoryDto == null || !StringUtils.hasLength(categoryDto.getCode())) {
      errors.add("Veuillez renseigner le code de la categorie");
    }
    return errors;
  }

}
