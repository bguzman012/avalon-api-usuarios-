package avalon.usuarios.service.externos;

import avalon.usuarios.config.RequestContextHolderUtil;
import avalon.usuarios.model.request.NotificacionRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class NotificacionServiceImpl implements NotificacionService{

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RequestContextHolderUtil requestContextHolderUtil;

    private final String url = "http://149.56.110.32:8087/notificaciones";

    @Override
    public void enviarNotificacion(NotificacionRequest notificacionRequest) {
        HttpServletRequest request = requestContextHolderUtil.getCurrentHttpRequest();
        String token = (String) request.getAttribute("token");

        if (token == null) {
            throw new IllegalArgumentException("Token not found in the request.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token); // Añadir el token en la cabecera Authorization

        // Crear la entidad HTTP con el cuerpo de la solicitud y los encabezados
        HttpEntity<NotificacionRequest> requestEntity = new HttpEntity<>(notificacionRequest, headers);

        // Realizar la solicitud POST con el token en las cabeceras
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);

    }
}
