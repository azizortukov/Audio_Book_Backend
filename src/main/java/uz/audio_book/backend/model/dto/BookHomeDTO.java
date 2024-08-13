package uz.audio_book.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uz.audio_book.backend.model.projection.BookProjection;

import java.util.List;

public record BookHomeDTO(
        @JsonProperty("new_release")
        List<BookProjection> newRelease,
        @JsonProperty("trending_now")
        List<BookProjection> trendingNow,
        @JsonProperty("best_seller")
        List<BookProjection> bestSeller,
        List<BookProjection> recommended) {}
