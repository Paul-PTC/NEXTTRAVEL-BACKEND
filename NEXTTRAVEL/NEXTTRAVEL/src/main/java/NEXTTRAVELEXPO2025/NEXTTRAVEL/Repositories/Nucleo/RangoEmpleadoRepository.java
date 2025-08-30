package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.RangoEmpleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface RangoEmpleadoRepository extends JpaRepository<RangoEmpleado, Long> {

    Page<RangoEmpleado> findByNombreRangoContainingIgnoreCase(String q, Pageable pageable);

    Page<RangoEmpleado> findBySalarioBaseBetween(BigDecimal min, BigDecimal max, Pageable pageable);

    boolean existsByNombreRangoIgnoreCase(String nombreRango);
}
