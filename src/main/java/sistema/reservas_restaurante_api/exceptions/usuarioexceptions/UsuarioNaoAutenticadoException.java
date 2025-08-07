package sistema.reservas_restaurante_api.exceptions.usuarioexceptions;

public class UsuarioNaoAutenticadoException extends RuntimeException {
    public UsuarioNaoAutenticadoException(String message) {
        super(message);
    }
}
