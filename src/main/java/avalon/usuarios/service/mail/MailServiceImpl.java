package avalon.usuarios.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("bryamgmfn@gmail.com"); // Asegúrate de que coincide con el email configurado

        mailSender.send(message);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String dynamicContent) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Cargar la plantilla HTML desde el archivo
        String htmlTemplate = loadHtmlTemplate("templates/email-template.html");

        // Reemplazar el marcador de contenido dinámico
        String htmlBody = htmlTemplate.replace("[[BODY]]", dynamicContent);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // El segundo parámetro "true" indica que es HTML
        helper.setFrom("bryamgmfn@gmail.com");

        mailSender.send(message);
    }

    //    private String loadHtmlTemplate(String filePath) throws IOException {
//        // Cargar el archivo desde el classpath
//        Path path = new ClassPathResource(filePath).getFile().toPath();
//        return new String(Files.readAllBytes(path));
//    }
    public String loadHtmlTemplate(String path) throws IOException {
        // Cargar el recurso usando ResourceLoader
        Resource resource = resourceLoader.getResource("classpath:" + path);

        // Leer el contenido del recurso como InputStream
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
