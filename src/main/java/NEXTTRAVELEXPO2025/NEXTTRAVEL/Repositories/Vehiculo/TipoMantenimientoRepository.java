package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.TipoMantenimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoMantenimientoRepository extends JpaRepository<TipoMantenimiento, Long> {

    boolean existsByNombreTipoIgnoreCase(String nombreTipo);

    Page<TipoMantenimiento> findByNombreTipoContainingIgnoreCase(String q, Pageable p);

    Page<TipoMantenimiento> findByDescripcionContainingIgnoreCase(String q, Pageable p);
}
