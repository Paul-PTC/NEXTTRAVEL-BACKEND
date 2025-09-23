package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Page<Pago> findByReserva_IdReserva(Long idReserva, Pageable p);

    Page<Pago> findByMetodoContainingIgnoreCase(String q, Pageable p);

    Page<Pago> findByMontoBetween(BigDecimal min, BigDecimal max, Pageable p);

    Page<Pago> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);
}
