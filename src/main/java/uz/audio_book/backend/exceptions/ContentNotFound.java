package uz.audio_book.backend.exceptions;

public class ContentNotFound extends RuntimeException{
    public ContentNotFound(String message) {
        super(message);
    }
}
