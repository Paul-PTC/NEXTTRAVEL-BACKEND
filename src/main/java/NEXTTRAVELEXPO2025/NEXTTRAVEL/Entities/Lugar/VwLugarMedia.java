package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Table(name = "VW_LUGAR_MEDIA")
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwLugarMedia {

    @Id
    @Column(name = "IDLUGARMEDIA")
    private Long idLugarMedia;

    @Column(name = "LUGAR")
    private String lugar; // nombreLugar

    @Column(name = "URL")
    private String url;

    @Column(name = "ALT_TEXT")
    private String altText;

    @Column(name = "IS_PRIMARY")
    private String isPrimary; // 'S'/'N'

    @Column(name = "POSITION")
    private Integer position;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}
