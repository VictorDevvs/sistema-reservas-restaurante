package sistema.reservas_restaurante_api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sistema.reservas_restaurante_api.exceptions.ApiDetails;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.EmailExistenteException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.SenhaFracaException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.SenhaInvalidaException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.UsuarioNaoEncontradoException;

import java.time.LocalDateTime;

@ControllerAdvice
public class UsuarioGlobalExceptionHandler {

    @ExceptionHandler(EmailExistenteException.class)
    public ResponseEntity<ApiDetails> emailExistenteException(EmailExistenteException ex){
        ApiDetails details = new ApiDetails("Email já existe", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SenhaFracaException.class)
    public ResponseEntity<ApiDetails> senhaFracaException(SenhaFracaException ex){
        ApiDetails details = new ApiDetails("Senha fraca", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SenhaInvalidaException.class)
    public ResponseEntity<ApiDetails> senhaInvalidaException(SenhaInvalidaException ex){
        ApiDetails details = new ApiDetails("Senha inválida", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ApiDetails> usuarioNaoEncontradoException(UsuarioNaoEncontradoException ex){
        ApiDetails details = new ApiDetails("Usuário não encontrado", ex.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }
}

