package avalon.usuarios.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private final RequestContextHolderUtil requestContextHolderUtil;

    @Autowired
    public AuditorAwareImpl(RequestContextHolderUtil requestContextHolderUtil) {
        this.requestContextHolderUtil = requestContextHolderUtil;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        HttpServletRequest request = requestContextHolderUtil.getCurrentHttpRequest();
        if (request != null) {
            String username = (String) request.getAttribute("username");
            return Optional.ofNullable(username);
        }
        return Optional.empty();
    }
}