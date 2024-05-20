package avalon.usuarios.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String>{

    @Override
    public Optional<String> getCurrentAuditor() {
        // Puedes proporcionar el nombre del usuario actual o algún identificador único.
        // En este ejemplo, devolvemos una cadena fija "admin" como auditor actual.
        return Optional.of("admin");
    }
}