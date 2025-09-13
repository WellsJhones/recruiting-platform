package com.wells.recruiting.platform.recruiting.platform.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.wells.recruiting.platform.recruiting.platform.user.User;
import com.wells.recruiting.platform.recruiting.platform.company.Employer;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class TokenService {
    private String secret = "mysecret123456789012345678901234";

    public String generateToken(User usuario) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Recruiting Platform")
                    .withSubject(usuario.getEmail())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);

        } catch (Exception e) {
            throw new RuntimeException("erro ao gerar token jwt " + e);
        }
    }

    // Overload for Employer, now includes role claim
    public String generateToken(Employer employer) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Recruiting Platform")
                    .withSubject(employer.getEmail())
                    .withClaim("role", employer.getRole())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);

        } catch (Exception e) {
            throw new RuntimeException("erro ao gerar token jwt " + e);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            var verifier = JWT.require(algoritmo).build();
            var jwt = verifier.verify(tokenJWT);
            return jwt.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("erro ao verificar token jwt " + e);
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getEmailFromToken(String tokenJWT) {
        return getSubject(tokenJWT);
    }

    // New: Get role from token
    public String getRoleFromToken(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            var verifier = JWT.require(algoritmo).build();
            DecodedJWT jwt = verifier.verify(tokenJWT);
            return jwt.getClaim("role").asString();
        } catch (Exception e) {
            throw new RuntimeException("erro ao obter role do token jwt " + e);
        }
    }
}
