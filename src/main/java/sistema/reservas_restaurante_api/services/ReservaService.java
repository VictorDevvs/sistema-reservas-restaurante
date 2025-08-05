package sistema.reservas_restaurante_api.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import sistema.reservas_restaurante_api.dtos.request.ReservaDTORequest;
import sistema.reservas_restaurante_api.dtos.response.ReservaDTOResponse;
import sistema.reservas_restaurante_api.exceptions.reservaexceptions.*;
import sistema.reservas_restaurante_api.mapper.ReservaMapper;
import sistema.reservas_restaurante_api.model.*;
import sistema.reservas_restaurante_api.repositories.MesaRepository;
import sistema.reservas_restaurante_api.repositories.ReservaRepository;
import sistema.reservas_restaurante_api.repositories.UsuarioRepository;
import sistema.reservas_restaurante_api.validation.ValidarAutenticacaoAutorizacaoUsuario;
import sistema.reservas_restaurante_api.validation.ValidarHorarioFuncionamento;
import sistema.reservas_restaurante_api.validation.ValidarMesaDisponivel;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final ReservaMapper mapper;
    private final ValidarHorarioFuncionamento validarHorarioFuncionamento;
    private final ValidarMesaDisponivel validarMesaDisponivel;
    private final ValidarAutenticacaoAutorizacaoUsuario validarAutenticacaoAutorizacaoUsuario;

    public ReservaService(MesaRepository mesaRepository,
                          UsuarioRepository usuarioRepository, ReservaRepository reservaRepository, ReservaMapper mapper,
                          ValidarHorarioFuncionamento validarHorarioFuncionamento, ValidarMesaDisponivel validarMesaDisponivel,
                          ValidarAutenticacaoAutorizacaoUsuario validarAutenticacaoAutorizacaoUsuario) {
        this.mesaRepository = mesaRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.mapper = mapper;
        this.validarHorarioFuncionamento = validarHorarioFuncionamento;
        this.validarMesaDisponivel = validarMesaDisponivel;
        this.validarAutenticacaoAutorizacaoUsuario = validarAutenticacaoAutorizacaoUsuario;
    }

    public List<ReservaDTOResponse> findAllByUsuario(){
        UsuarioModel usuario = validarAutenticacaoAutorizacaoUsuario.getUsuarioAutenticado();
        List<ReservaModel> reservas = reservaRepository.findByUsuario(usuario);
        return reservas.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public ReservaDTOResponse criarReserva(ReservaDTORequest request) {
        UsuarioModel usuarioModel = validarAutenticacaoAutorizacaoUsuario.getUsuarioAutenticado();

        MesaModel mesaModel = mesaRepository.findById(request.mesa())
                .orElseThrow(() -> new MesaNaoDisponivelException("Mesa não encontrada"));

        if (request.dataHoraReserva().isBefore(LocalDateTime.now())) {
            throw new DataHoraNaoPermitidaException("Erro: você não pode reservar para uma data e hora passada");
        }

        validarHorarioFuncionamento.validarHorarioFuncionamento(request.dataHoraReserva());
        validarHorarioFuncionamento.ultimaReserva(request.dataHoraReserva());

        if (!validarMesaDisponivel.isMesaDisponivel(mesaModel, request.dataHoraReserva())) {
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
    public void cancelarReserva(Long id){
        UsuarioModel usuarioAutenticado = validarAutenticacaoAutorizacaoUsuario.getUsuarioAutenticado();

        ReservaModel reserva = reservaRepository.findById(id).orElseThrow(() ->
                new ReservaNaoEncontradaException("Nenhuma reserva encontrada com o id = " + id));
        if (reserva.getStatus().equals(ReservaStatus.CANCELADA) || reserva.getStatus().equals(ReservaStatus.CONCLUIDA)){
            throw new ReservaNaoAtivaException("Reserva com o id " + id + " não está ativa");
        }

        boolean isAdmin = validarAutenticacaoAutorizacaoUsuario.isCurrentUserAdmin();
        if (!isAdmin){
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
}
