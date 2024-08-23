package uz.audio_book.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.audio_book.backend.exceptions.ExceptionResponse;
import uz.audio_book.backend.model.dto.UserDetailsDTO;
import uz.audio_book.backend.model.projection.UserDetailsProjection;
import uz.audio_book.backend.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
@Tag(name = "User API", description = "(For updating and getting user details)")
public class UserController {

    private final UserService userService;

    @Operation(summary = "User info API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's profile data is returned", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDetailsProjection.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/me")
    public HttpEntity<?> getUserById() {
        return userService.getUserDetails();
    }


    @Operation(summary = "User info update API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's profile data successfully uploaded", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDetailsProjection.class))),
            @ApiResponse(responseCode = "400", description = "Param is invalid or not provided", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PutMapping("/edit")
    public HttpEntity<?> updateUser(@RequestBody UserDetailsDTO userDetailsDTO) {
        return userService.updateUserDetails(userDetailsDTO);
    }


    @Operation(summary = "User profile image update via param API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User's profile image successfully uploaded"),
            @ApiResponse(responseCode = "400", description = "Param is invalid or not provided", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PutMapping(value = "/upload_image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<?> updateUserProfileImage(@RequestParam(name = "profile_image") MultipartFile profileImage) {
        return userService.updateUserProfileImage(profileImage);
    }

}
