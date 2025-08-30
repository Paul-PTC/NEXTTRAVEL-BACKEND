package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoRepository extends JpaRepository<Estado, Long> {

    Page<Estado> findByNombreEstadoContainingIgnoreCase(String q, Pageable pageable);

    boolean existsByNombreEstadoIgnoreCase(String nombreEstado);
}
