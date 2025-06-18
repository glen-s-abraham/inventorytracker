package in.mariasorganics.inventorytracker;


import java.io.Console;
import java.util.Scanner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Simple CLI tool to create BCrypt password hashes
 * consistent with your Spring Security configuration.
 */
public final class PasswordHashGenerator {

    private PasswordHashGenerator() { /* utility class */ }

    public static void main(String[] args) {
        String rawPassword = readPasswordFromConsole();
        if (rawPassword == null || rawPassword.isBlank()) {
            System.err.println("Password cannot be empty.");
            return;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(rawPassword);

        System.out.println("\nGenerated BCrypt hash:");
        System.out.println(hash);
    }

    /**
     * Reads a password from the console if available;
     * falls back to Scanner when running in an IDE that
     * doesn’t support Console.
     */
    private static String readPasswordFromConsole() {
        Console console = System.console();
        if (console != null) {
            char[] pwd = console.readPassword("Enter plain password: ");
            return pwd == null ? null : new String(pwd);
        } else {
            // IDE / non-interactive fallback
            System.out.print("Enter plain password: ");
            return new Scanner(System.in).nextLine();
        }
    }
}
