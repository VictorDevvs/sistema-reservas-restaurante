package sistema.reservas_restaurante_api.validation;

import org.springframework.stereotype.Component;
import sistema.reservas_restaurante_api.dtos.response.MesaDTOResponse;
import sistema.reservas_restaurante_api.exceptions.mesaexceptions.MesaExistenteException;
import sistema.reservas_restaurante_api.exceptions.reservaexceptions.CapacidadeMesaException;
import sistema.reservas_restaurante_api.exceptions.reservaexceptions.MesaNaoDisponivelException;
import sistema.reservas_restaurante_api.model.MesaModel;
import sistema.reservas_restaurante_api.model.MesaStatus;
import sistema.reservas_restaurante_api.model.ReservaModel;
import sistema.reservas_restaurante_api.repositories.MesaRepository;
import sistema.reservas_restaurante_api.repositories.ReservaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ValidacoesMesa {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;

    public ValidacoesMesa(ReservaRepository reservaRepository,
                          MesaRepository repository) {
        this.reservaRepository = reservaRepository;
        this.mesaRepository = repository;
    }


    public void mesaExistente(Integer numero){
        Optional<MesaDTOResponse> mesaExistente = mesaRepository.findByNumero(numero);
        if (mesaExistente.isPresent()){
            throw new MesaExistenteException("Mesa de número " + numero + " já existe no banco de dados");
        }
    }

    public void mesaDisponivel(MesaModel mesa, LocalDateTime dataHoraReserva){
        Optional<ReservaModel> reservaExistente = reservaRepository.findByMesaAndDataHoraReserva(mesa, dataHoraReserva);
        if (reservaExistente.isPresent()) {
            throw new MesaNaoDisponivelException("Mesa já reservada para o horário solicitado.");
        }
    }

    public void capacidadeExcedida(Integer numeroPessoas, Integer capacidadeMaxima) {
        if (numeroPessoas > capacidadeMaxima) {
            throw new CapacidadeMesaException("Capacidade máxima da mesa excedida. Capacidade máxima: "
                    + capacidadeMaxima);
        }
    }

    public void statusMesa(MesaModel model){
        if (model.getStatus().equals(MesaStatus.RESERVADA) || model.getStatus().equals(MesaStatus.INATIVA)) {
            throw new MesaNaoDisponivelException("Mesa de número " + model.getNumero() + " não disponível");
        }
    }

    public void atualizarNumeroMesa(Integer numeroAtualizacao, MesaModel model){
        if (numeroAtualizacao != null && !numeroAtualizacao.equals(model.getNumero())){
            mesaExistente(numeroAtualizacao);
            model.setNumero(numeroAtualizacao);
        }
    }

    public void atualizarCapacidadeMesa(Integer capacidadeAtualizacao, MesaModel model){
        if (capacidadeAtualizacao != null){
            model.setCapacidade(capacidadeAtualizacao);
        }
    }

    public void atualizarStatusMesa(MesaStatus statusAtualizacao, MesaModel model) {
        if (statusAtualizacao != null) {
            model.setStatus(statusAtualizacao);
        }
    }
}
