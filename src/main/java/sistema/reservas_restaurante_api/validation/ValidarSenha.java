package sistema.reservas_restaurante_api.validation;

import org.springframework.context.annotation.Configuration;
import sistema.reservas_restaurante_api.exceptions.usuarioexceptions.SenhaFracaException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class ValidarSenha {

    public void validatePassword(String password) throws SenhaFracaException {
        Pattern numberPattern = Pattern.compile(".*\\d.*");
        Matcher numberMatcher = numberPattern.matcher(password);
        boolean hasNumber = numberMatcher.matches();

        Pattern specialCharPattern = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        Matcher specialCharMatcher = specialCharPattern.matcher(password);
        boolean hasSpecialChar = specialCharMatcher.matches();

        Pattern upperCasePattern = Pattern.compile(".*[A-Z].*");
        Matcher upperCaseMatcher = upperCasePattern.matcher(password);
        boolean hasUpperCase = upperCaseMatcher.matches();

        Pattern lowerCasePattern = Pattern.compile(".*[a-z].*");
        Matcher lowerCaseMatcher = lowerCasePattern.matcher(password);
        boolean hasLowerCase = lowerCaseMatcher.matches();

        boolean hasMinimumLength = password.trim().length() >= 8;

        if (!hasNumber || !hasSpecialChar || !hasUpperCase || !hasLowerCase || !hasMinimumLength) {
            throw new SenhaFracaException(
                    """
                    A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número,
                    um caractere especial e no mínimo 8 caracteres."""
            );
        }
    }
}

