package it.fvaleri.integ.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.fvaleri.integ.utility.JWTTokenHelper;
import it.fvaleri.integ.domain.JWTResponse;

@CrossOrigin
@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JWTTokenHelper tokenHelper;

    @RequestMapping(value = "/auth")
    public ResponseEntity<?> createAuthenticationToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String password = (String) auth.getCredentials();

        authenticate(username, password);
        final String token = tokenHelper.generateToken(username);
        return ResponseEntity.ok(new JWTResponse(token));
    }

    private void authenticate(String username, String password) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new RuntimeException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("INVALID_CREDENTIALS", e);
        }
    }

}
