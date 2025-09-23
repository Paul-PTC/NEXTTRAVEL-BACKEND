package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Promocion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    Page<Promocion> findByNombreContainingIgnoreCase(String q, Pageable p);
    Page<Promocion> findByDescripcionContainingIgnoreCase(String q, Pageable p);

    Page<Promocion> findByDescuentoPorcentaje(Integer n, Pageable p);
    Page<Promocion> findByDescuentoPorcentajeBetween(Integer min, Integer max, Pageable p);

    // Vigentes en una fecha d: (inicio <= d) AND (fin >= d) considerando nulls como "sin límite"
    @Query("""
           SELECT pr FROM Promocion pr
           WHERE (:d IS NULL OR (COALESCE(pr.fechaInicio, :d) <= :d AND COALESCE(pr.fechaFin, :d) >= :d))
           """)
    Page<Promocion> findVigentesEn(@Param("d") LocalDate d, Pageable p);

    // Overlap con rango [desde, hasta]
    @Query("""
           SELECT pr FROM Promocion pr
           WHERE (:desde IS NULL OR pr.fechaFin IS NULL OR pr.fechaFin >= :desde)
             AND (:hasta IS NULL OR pr.fechaInicio IS NULL OR pr.fechaInicio <= :hasta)
           """)
    Page<Promocion> findByRangoSolapado(@Param("desde") LocalDate desde,
                                        @Param("hasta") LocalDate hasta,
                                        Pageable p);
}
