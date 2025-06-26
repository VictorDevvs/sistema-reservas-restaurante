package sistema.reservas_restaurante_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sistema.reservas_restaurante_api.dtos.request.ReservaDTORequest;
import sistema.reservas_restaurante_api.dtos.response.ReservaDTOResponse;
import sistema.reservas_restaurante_api.model.MesaModel;
import sistema.reservas_restaurante_api.model.ReservaModel;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    ReservaModel toModel(ReservaDTORequest request);

    @Mapping(source = "mesa.id", target = "mesa")
    ReservaDTOResponse toDto(ReservaModel model);

    default MesaModel mapMesaIdToMesaModel(Long mesaId) {
        if (mesaId == null) return null;
        MesaModel mesa = new MesaModel();
        mesa.setId(mesaId);
        return mesa;
    }
}
