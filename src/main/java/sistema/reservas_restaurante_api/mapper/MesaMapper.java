package sistema.reservas_restaurante_api.mapper;

import org.mapstruct.Mapper;
import sistema.reservas_restaurante_api.dtos.request.MesaDTORequest;
import sistema.reservas_restaurante_api.dtos.response.MesaDTOResponse;
import sistema.reservas_restaurante_api.model.MesaModel;

@Mapper(componentModel = "spring")
public interface MesaMapper {

    MesaModel toModel (MesaDTORequest request);

    MesaDTOResponse toDto (MesaModel model);
}
