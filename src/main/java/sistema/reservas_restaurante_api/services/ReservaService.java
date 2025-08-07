package sistema.reservas_restaurante_api.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import sistema.reservas_restaurante_api.dtos.request.ReservaDTORequest;
import sistema.reservas_restaurante_api.dtos.response.ReservaDTOResponse;
import sistema.reservas_restaurante_api.exceptions.PermissaoNegadaException;
import sistema.reservas_restaurante_api.exceptions.reservaexceptions.*;
import sistema.reservas_restaurante_api.mapper.ReservaMapper;
import sistema.reservas_restaurante_api.model.*;
import sistema.reservas_restaurante_api.repositories.MesaRepository;
import sistema.reservas_restaurante_api.repositories.ReservaRepository;
import sistema.reservas_restaurante_api.repositories.UsuarioRepository;
import sistema.reservas_restaurante_api.validation.ValidacoesReserva;
import sistema.reservas_restaurante_api.validation.ValidarAutenticacaoAutorizacaoUsuario;
import sistema.reservas_restaurante_api.validation.ValidacoesHorario;
import sistema.reservas_restaurante_api.validation.ValidacoesMesa;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final ReservaMapper mapper;
    private final ValidacoesHorario validarHorario;
    private final ValidacoesMesa validacoesMesa;
    private final ValidarAutenticacaoAutorizacaoUsuario validarAutenticacaoAutorizacaoUsuario;
    private final ValidacoesReserva validacoesReserva;

    public ReservaService(MesaRepository mesaRepository,
                          UsuarioRepository usuarioRepository, ReservaRepository reservaRepository, ReservaMapper mapper,
                          ValidacoesHorario validarHorario, ValidacoesMesa validacoesMesa,
                          ValidarAutenticacaoAutorizacaoUsuario validarAutenticacaoAutorizacaoUsuario,
                          ValidacoesReserva validacoesReserva) {
        this.mesaRepository = mesaRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.mapper = mapper;
        this.validarHorario = validarHorario;
        this.validacoesMesa = validacoesMesa;
        this.validarAutenticacaoAutorizacaoUsuario = validarAutenticacaoAutorizacaoUsuario;
        this.validacoesReserva = validacoesReserva;
    }

    public List<ReservaDTOResponse> buscarReservasPorUsuario(){
        UsuarioModel usuario = validarAutenticacaoAutorizacaoUsuario.getUsuarioAutenticado();
        List<ReservaModel> reservas = reservaRepository.findByUsuario(usuario);
        return reservas.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public ReservaDTOResponse criarReserva(ReservaDTORequest request) {
        UsuarioModel usuarioModel = validarAutenticacaoAutorizacaoUsuario.getUsuarioAutenticado();

        MesaModel mesaModel = mesaRepository.findById(request.mesa())
                .orElseThrow(() -> new MesaNaoDisponivelException("Mesa não encontrada"));

        validarHorario.validarHorarioFuncionamento(request.dataHoraReserva());
        validarHorario.ultimaReserva(request.dataHoraReserva());
        validarHorario.validarHorarioReserva(request.dataHoraReserva());
        validacoesMesa.mesaDisponivel(mesaModel, request.dataHoraReserva());
        validacoesMesa.capacidadeExcedida(request.numeroPessoas(), mesaModel.getCapacidade());
        validacoesMesa.statusMesa(mesaModel);

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
        validacoesReserva.verificarReservaExistente(reserva, id);

        boolean isAdmin = validarAutenticacaoAutorizacaoUsuario.isCurrentUserAdmin();
        validacoesReserva.verificarPermissao(isAdmin, reserva, usuarioAutenticado);

        reserva.setStatus(ReservaStatus.CANCELADA);
        MesaModel model = reserva.getMesa();
        model.setStatus(MesaStatus.DISPONIVEL);
        mesaRepository.save(model);
        reservaRepository.save(reserva);
    }

    @Transactional
    protected void verificarReservasExpiradas(){
        validacoesReserva.verificarReservas();
    }
}
