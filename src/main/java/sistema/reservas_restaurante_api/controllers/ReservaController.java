package sistema.reservas_restaurante_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sistema.reservas_restaurante_api.dtos.request.ReservaDTORequest;
import sistema.reservas_restaurante_api.dtos.response.ReservaDTOResponse;
import sistema.reservas_restaurante_api.services.ReservaService;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@Tag(name = "Reservas", description = "Gerenciamento de reservas de mesas")
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @Operation(summary = "Busca todas as reservas de um usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas encontradas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservaDTOResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (papel não permitido)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<ReservaDTOResponse>> reservas(){
        return new ResponseEntity<>(service.findAllByUsuario(), HttpStatus.OK);
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    @Operation(
            summary = "Cria uma nova reserva",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "mesa": 5,
                              "numeroPessoas": 7,
                              "dataHoraReserva": "2025-06-25T22:00:00"
                            }
                        """)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservaDTOResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou mesa não encontrada")
    })
    public ResponseEntity<ReservaDTOResponse> criarReserva(@RequestBody @Valid ReservaDTORequest request) {
        return new ResponseEntity<>(service.criarReserva(request), HttpStatus.CREATED);
    }


    @Operation(summary = "Cancela uma reserva existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva cancelada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Reserva não está ativa ou já concluída/cancelada",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (usuário sem permissão para cancelar esta reserva)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada",
                    content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id){
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
