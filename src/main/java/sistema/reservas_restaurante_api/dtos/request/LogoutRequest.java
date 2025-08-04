package sistema.reservas_restaurante_api.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para a requisição de logout do usuário")
public class LogoutRequest {

    @Schema(description = "Token de acesso do usuário que deseja fazer logout", example = "eyJraWQiOiJrZ3d6b2V5bGfQ...")
    @NotBlank
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
