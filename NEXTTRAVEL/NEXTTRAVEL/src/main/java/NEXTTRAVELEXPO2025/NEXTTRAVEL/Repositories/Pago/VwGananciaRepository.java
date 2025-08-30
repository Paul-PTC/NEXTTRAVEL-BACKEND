package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.VwGanancia;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface VwGananciaRepository extends JpaRepository<VwGanancia, Long> {

    Page<VwGanancia> findByClienteContainingIgnoreCase(String q, Pageable p);
    Page<VwGanancia> findByLugarContainingIgnoreCase(String q, Pageable p);

    Page<VwGanancia> findByMontoBrutoBetween(BigDecimal min, BigDecimal max, Pageable p);
    Page<VwGanancia> findByMontoNetoBetween(BigDecimal min, BigDecimal max, Pageable p);

    Page<VwGanancia> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);

    Page<VwGanancia> findByIdReserva(Long idReserva, Pageable p);
}
