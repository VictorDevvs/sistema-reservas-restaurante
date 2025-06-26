package sistema.reservas_restaurante_api.exceptions.usuarioexceptions;

public class EmailExistenteException extends RuntimeException {
    public EmailExistenteException(String message) {
        super(message);
    }
}
