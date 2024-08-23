package uz.audio_book.backend.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import uz.audio_book.backend.entity.Category;
import uz.audio_book.backend.exceptions.ExceptionResponse;
import uz.audio_book.backend.service.CategoryService;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category")
@Tag(name = "Category API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Category.class)))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping
    public HttpEntity<?> getCategory() {
        return categoryService.getCategories();
    }


    @Operation(summary = "Personalizing Categories by IDs API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categories saved!"),
            @ApiResponse(responseCode = "400", description = "Param is not valid or not provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/customize")
    public HttpEntity<?> customizeCategory(@RequestBody @Valid CategoryIdsDTO categoryIds) {
        return categoryService.customizeCategoryByIds(categoryIds.categoryIds());
    }


    @Operation(summary = "Personalizing All Categories API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categories saved!"),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/customize/all")
    public HttpEntity<?> customizeCategories() {
        return categoryService.customizeAllCategories();
    }


    @Operation(summary = "Personal Categories API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories saved!",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Category.class)))),
            @ApiResponse(responseCode = "401", description = "Authorization token is invalid or expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/recommended")
    public HttpEntity<?> getRecommended() {
        return categoryService.getRecommendedCategories();
    }
}

record CategoryIdsDTO(@NotNull @JsonProperty("category_ids") List<UUID> categoryIds) { }
