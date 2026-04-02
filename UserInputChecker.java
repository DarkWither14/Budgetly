import java.util.Scanner;

/**
 * A Singleton utility class that centralizes and controls all user input.
 * <p>
 * The class maintains a single Scanner instance, which by default reads from
 * {@code System.in}. For testing purposes, the input source may be replaced
 * with a custom Scanner (e.g., one backed by a file or string).
 * <p>
 * This class provides validated input methods for all common data types,
 * ensuring safe and consistent user interaction across applications.
 */
public final class UserInput {

    /** The single instance of this class (Singleton pattern). */
    private static final UserInput INSTANCE = new UserInput();

    /** The Scanner used for all input operations. */
    private Scanner userInput = new Scanner(System.in);

    /**
     * Private constructor to enforce Singleton design.
     */
    private UserInput() {}

    /**
     * Returns the single instance of the UserInput controller.
     *
     * @return the global UserInput instance
     */
    public static UserInput getInstance() {
        return INSTANCE;
    }

    /**
     * Replaces the current input source with a new Scanner.
     * Useful for automated testing or scripted input.
     *
     * @param newInput the new Scanner to use
     */
    public void setInput(Scanner newInput) {
        if (newInput != null) {
            this.userInput = newInput;
        }
    }

    // ---------------------------------------------------------
    //  VALIDATED INPUT METHODS
    // ---------------------------------------------------------

    /** Prompts for and returns a validated int. */
    public int nextInt(String description) {
        System.out.print(description);
        while (!userInput.hasNextInt()) {
            System.out.println("Try again");
            System.out.print(description);
            userInput.nextLine();
        }
        int num = userInput.nextInt();
        userInput.nextLine();
        return num;
    }

    /** Prompts for and returns a validated long. */
    public long nextLong(String description) {
        System.out.print(description);
        while (!userInput.hasNextLong()) {
            System.out.println("Try again");
            System.out.print(description);
            userInput.nextLine();
        }
        long num = userInput.nextLong();
        userInput.nextLine();
        return num;
    }

    /** Prompts for and returns a validated double. */
    public double nextDouble(String description) {
        System.out.print(description);
        while (!userInput.hasNextDouble()) {
            System.out.println("Try again");
            System.out.print(description);
            userInput.nextLine();
        }
        double num = userInput.nextDouble();
        userInput.nextLine();
        return num;
    }

    /** Prompts for and returns a validated float. */
    public float nextFloat(String description) {
        System.out.print(description);
        while (!userInput.hasNextFloat()) {
            System.out.println("Try again");
            System.out.print(description);
            userInput.nextLine();
        }
        float num = userInput.nextFloat();
        userInput.nextLine();
        return num;
    }

    /** Prompts for and returns a single validated character. */
    public char nextChar(String description) {
        while (true) {
            System.out.print(description);
            String input = userInput.nextLine().trim();
            if (input.length() == 1) {
                return input.charAt(0);
            }
            System.out.println("Try again (enter exactly one character)");
        }
    }

    /** Prompts for and returns a single word (token). */
    public String nextWord(String description) {
        System.out.print(description);
        String word = userInput.next();
        userInput.nextLine();
        return word;
    }

    /** Prompts for and returns a full line of text. */
    public String nextLine(String description) {
        System.out.print(description);
        return userInput.nextLine();
    }

    /** Prompts for and returns a validated boolean (yes/no, y/n, true/false). */
    public boolean nextBoolean(String description) {
        while (true) {
            System.out.print(description);
            String input = userInput.nextLine().trim().toLowerCase();

            if (input.equals("yes") || input.equals("y") || input.equals("true"))
                return true;

            if (input.equals("no") || input.equals("n") || input.equals("false"))
                return false;

            System.out.println("Try again (yes/no)");
        }
    }
}
