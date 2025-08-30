package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.PuntosCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PuntosClienteRepository extends JpaRepository<PuntosCliente, String> {

    Page<PuntosCliente> findByDuiClienteContainingIgnoreCase(String q, Pageable p);

    Page<PuntosCliente> findByPuntosBetween(Integer min, Integer max, Pageable p);

    Page<PuntosCliente> findByFechaActualizacionBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);
}