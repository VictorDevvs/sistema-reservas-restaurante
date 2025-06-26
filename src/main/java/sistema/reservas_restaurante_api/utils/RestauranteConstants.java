package sistema.reservas_restaurante_api.utils;

import java.time.LocalTime;

public class RestauranteConstants {

    private RestauranteConstants(){}

    public static final LocalTime HORARIO_ABERTURA = LocalTime.of(18, 0);
    public static final LocalTime HORARIO_FECHAMENTO = LocalTime.of(23, 0);
}
