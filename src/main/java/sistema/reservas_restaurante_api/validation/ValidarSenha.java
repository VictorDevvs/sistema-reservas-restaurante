package sistema.reservas_restaurante_api.validation;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.SenhaFracaException;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.SenhaInvalidaException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class ValidarSenha {

    private final PasswordEncoder encoder;

    public ValidarSenha(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public void validarSenhaForte(String password) throws SenhaFracaException {
        Pattern numberPattern = Pattern.compile("\\d");
        Matcher numberMatcher = numberPattern.matcher(password);
        boolean hasNumber = numberMatcher.find();

        Pattern specialCharPattern = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        Matcher specialCharMatcher = specialCharPattern.matcher(password);
        boolean hasSpecialChar = specialCharMatcher.find();

        Pattern upperCasePattern = Pattern.compile(".*[A-Z].*");
        Matcher upperCaseMatcher = upperCasePattern.matcher(password);
        boolean hasUpperCase = upperCaseMatcher.find();

        Pattern lowerCasePattern = Pattern.compile(".*[a-z].*");
        Matcher lowerCaseMatcher = lowerCasePattern.matcher(password);
        boolean hasLowerCase = lowerCaseMatcher.find();

        boolean hasMinimumLength = password.trim().length() >= 8;

        if (!hasNumber || !hasSpecialChar || !hasUpperCase || !hasLowerCase || !hasMinimumLength) {
            throw new SenhaFracaException(
                    """
                    A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número,
                    um caractere especial e no mínimo 8 caracteres."""
            );
        }
    }

    public boolean validarSenhaCorretaUsuario(String senhaPassada, String senhaBanco) {
        if (!encoder.matches(senhaPassada, senhaBanco)) {
            throw new SenhaInvalidaException("Senha inválida. Por favor, verifique sua senha e tente novamente.");
        }
        return true;
    }
}

