package sistema.reservas_restaurante_api.dtos.request;

import sistema.reservas_restaurante_api.model.MesaStatus;

public record AtualizarMesaDTORequest(
        Integer numero,
        Integer capacidade,
        MesaStatus status
) {
}
