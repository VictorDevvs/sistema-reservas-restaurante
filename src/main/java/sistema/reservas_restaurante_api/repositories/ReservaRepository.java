package sistema.reservas_restaurante_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sistema.reservas_restaurante_api.model.MesaModel;
import sistema.reservas_restaurante_api.model.ReservaModel;
import sistema.reservas_restaurante_api.model.UsuarioModel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<ReservaModel, Long> {
    Optional<ReservaModel> findByMesaAndDataHoraReserva(MesaModel mesa, LocalDateTime dataHoraReserva);

    List<ReservaModel> findByUsuario(UsuarioModel usuario);
}
