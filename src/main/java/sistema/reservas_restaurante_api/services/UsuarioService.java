package sistema.reservas_restaurante_api.services;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sistema.reservas_restaurante_api.dtos.request.UsuarioDTORequestLogin;
import sistema.reservas_restaurante_api.dtos.request.UsuarioDTORequestRegistro;
import sistema.reservas_restaurante_api.dtos.response.UsuarioDTOResponseLogin;
import sistema.reservas_restaurante_api.dtos.response.UsuarioDTOResponseRegistro;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.EmailExistenteException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.SenhaInvalidaException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.UsuarioNaoEncontradoException;
import sistema.reservas_restaurante_api.mapper.UsuarioMapper;
import sistema.reservas_restaurante_api.model.UsuarioModel;
import sistema.reservas_restaurante_api.repositories.UsuarioRepository;
import sistema.reservas_restaurante_api.security.CustomUserDetailsService;
import sistema.reservas_restaurante_api.security.JwtTokenProvider;
import sistema.reservas_restaurante_api.validation.ValidarSenha;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider provider;
    private final ValidarSenha validarSenha;
    private final UsuarioMapper mapper;
    private final CustomUserDetailsService detailsService;
    private final AuthenticationManager manager;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder, JwtTokenProvider provider, ValidarSenha validarSenha,
                          UsuarioMapper mapper, CustomUserDetailsService detailsService, AuthenticationManager manager) {
        this.repository = repository;
        this.encoder = encoder;
        this.provider = provider;
        this.validarSenha = validarSenha;
        this.mapper = mapper;
        this.detailsService = detailsService;
        this.manager = manager;
    }

    @Transactional
    public UsuarioDTOResponseRegistro save(UsuarioDTORequestRegistro request){
        repository.findByEmail(request.email()).ifPresent(u -> {
            throw new EmailExistenteException("O email inserido já existe. Registre-se com outro email ou faça login caso tenha");
        });

        validarSenha.validatePassword(request.senha());

        UsuarioModel usuario = mapper.toModel(request);
        usuario.setSenha(encoder.encode(usuario.getSenha()));

        return mapper.toRegistroDto(repository.save(usuario));
    }

    @Transactional
    public UsuarioDTOResponseLogin login(UsuarioDTORequestLogin request){
        UsuarioModel usuario = repository.findByEmail(request.email()).orElseThrow(
                () -> new UsuarioNaoEncontradoException("Usuário não encontrado no banco de dados"));

        if(!encoder.matches(request.senha(), usuario.getSenha())){
            throw new SenhaInvalidaException("Senha incorreta. Verifique e tente fazer login novamente");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.senha());

        Authentication authentication = manager.authenticate(authenticationToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = provider.generateToken(userDetails);
        UsuarioDTOResponseLogin response = mapper.toLoginDto(usuario);
        response.setToken(token);
        return response;
    }
}

