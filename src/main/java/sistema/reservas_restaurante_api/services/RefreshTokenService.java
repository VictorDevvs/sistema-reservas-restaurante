package sistema.reservas_restaurante_api.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sistema.reservas_restaurante_api.exceptions.refreshtokensexception.LimiteTokensAtivosException;
import sistema.reservas_restaurante_api.exceptions.refreshtokensexception.RefreshTokenInvalidoException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.UsuarioNaoEncontradoException;
import sistema.reservas_restaurante_api.model.AccessToken;
import sistema.reservas_restaurante_api.model.RefreshToken;
import sistema.reservas_restaurante_api.model.StatusToken;
import sistema.reservas_restaurante_api.model.UsuarioModel;
import sistema.reservas_restaurante_api.repositories.AccessTokenRepository;
import sistema.reservas_restaurante_api.repositories.RefreshTokenRepository;
import sistema.reservas_restaurante_api.repositories.UsuarioRepository;
import sistema.reservas_restaurante_api.security.JwtTokenProvider;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtTokenProvider provider;
    private final UsuarioRepository usuarioRepository;
    private final AccessTokenRepository accessTokenRepository;

    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;

    public RefreshTokenService(RefreshTokenRepository repository, JwtTokenProvider provider, UsuarioRepository usuarioRepository,
                               AccessTokenRepository accessTokenRepository) {
        this.repository = repository;
        this.provider = provider;
        this.usuarioRepository = usuarioRepository;
        this.accessTokenRepository = accessTokenRepository;
    }

    @Transactional
    public AccessToken saveAccessToken(UserDetails details){
        UsuarioModel usuario = usuarioRepository.findByEmail(details.getUsername()).orElseThrow(() ->
                new UsuarioNaoEncontradoException("Usuário não encontrado com o email: " + details.getUsername()));

        LocalDateTime agora = LocalDateTime.now();

        List<AccessToken> activeTokens = accessTokenRepository.findAtivosPorUsuario(usuario.getId(), agora);

        if (activeTokens.size() >= 3){
            throw new LimiteTokensAtivosException("Número máximo de tokens ativos foi atingido. Somente é permitido 3 tokens ativos por usuário.");
        }

        String accessToken = provider.generateAccessToken(details);
        AccessToken newAccessToken = new AccessToken();
        newAccessToken.setToken(accessToken);
        newAccessToken.setUsuario(usuario);
        newAccessToken.setExpiracao(agora.plus(Duration.ofMillis(accessTokenExpiration)));
        newAccessToken.setCreatedAt(agora);
        newAccessToken.setStatus(StatusToken.ATIVO);

        return accessTokenRepository.save(newAccessToken);
    }

    @Transactional
    public RefreshToken saveRefreshToken(UUID token, UserDetails details) {
        UsuarioModel usuario = usuarioRepository.findByEmail(details.getUsername()).orElseThrow(() ->
                new UsuarioNaoEncontradoException("Usuário não encontrado com o email: " + details.getUsername()));

        LocalDateTime agora = LocalDateTime.now();

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken(token);
        newRefreshToken.setEmail(usuario.getEmail());
        newRefreshToken.setExpirationDate(agora.plus(Duration.ofMillis(refreshTokenExpiration)));
        newRefreshToken.setCreatedAt(agora);
        newRefreshToken.setStatus(StatusToken.ATIVO);
        return repository.save(newRefreshToken);
    }

    public RefreshToken validate(UUID token){
        return repository.findByToken(token)
                .filter(rt -> rt.getExpirationDate().isAfter(LocalDateTime.now()) && rt.getStatus() == StatusToken.ATIVO)
                .orElseThrow(() -> new RefreshTokenInvalidoException("Token de refresh inválido ou expirado"));
    }

    public String getEmailFromRefreshToken(UUID token){
        return repository.findByToken(token)
                .map(RefreshToken::getEmail)
                .orElseThrow(() -> new RefreshTokenInvalidoException("Token de refresh inválido ou expirado"));
    }

    public void deleteByEmail(String email) {
        repository.deleteByEmail(email);
    }
}
