package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.VwReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VwReservaRepository extends JpaRepository<VwReserva, Long> {

    Page<VwReserva> findByNombreClienteContainingIgnoreCase(String q, Pageable p);
    Page<VwReserva> findByDuiClienteContainingIgnoreCase(String q, Pageable p);
    Page<VwReserva> findByLugarContainingIgnoreCase(String q, Pageable p);
    Page<VwReserva> findByEstadoActualContainingIgnoreCase(String q, Pageable p);

    Page<VwReserva> findByCantidadPersonasBetween(Integer min, Integer max, Pageable p);
    Page<VwReserva> findByFechaReservaBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);
}
