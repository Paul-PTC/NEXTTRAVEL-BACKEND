package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Cliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.MovimientoPuntos;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.PuntosCliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.MovimientoPuntosDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.ClienteRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.MovimientoPuntosRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.PuntosClienteRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovimientoPuntosService {

    private final MovimientoPuntosRepository repo;
    private final ClienteRepository clienteRepo;
    private final ReservaRepository reservaRepo;
    private final PuntosClienteRepository puntosRepo;

    private MovimientoPuntosDTO toDTO(MovimientoPuntos e) {
        return MovimientoPuntosDTO.builder()
                .duiCliente(e.getCliente() != null ? e.getCliente().getDui() : null)
                .idReserva(e.getReserva() != null ? e.getReserva().getIdReserva() : null)
                .tipo(e.getTipo())
                .puntosCambiados(e.getPuntosCambiados())
                .descripcion(e.getDescripcion())
                .fecha(e.getFecha())
                .build();
    }

    private int signed(String tipo, int puntos) {
        return "ACREDITACION".equalsIgnoreCase(tipo) ? puntos : -puntos;
    }

    private PuntosCliente getOrCreateSaldo(String dui) {
        return puntosRepo.findById(dui).orElseGet(() ->
                puntosRepo.save(PuntosCliente.builder().duiCliente(dui).puntos(0).build())
        );
    }

    // ===== Listado / Búsquedas =====
    public Page<MovimientoPuntosDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorDui(String q, Pageable p) {
        return repo.findByCliente_DuiContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorTipo(String tipo, Pageable p) {
        return repo.findByTipo(tipo.toUpperCase(), p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorDescripcion(String q, Pageable p) {
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorReserva(Long idReserva, Pageable p) {
        return repo.findByReserva_IdReserva(idReserva, p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorPuntos(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 1;
        int to   = (max != null) ? max : Integer.MAX_VALUE;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPuntosCambiadosBetween(from, to, p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorFecha(java.time.LocalDateTime d, java.time.LocalDateTime h, Pageable p) {
        return repo.findByFechaBetween(d, h, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid MovimientoPuntosDTO dto) {
        Cliente cliente = clienteRepo.findById(dto.getDuiCliente())
                .orElseThrow(() -> new EntityNotFoundException("No existe Cliente con DUI: " + dto.getDuiCliente()));

        Reserva reserva = null;
        if (dto.getIdReserva() != null) {
            reserva = reservaRepo.findById(dto.getIdReserva())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
        }

        String tipo = dto.getTipo().toUpperCase();
        if (!tipo.equals("ACREDITACION") && !tipo.equals("CONSUMO")) {
            throw new IllegalArgumentException("tipo debe ser ACREDITACION o CONSUMO");
        }

        int change = signed(tipo, dto.getPuntosCambiados());

        PuntosCliente saldo = getOrCreateSaldo(dto.getDuiCliente());
        int nuevo = saldo.getPuntos() + change;
        if (nuevo < 0) throw new IllegalArgumentException("El movimiento dejaría el saldo en negativo.");

        // Persistimos movimiento
        MovimientoPuntos mov = MovimientoPuntos.builder()
                .cliente(cliente)
                .reserva(reserva)
                .tipo(tipo)
                .puntosCambiados(dto.getPuntosCambiados())
                .descripcion(dto.getDescripcion())
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                .build();
        MovimientoPuntos g = repo.save(mov);

        // Actualizamos saldo
        saldo.setPuntos(nuevo);
        puntosRepo.save(saldo);

        log.info("MovimientoPuntos creado id={} dui={} tipo={} puntos={} saldo={}",
                g.getIdMovimiento(), cliente.getDui(), tipo, dto.getPuntosCambiados(), nuevo);
        return g.getIdMovimiento();
    }

    // ===== Actualizar =====
    @Transactional
    public void actualizar(Long id, @Valid MovimientoPuntosDTO dto) {
        MovimientoPuntos e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró MovimientoPuntos con id: " + id));

        String oldTipo = e.getTipo();
        int oldSigned = signed(oldTipo, e.getPuntosCambiados());

        // Cambios de relaciones
        if (dto.getDuiCliente() != null && (e.getCliente() == null || !dto.getDuiCliente().equals(e.getCliente().getDui()))) {
            Cliente c = clienteRepo.findById(dto.getDuiCliente())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Cliente con DUI: " + dto.getDuiCliente()));
            e.setCliente(c);
        }
        if (dto.getIdReserva() != null) {
            Reserva r = reservaRepo.findById(dto.getIdReserva())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
            e.setReserva(r);
        }

        // Datos básicos
        if (dto.getTipo() != null) e.setTipo(dto.getTipo().toUpperCase());
        if (dto.getPuntosCambiados() != null) e.setPuntosCambiados(dto.getPuntosCambiados());
        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        if (dto.getFecha() != null) e.setFecha(dto.getFecha());

        // Recalcular delta y ajustar saldo del (nuevo) cliente
        String newTipo = e.getTipo();
        if (!newTipo.equals("ACREDITACION") && !newTipo.equals("CONSUMO"))
            throw new IllegalArgumentException("tipo debe ser ACREDITACION o CONSUMO");

        int newSigned = signed(newTipo, e.getPuntosCambiados());

        // Nota: si cambió el DUI, el ajuste se hace sobre el nuevo DUI
        String dui = e.getCliente().getDui();
        PuntosCliente saldo = getOrCreateSaldo(dui);

        int nuevoSaldo = saldo.getPuntos() - oldSigned + newSigned;
        if (nuevoSaldo < 0) throw new IllegalArgumentException("La actualización dejaría el saldo en negativo.");

        repo.save(e);
        saldo.setPuntos(nuevoSaldo);
        puntosRepo.save(saldo);

        log.info("MovimientoPuntos actualizado id={} dui={} delta={} saldo={}",
                id, dui, (newSigned - oldSigned), nuevoSaldo);
    }

    // ===== Eliminar (revierte el efecto) =====
    @Transactional
    public boolean eliminar(Long id) {
        MovimientoPuntos e = repo.findById(id).orElse(null);
        if (e == null) return false;

        String dui = e.getCliente().getDui();
        int revert = -signed(e.getTipo(), e.getPuntosCambiados()); // revertir efecto
        PuntosCliente saldo = getOrCreateSaldo(dui);

        int nuevoSaldo = saldo.getPuntos() + revert;
        if (nuevoSaldo < 0)
            throw new IllegalArgumentException("No se puede eliminar: dejaría el saldo en negativo.");

        repo.deleteById(id);
        saldo.setPuntos(nuevoSaldo);
        puntosRepo.save(saldo);

        log.info("MovimientoPuntos eliminado id={} dui={} revert={} saldo={}", id, dui, revert, nuevoSaldo);
        return true;
    }
}
