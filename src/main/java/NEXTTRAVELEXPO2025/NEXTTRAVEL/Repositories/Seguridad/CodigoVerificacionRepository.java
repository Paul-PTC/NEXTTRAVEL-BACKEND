package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Seguridad;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Seguridad.CodigoVerificacion;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CodigoVerificacionRepository extends JpaRepository<CodigoVerificacion, Long> {

    Page<CodigoVerificacion> findByUsuario_IdUsuario(Long idUsuario, Pageable p);

    Page<CodigoVerificacion> findByCodigoContainingIgnoreCase(String q, Pageable p);

    Page<CodigoVerificacion> findByFechaGeneracionBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);

    Page<CodigoVerificacion> findByValidoHastaBetween(LocalDate desde, LocalDate hasta, Pageable p);

    Page<CodigoVerificacion> findByValidoHastaGreaterThanEqual(LocalDate fecha, Pageable p);
}
