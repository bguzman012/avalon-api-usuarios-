package avalon.usuarios.util;
import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    public static String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(ALPHANUMERIC_CHARACTERS.length());
            password.append(ALPHANUMERIC_CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
