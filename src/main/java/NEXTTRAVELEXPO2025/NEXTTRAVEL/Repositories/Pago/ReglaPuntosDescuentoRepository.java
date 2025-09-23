package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.ReglaPuntosDescuento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReglaPuntosDescuentoRepository extends JpaRepository<ReglaPuntosDescuento, Long> {

    boolean existsByPuntosRequeridos(Integer puntosRequeridos);
    boolean existsByPorcentajeDescuento(Integer porcentajeDescuento);

    Page<ReglaPuntosDescuento> findByPuntosRequeridos(Integer n, Pageable p);
    Page<ReglaPuntosDescuento> findByPuntosRequeridosBetween(Integer min, Integer max, Pageable p);

    Page<ReglaPuntosDescuento> findByPorcentajeDescuento(Integer n, Pageable p);
    Page<ReglaPuntosDescuento> findByPorcentajeDescuentoBetween(Integer min, Integer max, Pageable p);
}
