package avalon.usuarios.service.mail;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface MailService {
    void sendSimpleEmail(String to, String subject, String text);
    void sendHtmlEmail(String to, String subject, String dynamicContent) throws MessagingException, IOException;

}
