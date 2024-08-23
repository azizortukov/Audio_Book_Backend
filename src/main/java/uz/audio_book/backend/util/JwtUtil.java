package uz.audio_book.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${keys.jwt}")
    private String secretKey;

    public String genToken(UserDetails userDetails) {
        String roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return "Bearer " + Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authorities", roles)
                .issuedAt(new Date())
                .issuer("audio.book")
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(genKey())
                .compact();
    }

    private SecretKey genKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    public String genRefreshToken(UserDetails userDetails) {
        String roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return "Bearer " +  Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authorities", roles)
                .issuedAt(new Date())
                .issuer("audio.book")
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 14))
                .signWith(genKey())
                .compact();
    }

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(genKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String authorities = getClaims(token).get("authorities", String.class);
        return Arrays.stream(authorities.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public String genResetPasswordToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .issuer("audio.book")
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(genKey())
                .compact();
    }
}
