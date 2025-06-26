package sistema.reservas_restaurante_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sistema.reservas_restaurante_api.dtos.request.UsuarioDTORequestLogin;
import sistema.reservas_restaurante_api.dtos.request.UsuarioDTORequestRegistro;
import sistema.reservas_restaurante_api.dtos.response.UsuarioDTOResponseLogin;
import sistema.reservas_restaurante_api.dtos.response.UsuarioDTOResponseRegistro;
import sistema.reservas_restaurante_api.services.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários e autenticação")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Registra um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioDTOResponseRegistro.class))),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos ou email já existente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"title\": \"Email já existe\", \"message\": \"O email inserido já existe.\", \"timestamp\": \"2025-06-20T10:00:00\"}")))
    })
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTOResponseRegistro> registrar(@RequestBody @Valid UsuarioDTORequestRegistro request){
        return new ResponseEntity<>(service.save(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Realiza o login de um usuário e retorna um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token JWT retornado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioDTOResponseLogin.class))),
            @ApiResponse(responseCode = "400", description = "Senha incorreta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"title\": \"Senha inválida\", \"message\": \"Senha incorreta.\", \"timestamp\": \"2025-06-20T10:00:00\"}"))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"title\": \"Usuário não encontrado\", \"message\": \"Usuário não encontrado no banco de dados.\", \"timestamp\": \"2025-06-20T10:00:00\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTOResponseLogin> login(@RequestBody @Valid UsuarioDTORequestLogin request){
        return ResponseEntity.ok(service.login(request));
    }
}

