package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.ApiResponse;

import lombok.Getter;
import lombok.Setter;

public class ApiResponse<T> {

    @Getter @Setter
    private boolean status;

    @Getter
    @Setter
    private String message;

    @Getter @Setter
    private T data;

    public ApiResponse(boolean status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
