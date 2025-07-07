package sistema.reservas_restaurante_api.validation;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.UsuarioNaoEncontradoException;
import sistema.reservas_restaurante_api.model.Role;
import sistema.reservas_restaurante_api.model.UsuarioModel;
import sistema.reservas_restaurante_api.repositories.UsuarioRepository;

@Component
public class ValidarAutenticacaoAutorizacaoUsuario {

    private final UsuarioRepository repository;

    public ValidarAutenticacaoAutorizacaoUsuario(UsuarioRepository repository) {
        this.repository = repository;
    }

    public UsuarioModel getUsuarioAutenticado(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email;
        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof String str) {
            email = str;
        } else {
            throw new RuntimeException("Usuário não autenticado");
        }

        return repository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
    }

    public void isAdministrador() {
        UsuarioModel usuario = getUsuarioAutenticado();
        if (usuario.getRole() != Role.ADMINISTRADOR) {
            throw new RuntimeException("Usuário não possui status de administrador.");
        }
    }

    public boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Role.ADMINISTRADOR.name()));
    }
}
