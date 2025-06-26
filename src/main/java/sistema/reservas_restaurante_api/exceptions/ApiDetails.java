package sistema.reservas_restaurante_api.exceptions;

import java.time.LocalDateTime;

public record ApiDetails(
        String title,
        String message,
        LocalDateTime timestamp
) {
}
