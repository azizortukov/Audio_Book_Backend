package uz.audio_book.backend.model.dto;

import uz.audio_book.backend.model.projection.CommentProjection;
import uz.audio_book.backend.model.projection.SelectedBookProjection;

import java.util.List;

public record BookCommentDTO(SelectedBookProjection book, List<CommentProjection> comments) {
}
