package avalon.usuarios.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExceptionHandlerUtil {

    public static ResponseEntity<String> userHandleException(Exception e) {
        String err = e.getMessage();
        if (err.contains("uk_correo_electronico")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ocurrió un error al persistir la información, correo electrónico ya existente.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ocurrió un error al persistir la información.");
    }

}
