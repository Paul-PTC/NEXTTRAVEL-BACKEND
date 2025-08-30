package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.DetalleMantenimiento;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface DetalleMantenimientoRepository extends JpaRepository<DetalleMantenimiento, Long> {

    Page<DetalleMantenimiento> findByMantenimiento_IdMantenimiento(Long idMantenimiento, Pageable p);

    Page<DetalleMantenimiento> findByActividadContainingIgnoreCase(String q, Pageable p);

    Page<DetalleMantenimiento> findByCostoBetween(BigDecimal min, BigDecimal max, Pageable p);
}
