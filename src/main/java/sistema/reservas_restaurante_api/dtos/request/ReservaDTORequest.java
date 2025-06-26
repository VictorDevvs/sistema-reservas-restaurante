package sistema.reservas_restaurante_api.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "Dados para a requisição de criação de uma reserva")
public record ReservaDTORequest (
        @Schema(description = "ID da mesa que está sendo reservada.", example = "101")
        @NotNull
        Long mesa,

        @Schema(description = "Número de pessoas para esta reserva. Deve ser compatível com a capacidade da mesa.", example = "2")
        @NotNull
        Integer numeroPessoas,

        @Schema(description = "Data e hora no formato ISO 8601 (yyyy-MM-dd'T'HH:mm:ss)", example = "2025-06-25T22:00:00")
        @NotNull
        LocalDateTime dataHoraReserva
) {
}
