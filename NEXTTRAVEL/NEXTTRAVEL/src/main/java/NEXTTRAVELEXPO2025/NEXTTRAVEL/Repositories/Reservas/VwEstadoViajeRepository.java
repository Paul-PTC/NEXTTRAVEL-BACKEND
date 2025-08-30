package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.VwEstadoViaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VwEstadoViajeRepository extends JpaRepository<VwEstadoViaje, Long> {

    Page<VwEstadoViaje> findByClienteContainingIgnoreCase(String q, Pageable p);
    Page<VwEstadoViaje> findByLugarContainingIgnoreCase(String q, Pageable p);
    Page<VwEstadoViaje> findByEstadoContainingIgnoreCase(String q, Pageable p);
    Page<VwEstadoViaje> findByIdReserva(Long idReserva, Pageable p);

    Page<VwEstadoViaje> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);
}
