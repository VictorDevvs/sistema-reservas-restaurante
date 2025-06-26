package sistema.reservas_restaurante_api.services;


import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sistema.reservas_restaurante_api.dtos.request.AtualizarMesaDTORequest;
import sistema.reservas_restaurante_api.dtos.request.MesaDTORequest;
import sistema.reservas_restaurante_api.dtos.response.MesaDTOResponse;
import sistema.reservas_restaurante_api.exceptions.PermissaoNegadaException;
import sistema.reservas_restaurante_api.exceptions.mesaexceptions.MesaExistenteException;
import sistema.reservas_restaurante_api.exceptions.mesaexceptions.MesaNaoEncontradaException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.UsuarioNaoEncontradoException;
import sistema.reservas_restaurante_api.mapper.MesaMapper;
import sistema.reservas_restaurante_api.model.MesaModel;
import sistema.reservas_restaurante_api.model.Role;
import sistema.reservas_restaurante_api.model.UsuarioModel;
import sistema.reservas_restaurante_api.repositories.MesaRepository;
import sistema.reservas_restaurante_api.repositories.UsuarioRepository;

import java.util.Optional;

@Service
public class MesaService {

    private final MesaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final MesaMapper mapper;

    public MesaService(MesaRepository repository, UsuarioRepository usuarioRepository, MesaMapper mapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    public Page<MesaDTOResponse> findAll(Pageable pageable){
        return repository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public MesaDTOResponse save(MesaDTORequest request){
        getUsuarioAutenticado();
        isAdministrador();
        mesaExistente(request.numero());

        MesaModel model = mapper.toModel(request);
        return mapper.toDto(repository.save(model));
    }

    @Transactional
    public MesaDTOResponse update(AtualizarMesaDTORequest request, Long id){
        getUsuarioAutenticado();
        isAdministrador();

        MesaModel model = repository.findById(id).orElseThrow(() -> new MesaNaoEncontradaException("Mesa não encontrada"));

        if (request.numero() != null){
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
    public void delete(Long id){
        getUsuarioAutenticado();
        isAdministrador();

        MesaModel model = repository.findById(id).orElseThrow(() -> new MesaNaoEncontradaException("Mesa não encontrada"));

        repository.delete(model);
    }

    private void isAdministrador() {
        UsuarioModel usuario = getUsuarioAutenticado();
        if (usuario.getRole() != Role.ADMINISTRADOR) {
            throw new PermissaoNegadaException("Apenas administradores podem realizar esta ação.");
        }
    }

    private UsuarioModel getUsuarioAutenticado(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email;
        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof String str) {
            email = str;
        } else {
            throw new RuntimeException("Tipo de principal desconhecido");
        }

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
    }

    private void mesaExistente(Integer numero){
        Optional<MesaDTOResponse> mesaExistente = repository.findByNumero(numero);
        if (mesaExistente.isPresent()){
            throw new MesaExistenteException("Mesa de número " + numero + " já existe no banco de dados");
        }
    }
}

