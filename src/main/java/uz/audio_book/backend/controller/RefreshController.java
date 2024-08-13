package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.audio_book.backend.config.security.CustomUserDetailsService;
import uz.audio_book.backend.exceptions.ExceptionResponse;
import uz.audio_book.backend.model.dto.AccessTokenDTO;
import uz.audio_book.backend.util.JwtUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/refresh")
@Tag(name = "Token refresh API", description = "(Should receive refresh token and gives access token)")
public class RefreshController {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Operation(summary = "Refresh access token API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New access token is provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessTokenDTO.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token is expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping
    public HttpEntity<?> refresh() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return ResponseEntity.ok(new AccessTokenDTO(
                jwtUtil.genToken(userDetails))
        );
    }

}
