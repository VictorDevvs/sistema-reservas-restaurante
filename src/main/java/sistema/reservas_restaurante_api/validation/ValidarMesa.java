package sistema.reservas_restaurante_api.validation;

import org.springframework.stereotype.Component;
import sistema.reservas_restaurante_api.dtos.response.MesaDTOResponse;
import sistema.reservas_restaurante_api.exceptions.mesaexceptions.MesaExistenteException;
import sistema.reservas_restaurante_api.repositories.MesaRepository;
import java.util.Optional;

@Component
public class ValidarMesa {

    private final MesaRepository repository;

    public ValidarMesa(MesaRepository repository) {
        this.repository = repository;
    }

    public void mesaExistente(Integer numero){
        Optional<MesaDTOResponse> mesaExistente = repository.findByNumero(numero);
        if (mesaExistente.isPresent()){
            throw new MesaExistenteException("Mesa de número " + numero + " já existe no banco de dados");
        }
    }
}
