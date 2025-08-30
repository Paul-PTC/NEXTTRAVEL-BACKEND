package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.VwCalificacionLugar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VwCalificacionLugarRepository extends JpaRepository<VwCalificacionLugar, Long> {

    Page<VwCalificacionLugar> findByNombreLugarContainingIgnoreCase(String q, Pageable p);
    Page<VwCalificacionLugar> findByClienteContainingIgnoreCase(String q, Pageable p);
    Page<VwCalificacionLugar> findByComentarioContainingIgnoreCase(String q, Pageable p);

    Page<VwCalificacionLugar> findByPuntuacion(Integer puntaje, Pageable p);
    Page<VwCalificacionLugar> findByPuntuacionBetween(Integer min, Integer max, Pageable p);

    Page<VwCalificacionLugar> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);
}
