package sistema.reservas_restaurante_api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sistema.reservas_restaurante_api.exceptions.ApiDetails;
import sistema.reservas_restaurante_api.exceptions.refreshtokensexception.LimiteTokensAtivosException;
import sistema.reservas_restaurante_api.exceptions.refreshtokensexception.RefreshTokenInvalidoException;
import java.time.LocalDateTime;

@ControllerAdvice
public class RefreshTokenExceptionHandler {

    @ExceptionHandler(LimiteTokensAtivosException.class)
    public ResponseEntity<ApiDetails> limiteTokensAtivosException(LimiteTokensAtivosException ex){
        ApiDetails details = new ApiDetails("Limite de tokens atingido", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RefreshTokenInvalidoException.class)
    public ResponseEntity<ApiDetails> refreshTokenInvalidoException(RefreshTokenInvalidoException ex){
        ApiDetails details = new ApiDetails("Token inválido", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }
}
