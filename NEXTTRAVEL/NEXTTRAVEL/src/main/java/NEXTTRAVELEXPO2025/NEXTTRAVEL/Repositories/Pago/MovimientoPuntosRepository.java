package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.MovimientoPuntos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MovimientoPuntosRepository extends JpaRepository<MovimientoPuntos, Long> {

    Page<MovimientoPuntos> findByCliente_DuiContainingIgnoreCase(String duiLike, Pageable p);

    Page<MovimientoPuntos> findByTipo(String tipo, Pageable p);

    Page<MovimientoPuntos> findByDescripcionContainingIgnoreCase(String q, Pageable p);

    Page<MovimientoPuntos> findByReserva_IdReserva(Long idReserva, Pageable p);

    Page<MovimientoPuntos> findByPuntosCambiadosBetween(Integer min, Integer max, Pageable p);

    Page<MovimientoPuntos> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);
}
