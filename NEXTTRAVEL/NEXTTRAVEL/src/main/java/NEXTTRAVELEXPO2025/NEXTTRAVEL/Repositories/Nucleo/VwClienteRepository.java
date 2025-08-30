package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.VwCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VwClienteRepository extends JpaRepository<VwCliente, String> {
    Page<VwCliente> findByNombreContainingIgnoreCase(String q, Pageable p);
    Page<VwCliente> findByCorreoContainingIgnoreCase(String q, Pageable p);
    Page<VwCliente> findByTelefonoContainingIgnoreCase(String q, Pageable p);
    Page<VwCliente> findByDireccionContainingIgnoreCase(String q, Pageable p);

    Page<VwCliente> findByPuntosActualesBetween(Integer min, Integer max, Pageable p);
    Page<VwCliente> findByFechaRegistroBetween(LocalDateTime desde, LocalDateTime hasta, Pageable p);
}
