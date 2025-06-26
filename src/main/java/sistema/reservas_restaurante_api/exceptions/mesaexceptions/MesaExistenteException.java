package sistema.reservas_restaurante_api.exceptions.mesaexceptions;

public class MesaExistenteException extends RuntimeException {
    public MesaExistenteException(String message) {
        super(message);
    }
}
