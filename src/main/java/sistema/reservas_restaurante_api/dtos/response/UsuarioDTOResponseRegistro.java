package sistema.reservas_restaurante_api.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import sistema.reservas_restaurante_api.model.Role;

@Schema(description = "Dados da resposta após o registro de um novo usuário")
public record UsuarioDTOResponseRegistro (
        @Schema(description = "ID único do usuário registrado.", example = "1")
        Long id,
        @Schema(description = "Nome completo do usuário registrado.", example = "João da Silva")
        String nome,
        @Schema(description = "Email do usuário registrado.", example = "joao.silva@example.com")
        String email,
        @Schema(description = "Nível de acesso do usuário (CLIENTE, ADMINISTRADOR).", example = "CLIENTE")
        Role role
) {
}
