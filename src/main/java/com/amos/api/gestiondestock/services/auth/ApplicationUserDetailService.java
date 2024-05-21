package com.amos.api.gestiondestock.services.auth;

import com.amos.api.gestiondestock.dto.UtilisateurDto;
import com.amos.api.gestiondestock.exception.EntityNotFoundException;
import com.amos.api.gestiondestock.exception.ErrorCodes;
import com.amos.api.gestiondestock.model.Utilisateur;
import com.amos.api.gestiondestock.model.auth.ExtendedUser;
import com.amos.api.gestiondestock.repository.UtilisateurRepository;
import com.amos.api.gestiondestock.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ApplicationUserDetailService implements UserDetailsService {

    @Autowired
    private UtilisateurService service;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UtilisateurDto utilisateur = service.findByEmail(email);

        List<SimpleGrantedAuthority> authorites = new ArrayList<>();
        utilisateur.getRoles().forEach(role -> authorites.add(new SimpleGrantedAuthority(role.getRoleName())));


        return new ExtendedUser(utilisateur.getEmail(),utilisateur.getMoteDePasse(), Collections.emptyList());
    }
}
