package sistema.reservas_restaurante_api.exceptions.reservaexceptions;

public class DataHoraNaoPermitidaException extends RuntimeException {
    public DataHoraNaoPermitidaException(String message) {
        super(message);
    }
}
