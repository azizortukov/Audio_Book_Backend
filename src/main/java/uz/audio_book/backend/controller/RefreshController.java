package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.audio_book.backend.config.CustomUserDetailsService;
import uz.audio_book.backend.util.JwtUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/refresh")
@Tag(name = "Token refresh API", description = "(Should receive refresh token and gives access token)")
public class RefreshController {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Operation(
            summary = "Refresh access token API",
            description = """
            This API receives refresh token and responds with access token.Response
            will be 200 (good status) or 403 (not found) when refresh token itself
            is expired""")
    @GetMapping
    public String refresh() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return jwtUtil.genToken(userDetails);
    }

}
