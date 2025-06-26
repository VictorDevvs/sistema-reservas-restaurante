package sistema.reservas_restaurante_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sistema.reservas_restaurante_api.dtos.response.MesaDTOResponse;
import sistema.reservas_restaurante_api.model.MesaModel;
import java.util.Optional;

public interface MesaRepository extends JpaRepository<MesaModel, Long> {
    Optional<MesaDTOResponse> findByNumero(Integer numero);
}
