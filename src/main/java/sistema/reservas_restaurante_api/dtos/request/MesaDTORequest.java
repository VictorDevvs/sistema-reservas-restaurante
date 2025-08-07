package sistema.reservas_restaurante_api.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import sistema.reservas_restaurante_api.model.MesaStatus;

@Schema(description = "Dados para a requisição de criação ou atualização de uma mesa")
public record  MesaDTORequest(
        @Schema(description = "Número único da mesa. Deve ser um valor inteiro.", example = "5")
        @Column(nullable = false, unique = true)
        @NotNull
        Integer numero,

        @Schema(description = "Capacidade máxima de pessoas que a mesa pode acomodar. Deve ser no mínimo 1.", example = "4")
        @Column(nullable = false)
        @NotNull
        @Min(1)
        Integer capacidade,

        @Schema(description = "Status atual da mesa. Pode ser DISPONIVEL ou INDISPONIVEL.", example = "DISPONIVEL")
        @Column(nullable = false)
        @NotNull
        @Enumerated(EnumType.STRING)
        MesaStatus status
) {
}
