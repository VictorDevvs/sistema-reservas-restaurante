package sistema.reservas_restaurante_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sistema.reservas_restaurante_api.model.AccessToken;
import sistema.reservas_restaurante_api.model.StatusToken;
import sistema.reservas_restaurante_api.repositories.AccessTokenRepository;
import java.security.Key;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;

    private Key key;

    private final AccessTokenRepository repository;

    public JwtTokenProvider(AccessTokenRepository repository) {
        this.repository = repository;
    }

    private Key getSigningKey() {
        if (this.key == null) {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
        return this.key;
    }

    public String generateAccessToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID generateRefreshToken(UserDetails userDetails) {
        return UUID.randomUUID();
    }

    public String getEmailFromToken(String token) throws SignatureException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            throw new SignatureException("Invalid JWT signature", e);
        }
    }

    public boolean validateToken(String token){
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            Optional<AccessToken> tokenEntity = repository.findByToken(token);
            return tokenEntity.isPresent()
                    && tokenEntity.get().getStatus() == StatusToken.ATIVO
                    && tokenEntity.get().getExpiracao().isAfter(LocalDateTime.now());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
