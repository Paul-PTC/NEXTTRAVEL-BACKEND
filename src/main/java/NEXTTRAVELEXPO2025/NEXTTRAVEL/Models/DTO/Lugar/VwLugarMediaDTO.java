package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwLugarMediaDTO {
    private Long idLugarMedia;
    private String lugar;      // nombreLugar
    private String url;
    private String altText;
    private String isPrimary;  // 'S'/'N'
    private Integer position;
    private LocalDateTime createdAt;
}
