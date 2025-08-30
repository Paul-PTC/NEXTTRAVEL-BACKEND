package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.VwDescuentoAplicado;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VwDescuentoAplicadoRepository extends JpaRepository<VwDescuentoAplicado, Long> {

    Page<VwDescuentoAplicado> findByClienteContainingIgnoreCase(String q, Pageable p);
    Page<VwDescuentoAplicado> findByLugarContainingIgnoreCase(String q, Pageable p);
    Page<VwDescuentoAplicado> findByTipoDescuento(String tipo, Pageable p);
    Page<VwDescuentoAplicado> findByPromocionContainingIgnoreCase(String q, Pageable p);

    Page<VwDescuentoAplicado> findByPorcentajeAplicado(Integer n, Pageable p);
    Page<VwDescuentoAplicado> findByPorcentajeAplicadoBetween(Integer min, Integer max, Pageable p);

    Page<VwDescuentoAplicado> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);

    Page<VwDescuentoAplicado> findByIdReserva(Long idReserva, Pageable p);
}
