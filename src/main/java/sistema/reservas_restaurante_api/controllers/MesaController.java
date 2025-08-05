package sistema.reservas_restaurante_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sistema.reservas_restaurante_api.dtos.request.MesaDTORequest;
import sistema.reservas_restaurante_api.dtos.response.MesaDTOResponse;
import sistema.reservas_restaurante_api.services.MesaService;

@RestController
@RequestMapping("/mesas")
@Tag(name = "Mesas", description = "Gerenciamento de mesas do restaurante")
@SecurityRequirement(name = "bearerAuth")
public class MesaController {

    private final MesaService service;

    public MesaController(MesaService service) {
        this.service = service;
    }

    @Operation(summary = "Lista todas as mesas disponíveis no restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesas encontradas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MesaDTOResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Page<MesaDTOResponse>> buscarMesas(Pageable pageable){
        return new ResponseEntity<>(service.buscarMesas(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Cadastra uma nova mesa no sistema (apenas para ADMINISTRADORES)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mesa cadastrada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MesaDTOResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou mesa já existente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"title\": \"Mesa já existe\", \"message\": \"Mesa de número X já existe no banco de dados\", \"timestamp\": \"2025-06-20T10:00:00\"}"))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (apenas ADMINISTRADOR)"),
            @ApiResponse(responseCode = "409", description = "Conflito: Mesa com o mesmo número já existe")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MesaDTOResponse> saveMesa(@RequestBody @Valid MesaDTORequest request){
        return new ResponseEntity<>(service.saveMesa(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Atualiza os dados de uma mesa existente (apenas para ADMINISTRADORES)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MesaDTOResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (apenas ADMINISTRADOR)"),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"title\": \"Mesa não encontrada\", \"message\": \"Mesa não encontrada\", \"timestamp\": \"2025-06-20T10:00:00\"}")))
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MesaDTOResponse> updateMesa(@RequestBody @Valid MesaDTORequest request, @PathVariable Long id){
        return ResponseEntity.ok(service.updateMesa(request, id));
    }

    @Operation(summary = "Exclui uma mesa pelo ID (apenas para ADMINISTRADORES)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mesa excluída com sucesso (nenhum conteúdo retornado)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (apenas ADMINISTRADOR)"),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"title\": \"Mesa não encontrada\", \"message\": \"Mesa não encontrada\", \"timestamp\": \"2025-06-20T10:00:00\"}")))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteMesa(@PathVariable Long id){
        service.deleteMesa(id);
        return ResponseEntity.noContent().build();
    }
}

