package NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
