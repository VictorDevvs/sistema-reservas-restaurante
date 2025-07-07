package sistema.reservas_restaurante_api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sistema.reservas_restaurante_api.exceptions.ApiDetails;
import sistema.reservas_restaurante_api.exceptions.mesaexceptions.MesaExistenteException;

import java.time.LocalDateTime;

@ControllerAdvice
public class MesaGlobalExceptionHandler {

    @ExceptionHandler(MesaExistenteException.class)
    public ResponseEntity<ApiDetails> mesaExistenteException(MesaExistenteException ex){
        ApiDetails details = new ApiDetails("Mesa já existe", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }
}
