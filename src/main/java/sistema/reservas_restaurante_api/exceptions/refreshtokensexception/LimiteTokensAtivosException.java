package sistema.reservas_restaurante_api.exceptions.refreshtokensexception;

public class LimiteTokensAtivosException extends RuntimeException {
    public LimiteTokensAtivosException(String message) {
        super(message);
    }
}
