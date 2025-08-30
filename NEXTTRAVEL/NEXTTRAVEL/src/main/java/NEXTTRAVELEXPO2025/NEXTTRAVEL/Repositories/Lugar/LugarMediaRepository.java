package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.LugarMedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LugarMediaRepository extends JpaRepository<LugarMedia, Long> {
    boolean existsByLugar_IdLugarAndPosition(Long idLugar, Integer position);
}
