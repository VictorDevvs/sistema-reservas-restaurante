package sistema.reservas_restaurante_api.exceptions.mesaexceptions;

public class MesaNaoEncontradaException extends RuntimeException {
    public MesaNaoEncontradaException(String message) {
        super(message);
    }
}
