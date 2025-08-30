package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.VwGasto;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface VwGastoRepository extends JpaRepository<VwGasto, Long> {

    Page<VwGasto> findByTipoGastoContainingIgnoreCase(String q, Pageable p);

    Page<VwGasto> findByDescripcionContainingIgnoreCase(String q, Pageable p);

    Page<VwGasto> findByMontoBetween(BigDecimal min, BigDecimal max, Pageable p);

    Page<VwGasto> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);
}
