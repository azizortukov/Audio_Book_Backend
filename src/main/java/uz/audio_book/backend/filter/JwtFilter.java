package uz.audio_book.backend.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.audio_book.backend.exceptions.BadRequestException;
import uz.audio_book.backend.exceptions.ExceptionResponse;
import uz.audio_book.backend.exceptions.ExpiredTokenException;
import uz.audio_book.backend.util.JwtUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer")) {
                String token = authorization.substring(7);
                if (jwtUtil.isValid(token)) {
                    String username = jwtUtil.getUsername(token);
                    List<GrantedAuthority> authorities = jwtUtil.getAuthorities(token);
                    var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    throw new ExpiredTokenException("Provided JWT Token is expired");
                }
            } else {
                throw new BadRequestException("You don't have permission to access this resource");
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredTokenException | BadRequestException e) {
            handleException(response, e);
        }
    }


    private void handleException(HttpServletResponse response, RuntimeException e) throws IOException {
        log.warn(e.getMessage());

        HttpStatus responseType;
        if (e instanceof ExpiredTokenException) {
            responseType = HttpStatus.UNAUTHORIZED;
        } else {
            responseType = HttpStatus.FORBIDDEN;
        }

        response.setStatus(responseType.value());
        response.setContentType("application/json");
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                responseType,
                e.getMessage(),
                LocalDateTime.now().toString()
        );
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(exceptionResponse));
    }
}
