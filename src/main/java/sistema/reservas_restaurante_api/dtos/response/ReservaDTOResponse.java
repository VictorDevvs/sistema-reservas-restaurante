package sistema.reservas_restaurante_api.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import sistema.reservas_restaurante_api.model.ReservaStatus;
import java.time.LocalDateTime;

@Schema(description = "Dados da resposta de uma reserva")
public record ReservaDTOResponse(
        @Schema(description = "ID da mesa associada à reserva.", example = "101")
        Long mesa,
        @Schema(description = "Número de pessoas para esta reserva.", example = "2")
        Integer numeroPessoas,
        @Schema(description = "Data e hora exata da reserva.", example = "2025-07-20T20:00:00")
        LocalDateTime dataHoraReserva,
        @Schema(description = "Status atual da reserva (ATIVA, CANCELADA, CONCLUIDA).", example = "ATIVA")
        ReservaStatus status
) {
}
