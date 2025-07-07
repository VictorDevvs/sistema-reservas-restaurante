package sistema.reservas_restaurante_api.exceptions.reservaexceptions;

public class ReservaNaoEncontradaException extends RuntimeException {
    public ReservaNaoEncontradaException(String message) {
        super(message);
    }
}
