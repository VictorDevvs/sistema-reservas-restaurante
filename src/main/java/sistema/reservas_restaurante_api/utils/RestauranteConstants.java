package sistema.reservas_restaurante_api.utils;

import sistema.reservas_restaurante_api.exceptions.reservaexceptions.DataHoraNaoPermitidaException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RestauranteConstants {

    private RestauranteConstants(){}

    public static final LocalTime HORARIO_ABERTURA = LocalTime.of(18, 0);
    public static final LocalTime HORARIO_FECHAMENTO = LocalTime.of(23, 0);

    public static void ultimaReserva(LocalDateTime dataHoraReserva){
        var ultimaReserva = HORARIO_FECHAMENTO.minusHours(1);
        if (dataHoraReserva.toLocalTime().isAfter(ultimaReserva)){
            throw new DataHoraNaoPermitidaException("A última reserva deve ser feita até uma hora antes do fechamento do restaurante.");
        }
    }
}
