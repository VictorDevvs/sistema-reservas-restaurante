package sistema.reservas_restaurante_api.exceptions.reservaexceptions;

public class MesaNaoDisponivelException extends RuntimeException {
    public MesaNaoDisponivelException(String message) {
        super(message);
    }
}
