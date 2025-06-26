package sistema.reservas_restaurante_api.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TarefaAgendamentoService {

    private final ReservaService reservaService;

    public TarefaAgendamentoService(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void verificarReservasAutomaticamente() {
        reservaService.verificarReservasExpiradas();
    }
}
