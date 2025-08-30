package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.LugarTuristico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LugarTuristicoRepository extends JpaRepository<LugarTuristico, Long> {

    Page<LugarTuristico> findByNombreLugarContainingIgnoreCase(String q, Pageable p);
    Page<LugarTuristico> findByUbicacionContainingIgnoreCase(String q, Pageable p);
    Page<LugarTuristico> findByTipoContainingIgnoreCase(String q, Pageable p);
    Page<LugarTuristico> findByDescripcionContainingIgnoreCase(String q, Pageable p);

    boolean existsByNombreLugarIgnoreCase(String nombreLugar);
}