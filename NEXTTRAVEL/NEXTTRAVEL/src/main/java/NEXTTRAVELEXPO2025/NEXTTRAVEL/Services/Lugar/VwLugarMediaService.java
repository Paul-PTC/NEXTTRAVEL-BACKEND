package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.VwLugarMedia;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.VwLugarMediaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar.VwLugarMediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwLugarMediaService {

    private final VwLugarMediaRepository repo;

    private VwLugarMediaDTO toDTO(VwLugarMedia v) {
        return VwLugarMediaDTO.builder()
                .idLugarMedia(v.getIdLugarMedia())
                .lugar(v.getLugar())
                .url(v.getUrl())
                .altText(v.getAltText())
                .isPrimary(v.getIsPrimary())
                .position(v.getPosition())
                .createdAt(v.getCreatedAt())
                .build();
    }

    public Page<VwLugarMediaDTO> listar(Pageable p) { return repo.findAll(p).map(this::toDTO); }
    public Page<VwLugarMediaDTO> buscarPorLugar(String q, Pageable p) { return repo.findByLugarContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwLugarMediaDTO> buscarPorUrl(String q, Pageable p) { return repo.findByUrlContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwLugarMediaDTO> buscarPorAlt(String q, Pageable p) { return repo.findByAltTextContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwLugarMediaDTO> buscarPorPrimary(String flag, Pageable p) { return repo.findByIsPrimary(flag.toUpperCase(), p).map(this::toDTO); }
    public Page<VwLugarMediaDTO> buscarPorPosition(Integer pos, Pageable p) { return repo.findByPosition(pos, p).map(this::toDTO); }
    public Page<VwLugarMediaDTO> buscarPorFecha(LocalDateTime d, LocalDateTime h, Pageable p) { return repo.findByCreatedAtBetween(d, h, p).map(this::toDTO); }
}
