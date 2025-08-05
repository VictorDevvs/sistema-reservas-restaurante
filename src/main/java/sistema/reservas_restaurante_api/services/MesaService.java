package sistema.reservas_restaurante_api.services;


import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sistema.reservas_restaurante_api.dtos.request.MesaDTORequest;
import sistema.reservas_restaurante_api.dtos.response.MesaDTOResponse;
import sistema.reservas_restaurante_api.exceptions.mesaexceptions.MesaNaoEncontradaException;
import sistema.reservas_restaurante_api.mapper.MesaMapper;
import sistema.reservas_restaurante_api.model.MesaModel;
import sistema.reservas_restaurante_api.repositories.MesaRepository;
import sistema.reservas_restaurante_api.repositories.UsuarioRepository;
import sistema.reservas_restaurante_api.validation.ValidarAutenticacaoAutorizacaoUsuario;
import sistema.reservas_restaurante_api.validation.ValidarMesa;

@Service
public class MesaService {

    private final MesaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final MesaMapper mapper;
    private final ValidarAutenticacaoAutorizacaoUsuario validarAutenticacaoUsuario;
    private final ValidarMesa validarMesa;

    public MesaService(MesaRepository repository, UsuarioRepository usuarioRepository, MesaMapper mapper,
                       ValidarAutenticacaoAutorizacaoUsuario validarAutenticacaoUsuario, ValidarMesa validarMesa) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
        this.validarAutenticacaoUsuario = validarAutenticacaoUsuario;
        this.validarMesa = validarMesa;
    }

    public Page<MesaDTOResponse> buscarMesas(Pageable pageable){
        validarAutenticacaoUsuario.getUsuarioAutenticado();
        return repository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public MesaDTOResponse saveMesa(MesaDTORequest request){
        validarAutenticacaoUsuario.isAdministrador();
        validarMesa.mesaExistente(request.numero());

        MesaModel model = mapper.toModel(request);
        return mapper.toDto(repository.save(model));
    }

    @Transactional
    public MesaDTOResponse updateMesa(MesaDTORequest request, Long id){
        validarAutenticacaoUsuario.isAdministrador();

        MesaModel model = repository.findById(id).orElseThrow(() -> new MesaNaoEncontradaException("Mesa não encontrada"));

        if (request.numero() != null && !request.numero().equals(model.getNumero())) {
            validarMesa.mesaExistente(request.numero());
            model.setNumero(request.numero());
        }

        if (request.capacidade() != null){
            model.setCapacidade(request.capacidade());
        }

        if (request.status() != null){
            model.setStatus(request.status());
        }

        return mapper.toDto(repository.save(model));
    }

    @Transactional
    public void deleteMesa(Long id){
        validarAutenticacaoUsuario.isAdministrador();

        MesaModel model = repository.findById(id).orElseThrow(() -> new MesaNaoEncontradaException("Mesa não encontrada"));

        repository.delete(model);
    }
}

