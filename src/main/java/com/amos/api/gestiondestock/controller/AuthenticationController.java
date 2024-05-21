package com.amos.api.gestiondestock.controller;

import com.amos.api.gestiondestock.dto.auth.AuthenticationRequest;
import com.amos.api.gestiondestock.dto.auth.AuthenticationResponse;
import com.amos.api.gestiondestock.model.auth.ExtendedUser;
import com.amos.api.gestiondestock.services.auth.ApplicationUserDetailService;
import com.amos.api.gestiondestock.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.amos.api.gestiondestock.utils.Constants.AUTHENTICATION_ENDPOINT;

@RestController
@RequestMapping(AUTHENTICATION_ENDPOINT)
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ApplicationUserDetailService userDetailService;

    @Autowired
    private JwtUtil jwtUtil;
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );
        final UserDetails userDetails = userDetailService.loadUserByUsername(request.getLogin());

        final String jwt = jwtUtil.generateToken((ExtendedUser) userDetails);

        return ResponseEntity.ok(AuthenticationResponse.builder().accessToken("dummy_access_token").build());
    }

}
