package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.Vehiculo;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    boolean existsByPlacaIgnoreCase(String placa);

    Page<Vehiculo> findByPlacaContainingIgnoreCase(String q, Pageable p);

    Page<Vehiculo> findByModeloContainingIgnoreCase(String q, Pageable p);

    Page<Vehiculo> findByEstadoContainingIgnoreCase(String q, Pageable p);

    Page<Vehiculo> findByCapacidadBetween(Integer min, Integer max, Pageable p);

    Page<Vehiculo> findByAnioFabricacionBetween(Integer min, Integer max, Pageable p);
}
