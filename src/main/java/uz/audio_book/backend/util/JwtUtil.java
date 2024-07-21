package uz.audio_book.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import uz.audio_book.backend.dto.SignUpDTO;
import uz.audio_book.backend.service.MailService;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final MailService mailService;
    private final ObjectMapper jacksonObjectMapper;
    @Value("${jwt.secret.key}")
    private String secretKey;
    Random random = new Random();

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
                .issuer("homework.io")
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

    public String generateVerificationCodeToken(SignUpDTO user) {
        user.setVerificationCode(genVerificationCode().toString());
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("password", user.getPassword());
        claims.put("birthDate", user.getBirthDate());
        claims.put("verificationCode", user.getVerificationCode());

        mailService.sendConfirmationCode(user.getEmail(), user.getVerificationCode());
        return genSignUpConfirmationToken(claims, user.getEmail());
    }

    public String genSignUpConfirmationToken(Map<String, Object> user, String email) {
        return "Confirmation " + Jwts.builder()
                .claim("details", user)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(genKey())
                .compact();
    }

    private Integer genVerificationCode() {
        int code = random.nextInt(101010, 989898);
        System.out.println(code);
        return code;
    }

    public boolean checkVerificationCodeFromDto(String verificationCode, String token) {
        SignUpDTO user = getDtoFromToken(token);
        return verificationCode.equals(user.getVerificationCode());
    }

    public SignUpDTO getDtoFromToken(String token) {
        Claims claims = getClaims(token);
        Map<String, Object> details = claims.get("details", Map.class);
        return jacksonObjectMapper.convertValue(details, SignUpDTO.class);
    }

    public String generateCodeToken(String email) {
        Integer verificationCode = genVerificationCode();
        mailService.sendConfirmationCode(email, verificationCode.toString());
        return "Confirmation " + Jwts.builder()
                .subject(email)
                .claim("confirmationCode", verificationCode.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(genKey())
                .compact();
    }

    public boolean checkVerification(String verificationCode, String token) {
        Claims claims = getClaims(token);
        return verificationCode.equals(claims.get("confirmationCode", String.class));
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }
}
