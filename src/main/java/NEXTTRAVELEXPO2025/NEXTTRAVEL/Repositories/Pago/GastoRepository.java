package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> { }
