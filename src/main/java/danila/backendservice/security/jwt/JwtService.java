package danila.backendservice.security.jwt;

import danila.backendservice.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProperties;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(UserPrincipal userPrincipal) {
        return Jwts.builder()
                .subject(String.valueOf(userPrincipal.getId()))
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(jwtProperties.accessTokenExpiration())))
                .signWith(getSecretKey())
                .compact();
    }

    public Claims extractClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }
}
