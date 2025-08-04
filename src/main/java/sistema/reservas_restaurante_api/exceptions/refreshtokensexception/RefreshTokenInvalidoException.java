package sistema.reservas_restaurante_api.exceptions.refreshtokensexception;

public class RefreshTokenInvalidoException extends RuntimeException {
    public RefreshTokenInvalidoException(String message) {
        super(message);
    }
}
