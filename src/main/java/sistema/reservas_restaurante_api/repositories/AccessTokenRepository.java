package sistema.reservas_restaurante_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sistema.reservas_restaurante_api.model.AccessToken;
import sistema.reservas_restaurante_api.model.StatusToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    @Query("SELECT a FROM AccessToken a WHERE a.usuario.id = :usuarioId AND a.status = 'ATIVO' AND a.expiracao > :agora")
    List<AccessToken> findAtivosPorUsuario(Long usuarioId, LocalDateTime agora);

    List<AccessToken> findByStatusAndExpiracaoBefore(StatusToken status, LocalDateTime now);

    Optional<AccessToken> findByToken(String token);
}
