package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Ganancia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GananciaRepository extends JpaRepository<Ganancia, Long> {
    boolean existsByReserva_IdReserva(Long idReserva);
}
