package sistema.reservas_restaurante_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sistema.reservas_restaurante_api.model.RefreshToken;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(UUID token);

    void deleteByEmail(String email);
}
