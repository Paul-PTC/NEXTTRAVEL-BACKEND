package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.VwEmpleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;

public interface VwEmpleadoRepository extends JpaRepository<VwEmpleado, String> {
    Page<VwEmpleado> findByNombreContainingIgnoreCase(String q, Pageable p);
    Page<VwEmpleado> findByCorreoContainingIgnoreCase(String q, Pageable p);
    Page<VwEmpleado> findByTelefonoContainingIgnoreCase(String q, Pageable p);
    Page<VwEmpleado> findByDireccionContainingIgnoreCase(String q, Pageable p);
    Page<VwEmpleado> findByRangoContainingIgnoreCase(String q, Pageable p);
    Page<VwEmpleado> findBySalarioBaseBetween(BigDecimal min, BigDecimal max, Pageable p);
}