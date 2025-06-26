package sistema.reservas_restaurante_api.exceptions.usuarioexceptions;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}
