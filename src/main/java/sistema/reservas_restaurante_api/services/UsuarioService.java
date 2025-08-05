package sistema.reservas_restaurante_api.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sistema.reservas_restaurante_api.dtos.request.RefreshTokenRequest;
import sistema.reservas_restaurante_api.dtos.request.UsuarioDTORequestLogin;
import sistema.reservas_restaurante_api.dtos.request.UsuarioDTORequestRegistro;
import sistema.reservas_restaurante_api.dtos.response.RefreshAccessTokenResponse;
import sistema.reservas_restaurante_api.dtos.response.UsuarioDTOResponseLogin;
import sistema.reservas_restaurante_api.dtos.response.UsuarioDTOResponseRegistro;
import sistema.reservas_restaurante_api.exceptions.refreshtokensexception.RefreshTokenInvalidoException;
import sistema.reservas_restaurante_api.exceptions.refreshtokensexception.TokenExpiradoException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.EmailExistenteException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.SenhaInvalidaException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.UsuarioNaoEncontradoException;
import sistema.reservas_restaurante_api.mapper.UsuarioMapper;
import sistema.reservas_restaurante_api.model.AccessToken;
import sistema.reservas_restaurante_api.model.RefreshToken;
import sistema.reservas_restaurante_api.model.StatusToken;
import sistema.reservas_restaurante_api.model.UsuarioModel;
import sistema.reservas_restaurante_api.repositories.AccessTokenRepository;
import sistema.reservas_restaurante_api.repositories.RefreshTokenRepository;
import sistema.reservas_restaurante_api.repositories.UsuarioRepository;
import sistema.reservas_restaurante_api.security.CustomUserDetailsService;
import sistema.reservas_restaurante_api.security.JwtTokenProvider;
import sistema.reservas_restaurante_api.validation.ValidarSenha;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider provider;
    private final ValidarSenha validarSenha;
    private final UsuarioMapper mapper;
    private final CustomUserDetailsService detailsService;
    private final AuthenticationManager manager;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder, JwtTokenProvider provider, ValidarSenha validarSenha,
                          UsuarioMapper mapper, CustomUserDetailsService detailsService, AuthenticationManager manager,
                          RefreshTokenService service, AccessTokenRepository accessTokenRepository,
                          RefreshTokenRepository refreshTokenRepository) {
        this.repository = repository;
        this.encoder = encoder;
        this.provider = provider;
        this.validarSenha = validarSenha;
        this.mapper = mapper;
        this.detailsService = detailsService;
        this.manager = manager;
        this.refreshTokenService = service;
        this.accessTokenRepository = accessTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public UsuarioDTOResponseRegistro saveUser(UsuarioDTORequestRegistro request){
        repository.findByEmail(request.email()).ifPresent(u -> {
            throw new EmailExistenteException("O email inserido já existe. Registre-se com outro email ou faça login caso tenha");
        });

        validarSenha.validatePassword(request.senha());

        UsuarioModel usuario = mapper.toModel(request);
        usuario.setSenha(encoder.encode(usuario.getSenha()));

        return mapper.toRegistroDto(repository.save(usuario));
    }

    @Transactional
    public UsuarioDTOResponseLogin loginUser(UsuarioDTORequestLogin request){
        UsuarioModel usuario = repository.findByEmail(request.email()).orElseThrow(
                () -> new UsuarioNaoEncontradoException("Usuário não encontrado no banco de dados"));

        if(!encoder.matches(request.senha(), usuario.getSenha())){
            throw new SenhaInvalidaException("Senha incorreta. Verifique e tente fazer login novamente");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.senha());

        Authentication authentication = manager.authenticate(authenticationToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UUID refreshToken = provider.generateRefreshToken(userDetails);
        refreshTokenService.saveRefreshToken(refreshToken, userDetails);

        AccessToken accessTokenObj = refreshTokenService.saveAccessToken(userDetails);
        String accessToken = accessTokenObj.getToken();

        UsuarioDTOResponseLogin response = mapper.toLoginDto(usuario);
        response.setRefreshToken(refreshToken);
        response.setAccessToken(accessToken);
        return response;
    }

    @Transactional
    public RefreshAccessTokenResponse revogarRefreshToken(RefreshTokenRequest request){
        refreshTokenService.validateRefreshToken(request.getRefreshToken());
        UUID oldToken = request.getRefreshToken();

        RefreshToken antigo = refreshTokenRepository.findByToken(oldToken).orElseThrow(() ->
                new RefreshTokenInvalidoException("Refresh token não encontrado ou inválido."));

        antigo.setStatus(StatusToken.REVOGADO);
        refreshTokenRepository.save(antigo);

        UUID novoRefreshToken = UUID.randomUUID();

        RefreshToken novoToken = new RefreshToken();
        novoToken.setToken(novoRefreshToken);
        novoToken.setEmail(antigo.getEmail());
        novoToken.setCreatedAt(LocalDateTime.now());
        novoToken.setExpirationDate(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpiration)));
        novoToken.setStatus(StatusToken.ATIVO);

        refreshTokenRepository.save(novoToken);

        UserDetails userDetails = detailsService.loadUserByUsername(antigo.getEmail());
        AccessToken accessTokenObj = refreshTokenService.saveAccessToken(userDetails);
        String newAccessToken = accessTokenObj.getToken();

        UsuarioModel usuario = repository.findByEmail(antigo.getEmail()).orElseThrow(() ->
                new UsuarioNaoEncontradoException("Usuário não encontrado com o email: " + antigo.getEmail()));
        List<AccessToken> activeTokens = accessTokenRepository.findAtivosPorUsuario(usuario.getId(), LocalDateTime.now());

        return new RefreshAccessTokenResponse(newAccessToken, novoRefreshToken,
                "Token gerado com sucesso! Tokens ativos: " + activeTokens.size() + " (Máximo permitido: 3)");
    }

    @Transactional
    public void revogarAccessToken(String token){
        AccessToken accessToken = accessTokenRepository.findByToken(token).orElseThrow(() ->
                new RefreshTokenInvalidoException("Access token não encontrado ou inválido."));

        if (accessToken.getStatus() == StatusToken.EXPIRADO || accessToken.getStatus() == StatusToken.REVOGADO) {
            throw new TokenExpiradoException("Access token já está expirado ou revogado.");
        }

        accessToken.setStatus(StatusToken.REVOGADO);
        accessTokenRepository.save(accessToken);
    }
}

