package sistema.reservas_restaurante_api.exceptions.reservaexceptions;

public class ReservaNaoAtivaException extends RuntimeException {
    public ReservaNaoAtivaException(String message) {
        super(message);
    }
}
