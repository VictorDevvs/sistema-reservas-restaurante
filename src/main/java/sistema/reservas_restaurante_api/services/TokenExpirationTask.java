package sistema.reservas_restaurante_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sistema.reservas_restaurante_api.model.AccessToken;
import sistema.reservas_restaurante_api.model.StatusToken;
import sistema.reservas_restaurante_api.repositories.AccessTokenRepository;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TokenExpirationTask {

    private static final Logger log = LoggerFactory.getLogger(TokenExpirationTask.class);
    private final AccessTokenRepository accessTokenRepository;

    public TokenExpirationTask(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void expireAccessTokens() {
        LocalDateTime now = LocalDateTime.now();

        List<AccessToken> tokensExpirados = accessTokenRepository.findByStatusAndExpiracaoBefore(StatusToken.ATIVO, now);

        tokensExpirados.forEach(t -> t.setStatus(StatusToken.EXPIRADO));
        accessTokenRepository.saveAll(tokensExpirados);

        log.info("Expiração  de tokens: {} tokens expirados", tokensExpirados.size());
    }
}
