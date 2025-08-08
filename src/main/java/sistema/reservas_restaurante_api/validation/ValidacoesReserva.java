package sistema.reservas_restaurante_api.validation;

import org.springframework.stereotype.Component;
import sistema.reservas_restaurante_api.exceptions.PermissaoNegadaException;
import sistema.reservas_restaurante_api.exceptions.reservaexceptions.ReservaNaoAtivaException;
import sistema.reservas_restaurante_api.model.*;
import sistema.reservas_restaurante_api.repositories.MesaRepository;
import sistema.reservas_restaurante_api.repositories.ReservaRepository;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ValidacoesReserva {

    private final MesaRepository mesaRepository;
    private final ReservaRepository reservaRepository;

    public ValidacoesReserva(MesaRepository mesaRepository, ReservaRepository reservaRepository) {
        this.mesaRepository = mesaRepository;
        this.reservaRepository = reservaRepository;
    }

    public void verificarReservaExistente(ReservaModel reserva, Long id) {
        if (reserva.getStatus().equals(ReservaStatus.CANCELADA) || reserva.getStatus().equals(ReservaStatus.CONCLUIDA)){
            throw new ReservaNaoAtivaException("Reserva com o id " + id + " não está ativa");
        }
    }

    public void verificarPermissao(boolean isAdmin, ReservaModel reserva, UsuarioModel usuarioAutenticado) {
        if (!isAdmin){
            if (!reserva.getUsuario().getId().equals(usuarioAutenticado.getId())){
                throw new PermissaoNegadaException("Você não tem permissão para cancelar essa reserva");
            }
        }
    }

    public void concluirReservasExpiradas(){
        List<ReservaModel> reservas = reservaRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (ReservaModel reserva : reservas) {
            if (reserva.getStatus() == ReservaStatus.ATIVA) {
                LocalDateTime fimReserva = reserva.getDataHoraReserva().plusHours(1);
                if (now.isAfter(fimReserva)) {
                    reserva.setStatus(ReservaStatus.CONCLUIDA);
                    MesaModel mesa = reserva.getMesa();
                    mesa.setStatus(MesaStatus.DISPONIVEL);
                    mesaRepository.save(mesa);
                    reservaRepository.save(reserva);
                }
            }
        }
    }
}
