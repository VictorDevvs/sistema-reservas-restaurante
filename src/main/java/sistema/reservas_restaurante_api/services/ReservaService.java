package sistema.reservas_restaurante_api.services;

import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sistema.reservas_restaurante_api.dtos.request.ReservaDTORequest;
import sistema.reservas_restaurante_api.dtos.response.ReservaDTOResponse;
import sistema.reservas_restaurante_api.exceptions.PermissaoNegadaException;
import sistema.reservas_restaurante_api.exceptions.reservaexceptions.*;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.UsuarioNaoEncontradoException;
import sistema.reservas_restaurante_api.mapper.ReservaMapper;
import sistema.reservas_restaurante_api.model.*;
import sistema.reservas_restaurante_api.repositories.MesaRepository;
import sistema.reservas_restaurante_api.repositories.ReservaRepository;
import sistema.reservas_restaurante_api.repositories.UsuarioRepository;
import sistema.reservas_restaurante_api.utils.RestauranteConstants;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final ReservaMapper mapper;

    public ReservaService(MesaRepository mesaRepository,
                          UsuarioRepository usuarioRepository, ReservaRepository reservaRepository, ReservaMapper mapper) {
        this.mesaRepository = mesaRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.mapper = mapper;
    }

    public List<ReservaDTOResponse> findAllByUsuario(String email){
        UsuarioModel usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
        List<ReservaModel> reservas = reservaRepository.findByUsuario(usuario);
        return reservas.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public ReservaDTOResponse criarReserva(ReservaDTORequest request, String emailUsuarioAutenticado) {
        UsuarioModel usuarioModel = usuarioRepository.findByEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário autenticado não encontrado"));

        MesaModel mesaModel = mesaRepository.findById(request.mesa())
                .orElseThrow(() -> new MesaNaoDisponivelException("Mesa não encontrada"));

        if (request.dataHoraReserva().isBefore(LocalDateTime.now())) {
            throw new DataHoraNaoPermitidaException("Erro: você não pode reservar para uma data e hora passada");
        }

        validarHorarioFuncionamento(request.dataHoraReserva());

        if (!isMesaDisponivel(mesaModel, request.dataHoraReserva())) {
            throw new MesaNaoDisponivelException("Mesa não disponível para esta data e hora");
        }

        if (request.numeroPessoas() > mesaModel.getCapacidade()) {
            throw new CapacidadeMesaException("Número de pessoas excede a capacidade suportada da mesa");
        }

        if (mesaModel.getStatus().equals(MesaStatus.RESERVADA) || mesaModel.getStatus().equals(MesaStatus.INATIVA)) {
            throw new MesaNaoDisponivelException("Mesa de número " + mesaModel.getNumero() + " não disponível");
        }

        ReservaModel reservaModel = mapper.toModel(request);
        reservaModel.setUsuario(usuarioModel);
        reservaModel.setMesa(mesaModel);
        reservaModel.setDataHoraReserva(request.dataHoraReserva());
        reservaModel.setStatus(ReservaStatus.ATIVA);
        mesaModel.setStatus(MesaStatus.RESERVADA);

        return mapper.toDto(reservaRepository.save(reservaModel));
    }

    @Transactional
    public void cancelar(Long id){
        ReservaModel reserva = reservaRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Nenhuma reserva encontrada com o id = " + id));
        if (reserva.getStatus().equals(ReservaStatus.CANCELADA) || reserva.getStatus().equals(ReservaStatus.CONCLUIDA)){
            throw new ReservaNaoAtivaException("Reserva com o id " + id + " não está ativa");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()){
            throw new UsuarioNaoEncontradoException("Usuário não autenticado");
        }

        String emailUsuarioAutenticado;
        if (authentication.getPrincipal() instanceof UserDetails) {
            emailUsuarioAutenticado = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            emailUsuarioAutenticado = authentication.getPrincipal().toString();
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE" + Role.ADMINISTRADOR.name()));

        if (!isAdmin){
            UsuarioModel usuarioAutenticado = usuarioRepository.findByEmail(emailUsuarioAutenticado).orElseThrow(
                    () -> new UsuarioNaoEncontradoException("Usuário autenticado não encontrado")
            );

            if (!reserva.getUsuario().getId().equals(usuarioAutenticado.getId())){
                throw new PermissaoNegadaException("Você não tem permissão para cancelar essa reserva");
            }
        }

        reserva.setStatus(ReservaStatus.CANCELADA);
        MesaModel model = reserva.getMesa();
        model.setStatus(MesaStatus.DISPONIVEL);
        mesaRepository.save(model);
        reservaRepository.save(reserva);
    }

    @Transactional
    protected void verificarReservasExpiradas(){
        LocalDateTime now = LocalDateTime.now();
        List<ReservaModel> reservas = reservaRepository.findAll();

        for (ReservaModel reserva : reservas) {
            if (reserva.getStatus() == ReservaStatus.ATIVA) {
                LocalDateTime fimReserva = reserva.getDataHoraReserva().plusHours(1);
                if (now.isAfter(fimReserva)) {
                    reserva.setStatus(ReservaStatus.CONCLUIDA);
                    MesaModel mesa = reserva.getMesa();
                    mesa.setStatus(MesaStatus.DISPONIVEL);
                    mesaRepository.save(mesa);
                    reservaRepository.save(reserva);
                }
            }
        }
    }

    private boolean isMesaDisponivel(MesaModel mesa, LocalDateTime dataHoraReserva){
        Optional<ReservaModel> reservaExistente = reservaRepository.findByMesaAndDataHoraReserva(mesa, dataHoraReserva);
        return reservaExistente.isEmpty();
    }

    private void validarHorarioFuncionamento(LocalDateTime dataHora) {
        LocalTime horaReserva = dataHora.toLocalTime();
        if (horaReserva.isBefore(RestauranteConstants.HORARIO_ABERTURA) || horaReserva.isAfter(RestauranteConstants.HORARIO_FECHAMENTO)) {
            String msg = String.format("Horário de reserva fora de expediente. O restaurante funciona das %s às %s.",
                    RestauranteConstants.HORARIO_ABERTURA, RestauranteConstants.HORARIO_FECHAMENTO);
            throw new HorarioForaExpedienteException(msg);
        }
    }

}
