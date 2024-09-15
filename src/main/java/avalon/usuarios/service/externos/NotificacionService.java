package avalon.usuarios.service.externos;

import avalon.usuarios.model.request.NotificacionRequest;

public interface NotificacionService {

    void enviarNotificacion(NotificacionRequest notificacionRequest);
}
