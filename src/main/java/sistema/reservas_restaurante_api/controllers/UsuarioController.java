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
import sistema.reservas_restaurante_api.dtos.request.LogoutRequest;
import sistema.reservas_restaurante_api.dtos.request.RefreshTokenRequest;
import sistema.reservas_restaurante_api.dtos.request.UsuarioDTORequestLogin;
import sistema.reservas_restaurante_api.dtos.request.UsuarioDTORequestRegistro;
import sistema.reservas_restaurante_api.dtos.response.RefreshAccessTokenResponse;
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
    /*APENAS USUARIOS ADMINISTRADORES PODERIAM CRIAR NOVOS USUÁRIOS ADMINISTRADORES, MAS, PARA ESSA SIMPLES API E PARA
     QUE TODOS POSSAM TESTAR TODAS AS ROTAS, INCLUSIVE AQUELAS QUE SOMENTE ADMINISTRADORES PODEM ACESSAR, DEIXEI LIVRE
     PARA CRIAR USUÁRIOS ADMINISTRADORES MESMO NÃO SENDO ADMINISTRADOR. MAS, EM UM SISTEMA REAL, É RECOMENDADO O QUE FOI
     DITO NA PRIMEIRA LINHA DO COMENTÁRIO. ABAIXO DEIXAREI UMA CONFIGURAÇÃO DE EXEMPLO PARA QUE SOMENTE ADMINISTRADORES
     CRIEM NOVOS USUÁRIOS ADMINISTRADORES:
     TODO
        @PreAuthorize("#request.role() != T(sistema.reservas_restaurante_api.model.Role).ADMINISTRADOR or
            isFullyAuthenticated() and hasRole('ADMINISTRADOR')")
     */
    /* O @PreAuthorize VERIFICARÁ SE O USUÁRIO ESTÁ TENTANDO CRIAR UM USUÁRIO DIFERENTE DE ADMINISTRADOR, OU SEJA,
       CLIENTE. SE SIM, ESTÁ AUTORIZADO A CRIAR O USUÁRIO. SE NÃO, VERIFICA SE O USUÁRIO ESTÁ AUTENTICADO TOTALMENTE
       E SE É ADMINISTRADOR.
    */
    public ResponseEntity<UsuarioDTOResponseRegistro> registrarUsuario(@RequestBody @Valid UsuarioDTORequestRegistro request){
        return new ResponseEntity<>(service.saveUser(request), HttpStatus.CREATED);
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
    public ResponseEntity<UsuarioDTOResponseLogin> loginUsuario(@RequestBody @Valid UsuarioDTORequestLogin request){
        return ResponseEntity.ok(service.loginUser(request));
    }

    @Operation(summary = "Atualiza o token de acesso usando um token de atualização")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de acesso atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RefreshAccessTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Token de atualização inválido ou expirado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"title\": \"Token inválido\", \"message\": \"O token de atualização é inválido ou expirou.\", \"timestamp\": \"2025-06-20T10:00:00\"}")))
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshAccessTokenResponse> revogarRefreshToken(@RequestBody RefreshTokenRequest request){
        RefreshAccessTokenResponse response = service.revogarRefreshToken(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Revoga o token de acesso do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de acesso revogado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token de acesso inválido ou expirado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"title\": \"Token inválido\", \"message\": \"O token de acesso é inválido ou expirou.\", \"timestamp\": \"2025-06-20T10:00:00\"}")))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> revogarAccessToken(@RequestBody LogoutRequest request){
        service.revogarAccessToken(request.getAccessToken());
        return ResponseEntity.ok().build();
    }
}

