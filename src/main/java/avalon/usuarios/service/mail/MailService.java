package avalon.usuarios.service.mail;

public interface MailService {
    void sendSimpleEmail(String to, String subject, String text);

}
