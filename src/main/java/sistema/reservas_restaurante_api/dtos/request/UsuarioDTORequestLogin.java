package sistema.reservas_restaurante_api.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para a requisição de login de usuário")
public record UsuarioDTORequestLogin(
        @Schema(description = "Endereço de email do usuário.", example = "usuario@example.com")
        @Column(nullable = false, length = 100)
        @Email(message = "Email inválido")
        @NotBlank(message = "Email é obrigatório")
        String email,

        @Schema(description = "Senha do usuário. Deve ter no mínimo 8 caracteres.", example = "SenhaSegura123!")
        @Column(nullable = false, length = 100)
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8)
        String senha
) {
}
