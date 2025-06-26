package sistema.reservas_restaurante_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sistema.reservas_restaurante_api.model.UsuarioModel;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByEmail(String email);
}
