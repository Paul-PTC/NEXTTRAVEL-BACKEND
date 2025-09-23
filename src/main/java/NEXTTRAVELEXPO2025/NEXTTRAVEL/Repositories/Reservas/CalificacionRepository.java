package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Calificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    Page<Calificacion> findByComentarioContainingIgnoreCase(String q, Pageable p);

    Page<Calificacion> findByPuntuacion(Integer n, Pageable p);
    Page<Calificacion> findByPuntuacionBetween(Integer min, Integer max, Pageable p);

    Page<Calificacion> findByReserva_IdReserva(Long idReserva, Pageable p);
}
