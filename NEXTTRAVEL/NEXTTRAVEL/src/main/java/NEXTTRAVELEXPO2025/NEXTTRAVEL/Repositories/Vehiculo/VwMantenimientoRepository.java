package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.VwMantenimiento;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VwMantenimientoRepository extends JpaRepository<VwMantenimiento, Long> {

    Page<VwMantenimiento> findByPlacaContainingIgnoreCase(String q, Pageable p);

    Page<VwMantenimiento> findByModeloContainingIgnoreCase(String q, Pageable p);

    Page<VwMantenimiento> findByTipoMantenimientoContainingIgnoreCase(String q, Pageable p);

    Page<VwMantenimiento> findByDescripcionContainingIgnoreCase(String q, Pageable p);

    Page<VwMantenimiento> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);
}
