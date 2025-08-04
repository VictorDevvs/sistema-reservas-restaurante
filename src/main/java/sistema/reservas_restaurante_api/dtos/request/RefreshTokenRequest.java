package sistema.reservas_restaurante_api.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Schema(description = "Dados para a requisição de renovação do token de autenticação")
public class RefreshTokenRequest {

    @Schema(description = "Token de atualização (refresh token) para renovar o token de acesso",
            example = "123e4567-e89b-12d3-a456-426614174000")
    @NotBlank
    private UUID refreshToken;

    public UUID getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(UUID refreshToken) {
        this.refreshToken = refreshToken;
    }
}
