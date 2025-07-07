package sistema.reservas_restaurante_api.exceptions.reservaexceptions;

public class PermissaoNegadaException extends RuntimeException {
    public PermissaoNegadaException(String message) {
        super(message);
    }
}
