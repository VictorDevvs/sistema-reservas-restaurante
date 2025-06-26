package sistema.reservas_restaurante_api.exceptions.usuarioexceptions;

public class SenhaInvalidaException extends RuntimeException {
    public SenhaInvalidaException(String message) {
        super(message);
    }
}
