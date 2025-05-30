package com.beaconfire.userservice.controller;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/oauth2")
public class JwksController {
    private final JWKSource<SecurityContext> jwkSource;
    private static final Logger logger = Logger.getLogger(JwksController.class.getName());

    public JwksController(JWKSource<SecurityContext> jwkSource) {
        this.jwkSource = jwkSource;
    }

    @GetMapping("/jwks")
    public Map<String, Object> getKeys() throws Exception {
        logger.info("Generating JWKs");
        List<JWK> temp = this.jwkSource.get(new JWKSelector(new JWKMatcher.Builder().build()), null);
        JWKSet jwkSet = new JWKSet(temp);
        return jwkSet.toJSONObject();
    }
}

