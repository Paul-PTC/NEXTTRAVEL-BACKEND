package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.VwLugarMedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VwLugarMediaRepository extends JpaRepository<VwLugarMedia, Long> {

    Page<VwLugarMedia> findByLugarContainingIgnoreCase(String q, Pageable p);
    Page<VwLugarMedia> findByUrlContainingIgnoreCase(String q, Pageable p);
    Page<VwLugarMedia> findByAltTextContainingIgnoreCase(String q, Pageable p);

    Page<VwLugarMedia> findByIsPrimary(String flag, Pageable p); // 'S' o 'N'
    Page<VwLugarMedia> findByPosition(Integer pos, Pageable p);

    Page<VwLugarMedia> findByCreatedAtBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);
}
