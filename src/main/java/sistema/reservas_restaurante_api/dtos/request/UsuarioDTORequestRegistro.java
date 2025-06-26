package sistema.reservas_restaurante_api.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sistema.reservas_restaurante_api.model.Role;

@Schema(description = "Dados para a requisição de registro de um novo usuário")
public record UsuarioDTORequestRegistro (
        @Schema(description = "Nome completo do usuário.", example = "João da Silva")
        @Column(nullable = false, length = 100)
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Schema(description = "Endereço de email do usuário. Deve ser único.", example = "joao.silva@example.com")
        @Column(nullable = false, length = 100)
        @Email(message = "Email em formato inválido")
        @NotBlank(message = "Email é obrigatório")
        String email,

        @Schema(description = "Senha do usuário. Deve atender aos requisitos de segurança (mín. 8 caracteres, maiúscula, minúscula, número, especial).", example = "SenhaNova123@")
        @Column(nullable = false, length = 100)
        @NotBlank(message = "Senha é obrigatória")
        String senha,

        @Schema(description = "Nível de acesso do usuário. Pode ser CLIENTE ou ADMINISTRADOR.", example = "CLIENTE")
        @Column(nullable = false)
        @NotNull(message = "Role é obrigatório")
        @Enumerated(EnumType.STRING)
        Role role)
{
}
