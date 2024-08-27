package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.MetodoPago;
import avalon.usuarios.model.request.MetodoPagoRequest;
import avalon.usuarios.service.MetodoPagoService;
import avalon.usuarios.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    @Autowired
    private MailService mailService;

    @GetMapping("/mail")
    public String sendEmail(@RequestParam String to,
                            @RequestParam String subject,
                            @RequestParam String text) {
        mailService.sendSimpleEmail(to, subject, text);
        return "Email enviado exitosamente!";
    }

}
