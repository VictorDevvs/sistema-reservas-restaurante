package sistema.reservas_restaurante_api.validation;

import org.springframework.stereotype.Component;
import sistema.reservas_restaurante_api.model.MesaModel;
import sistema.reservas_restaurante_api.model.ReservaModel;
import sistema.reservas_restaurante_api.repositories.ReservaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ValidarMesaDisponivel {

    private final ReservaRepository reservaRepository;

    public ValidarMesaDisponivel(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public boolean isMesaDisponivel(MesaModel mesa, LocalDateTime dataHoraReserva){
        Optional<ReservaModel> reservaExistente = reservaRepository.findByMesaAndDataHoraReserva(mesa, dataHoraReserva);
        return reservaExistente.isEmpty();
    }
}
