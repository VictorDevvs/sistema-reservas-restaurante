package sistema.reservas_restaurante_api.exceptions.refreshtokensexception;

public class TokenExpiradoException extends RuntimeException {

    public TokenExpiradoException(String message) {
        super(message);
    }
}
