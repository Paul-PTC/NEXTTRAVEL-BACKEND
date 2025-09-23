package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Cliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.MovimientoPuntos;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.PuntosCliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
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

    private void validateTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BadRequestException("El campo 'tipo' es obligatorio.");
        }
        if (!tipo.equalsIgnoreCase("ACREDITACION") && !tipo.equalsIgnoreCase("CONSUMO")) {
            throw new BadRequestException("El campo 'tipo' debe ser 'ACREDITACION' o 'CONSUMO'.");
        }
    }

    private void validatePuntos(Integer puntos) {
        if (puntos == null || puntos <= 0) {
            throw new BadRequestException("Los 'puntosCambiados' deben ser mayores que 0.");
        }
    }

    // ===== Listado / Búsquedas =====
    public Page<MovimientoPuntosDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorDui(String q, Pageable p) {
        if (q == null || q.isBlank()) {
            throw new BadRequestException("El parámetro 'duiCliente' no puede estar vacío.");
        }
        return repo.findByCliente_DuiContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorTipo(String tipo, Pageable p) {
        validateTipo(tipo);
        return repo.findByTipo(tipo.toUpperCase(), p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorDescripcion(String q, Pageable p) {
        if (q == null || q.isBlank()) {
            throw new BadRequestException("El parámetro 'descripcion' no puede estar vacío.");
        }
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorReserva(Long idReserva, Pageable p) {
        if (idReserva == null || idReserva <= 0) {
            throw new BadRequestException("El parámetro 'idReserva' no es válido.");
        }
        return repo.findByReserva_IdReserva(idReserva, p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorPuntos(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 1;
        int to   = (max != null) ? max : Integer.MAX_VALUE;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPuntosCambiadosBetween(from, to, p).map(this::toDTO);
    }

    public Page<MovimientoPuntosDTO> buscarPorFecha(LocalDateTime d, LocalDateTime h, Pageable p) {
        if (d == null || h == null) {
            throw new BadRequestException("El rango de fechas es obligatorio.");
        }
        return repo.findByFechaBetween(d, h, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid MovimientoPuntosDTO dto) {
        Cliente cliente = clienteRepo.findById(dto.getDuiCliente())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Cliente con DUI: " + dto.getDuiCliente()));

        Reserva reserva = null;
        if (dto.getIdReserva() != null) {
            reserva = reservaRepo.findById(dto.getIdReserva())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
        }

        validateTipo(dto.getTipo());
        validatePuntos(dto.getPuntosCambiados());

        int change = signed(dto.getTipo(), dto.getPuntosCambiados());
        PuntosCliente saldo = getOrCreateSaldo(dto.getDuiCliente());

        int nuevo = saldo.getPuntos() + change;
        if (nuevo < 0) {
            throw new BadRequestException("El movimiento dejaría el saldo de puntos en negativo.");
        }

        MovimientoPuntos mov = MovimientoPuntos.builder()
                .cliente(cliente)
                .reserva(reserva)
                .tipo(dto.getTipo().toUpperCase())
                .puntosCambiados(dto.getPuntosCambiados())
                .descripcion(dto.getDescripcion())
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                .build();

        MovimientoPuntos g = repo.save(mov);
        saldo.setPuntos(nuevo);
        puntosRepo.save(saldo);

        log.info("MovimientoPuntos creado id={} dui={} tipo={} puntos={} saldo={}",
                g.getIdMovimiento(), cliente.getDui(), dto.getTipo(), dto.getPuntosCambiados(), nuevo);

        return g.getIdMovimiento();
    }

    // ===== Actualizar =====
    @Transactional
    public void actualizar(Long id, @Valid MovimientoPuntosDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        MovimientoPuntos e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró MovimientoPuntos con id: " + id));

        String oldTipo = e.getTipo();
        int oldSigned = signed(oldTipo, e.getPuntosCambiados());

        if (dto.getDuiCliente() != null &&
                (e.getCliente() == null || !dto.getDuiCliente().equals(e.getCliente().getDui()))) {
            Cliente c = clienteRepo.findById(dto.getDuiCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe Cliente con DUI: " + dto.getDuiCliente()));
            e.setCliente(c);
        }
        if (dto.getIdReserva() != null) {
            Reserva r = reservaRepo.findById(dto.getIdReserva())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
            e.setReserva(r);
        }

        if (dto.getTipo() != null) {
            validateTipo(dto.getTipo());
            e.setTipo(dto.getTipo().toUpperCase());
        }
        if (dto.getPuntosCambiados() != null) {
            validatePuntos(dto.getPuntosCambiados());
            e.setPuntosCambiados(dto.getPuntosCambiados());
        }
        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        if (dto.getFecha() != null) e.setFecha(dto.getFecha());

        String newTipo = e.getTipo();
        int newSigned = signed(newTipo, e.getPuntosCambiados());

        String dui = e.getCliente().getDui();
        PuntosCliente saldo = getOrCreateSaldo(dui);

        int nuevoSaldo = saldo.getPuntos() - oldSigned + newSigned;
        if (nuevoSaldo < 0) {
            throw new BadRequestException("La actualización dejaría el saldo de puntos en negativo.");
        }

        repo.save(e);
        saldo.setPuntos(nuevoSaldo);
        puntosRepo.save(saldo);

        log.info("MovimientoPuntos actualizado id={} dui={} delta={} saldo={}",
                id, dui, (newSigned - oldSigned), nuevoSaldo);
    }

    // ===== Eliminar (revierte el efecto) =====
    @Transactional
    public boolean eliminar(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        MovimientoPuntos e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró MovimientoPuntos con id: " + id));

        String dui = e.getCliente().getDui();
        int revert = -signed(e.getTipo(), e.getPuntosCambiados());
        PuntosCliente saldo = getOrCreateSaldo(dui);

        int nuevoSaldo = saldo.getPuntos() + revert;
        if (nuevoSaldo < 0) {
            throw new BadRequestException("No se puede eliminar: dejaría el saldo en negativo.");
        }

        repo.deleteById(id);
        saldo.setPuntos(nuevoSaldo);
        puntosRepo.save(saldo);

        log.info("MovimientoPuntos eliminado id={} dui={} revert={} saldo={}", id, dui, revert, nuevoSaldo);
        return true;
    }
}
