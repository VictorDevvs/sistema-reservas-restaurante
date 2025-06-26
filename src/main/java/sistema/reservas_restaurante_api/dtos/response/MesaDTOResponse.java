package sistema.reservas_restaurante_api.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import sistema.reservas_restaurante_api.model.MesaStatus;

@Schema(description = "Dados da resposta de uma mesa")
public record MesaDTOResponse (
        @Schema(description = "Número único da mesa.", example = "5")
        Integer numero,
        @Schema(description = "Capacidade máxima de pessoas da mesa.", example = "4")
        Integer capacidade,
        @Schema(description = "Status atual da mesa.", example = "DISPONIVEL")
        MesaStatus status
){
}
