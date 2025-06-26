package sistema.reservas_restaurante_api.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados da resposta de login de usuário, incluindo o token JWT")
public class UsuarioDTOResponseLogin {

    @Schema(description = "Token JWT de autenticação para o usuário logado.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    public UsuarioDTOResponseLogin(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
