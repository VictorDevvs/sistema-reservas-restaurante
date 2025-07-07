package sistema.reservas_restaurante_api.validation;

import org.springframework.stereotype.Component;
import sistema.reservas_restaurante_api.exceptions.reservaexceptions.DataHoraNaoPermitidaException;
import sistema.reservas_restaurante_api.exceptions.reservaexceptions.HorarioForaExpedienteException;
import sistema.reservas_restaurante_api.utils.RestauranteConstants;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class ValidarHorarioFuncionamento {

    public void validarHorarioFuncionamento(LocalDateTime dataHora) {
        LocalTime horaReserva = dataHora.toLocalTime();
        if (horaReserva.isBefore(RestauranteConstants.HORARIO_ABERTURA) || horaReserva.isAfter(RestauranteConstants.HORARIO_FECHAMENTO)) {
            String msg = String.format("Horário de reserva fora de expediente. O restaurante funciona das %s às %s.",
                    RestauranteConstants.HORARIO_ABERTURA, RestauranteConstants.HORARIO_FECHAMENTO);
            throw new HorarioForaExpedienteException(msg);
        }
    }

    public void ultimaReserva(LocalDateTime dataHoraReserva){
        var ultimaReserva = RestauranteConstants.HORARIO_FECHAMENTO.minusHours(1);
        if (dataHoraReserva.toLocalTime().isAfter(ultimaReserva)){
            throw new DataHoraNaoPermitidaException("A última reserva deve ser feita até uma hora antes do fechamento do restaurante.");
        }
    }
}
