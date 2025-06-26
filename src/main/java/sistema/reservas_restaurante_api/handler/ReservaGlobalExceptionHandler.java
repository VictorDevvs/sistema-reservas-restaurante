package sistema.reservas_restaurante_api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sistema.reservas_restaurante_api.exceptions.ApiDetails;
import sistema.reservas_restaurante_api.exceptions.PermissaoNegadaException;
import sistema.reservas_restaurante_api.exceptions.mesaexceptions.MesaNaoEncontradaException;
import sistema.reservas_restaurante_api.exceptions.reservaexceptions.*;

import java.time.LocalDateTime;

@ControllerAdvice
public class ReservaGlobalExceptionHandler {

    @ExceptionHandler(CapacidadeMesaException.class)
    public ResponseEntity<ApiDetails> capacidadeMesaException(CapacidadeMesaException ex){
        ApiDetails details = new ApiDetails("Capacidade não suportada", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataHoraNaoPermitidaException.class)
    public ResponseEntity<ApiDetails> handleDataHoraNaoPermitidaException(DataHoraNaoPermitidaException ex){
        ApiDetails details = new ApiDetails("Data e hora inválida", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HorarioForaExpedienteException.class)
    public ResponseEntity<ApiDetails> horarioForaExpedienteException(HorarioForaExpedienteException ex){
        ApiDetails details = new ApiDetails("Horário fora de expediente", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MesaNaoDisponivelException.class)
    public ResponseEntity<ApiDetails> mesaNaoDisponivelException(MesaNaoDisponivelException ex){
        ApiDetails details = new ApiDetails("Mesa indisponível", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MesaNaoEncontradaException.class)
    public ResponseEntity<ApiDetails> mesaNaoEncontradaException(MesaNaoEncontradaException ex){
        ApiDetails details = new ApiDetails("Mesa não encontrada", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PermissaoNegadaException.class)
    public ResponseEntity<ApiDetails> permissaoNegadaException(PermissaoNegadaException ex){
        ApiDetails details = new ApiDetails("Permissão negada", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReservaNaoAtivaException.class)
    public ResponseEntity<ApiDetails> reservaNaoAtivaException(ReservaNaoAtivaException ex){
        ApiDetails details = new ApiDetails("Reserva inativa", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }
}

