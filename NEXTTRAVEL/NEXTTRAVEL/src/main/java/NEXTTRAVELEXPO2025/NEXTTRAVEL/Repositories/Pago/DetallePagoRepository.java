package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.DetallePago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface DetallePagoRepository extends JpaRepository<DetallePago, Long> {

    Page<DetallePago> findByPago_IdPago(Long idPago, Pageable p);

    Page<DetallePago> findByDescripcionContainingIgnoreCase(String q, Pageable p);

    Page<DetallePago> findByMontoBetween(BigDecimal min, BigDecimal max, Pageable p);
}
