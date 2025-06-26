package sistema.reservas_restaurante_api.mapper;

import org.mapstruct.Mapper;
import sistema.reservas_restaurante_api.dtos.request.UsuarioDTORequestRegistro;
import sistema.reservas_restaurante_api.dtos.response.UsuarioDTOResponseLogin;
import sistema.reservas_restaurante_api.dtos.response.UsuarioDTOResponseRegistro;
import sistema.reservas_restaurante_api.model.UsuarioModel;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioModel toModel (UsuarioDTORequestRegistro request);

    UsuarioDTOResponseLogin toLoginDto (UsuarioModel model);

    UsuarioDTOResponseRegistro toRegistroDto (UsuarioModel model);
}
