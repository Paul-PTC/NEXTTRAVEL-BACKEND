package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LUGARMEDIA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LugarMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDLUGARMEDIA")
    private Long idLugarMedia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDLUGAR", nullable = false)
    private LugarTuristico lugar; // FK a LUGARTURISTICO

    @Column(name = "URL", nullable = false, length = 600)
    private String url;

    @Column(name = "ALT_TEXT", length = 200)
    private String altText;

    // 'S' o 'N' en DB
    @Column(name = "IS_PRIMARY", length = 1)
    private String isPrimary; // usa 'S' (true) o 'N' (false)

    @Column(name = "POSITION")
    private Integer position;

    // lo deja a la DB (DEFAULT SYSTIMESTAMP)
    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
