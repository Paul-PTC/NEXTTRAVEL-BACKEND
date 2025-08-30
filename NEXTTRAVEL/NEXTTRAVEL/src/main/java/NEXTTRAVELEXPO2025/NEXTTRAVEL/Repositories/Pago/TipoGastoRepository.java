package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.TipoGasto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoGastoRepository extends JpaRepository<TipoGasto, Long> {

    boolean existsByNombreTipoIgnoreCase(String nombreTipo);

    Page<TipoGasto> findByNombreTipoContainingIgnoreCase(String q, Pageable p);

    Page<TipoGasto> findByDescripcionContainingIgnoreCase(String q, Pageable p);
}
