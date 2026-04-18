import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Terminal entry point for Budgetly.
 *
 * Main only communicates with the Controller. All business logic, state
 * management, and service/model interactions are handled by the Controller
 * and the operations classes it delegates to.
 */
public class Main {

    private static final UserInput  input      = UserInput.getInstance();
    private static final Controller controller = new Controller();

    // =========================================================================
    //  ENTRY POINT
    // =========================================================================

    public static void main(String[] args) {
        printBanner();

        if (!loginMenu()) {
            System.out.println("\nGoodbye!");
            return;
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = input.nextInt("Choice: ");
            switch (choice) {
                case 1 -> profileMenu();
                case 2 -> categoryMenu();
                case 3 -> transactionMenu();
                case 4 -> groupMenu();
                case 5 -> reportMenu();
                case 6 -> running = false;
                default -> System.out.println("  Invalid option.");
            }
        }

        System.out.println("\nGoodbye!");
    }

    // =========================================================================
    //  LOGIN / REGISTER
    // =========================================================================

    /** Returns true when the user is successfully logged in, false if they chose to exit. */
    private static boolean loginMenu() {
        while (true) {
            System.out.println("\n── Account ──");
            System.out.println("  1. Register");
            System.out.println("  2. Login");
            System.out.println("  3. Exit");
            int choice = input.nextInt("Choice: ");
            switch (choice) {
                case 1 -> register();
                case 2 -> loginAccount();
                case 3 -> { return false; }
                default -> System.out.println("  Invalid option.");
            }
            if (controller.isLoggedIn()) return true;
        }
    }

    private static void register() {
        String email    = input.nextLine("Email: ");
        String password = input.nextLine("Password: ");
        if (controller.registerAccount(email, password)) {
            System.out.println("  Account created. You are now logged in as " + email + ".");
        } else {
            System.out.println("  That email is already registered. Please log in.");
        }
    }

    private static void loginAccount() {
        String email    = input.nextLine("Email: ");
        String password = input.nextLine("Password: ");
        if (controller.login(email, password)) {
            System.out.println("  Welcome back, " + email + "!");
        } else {
            System.out.println("  Invalid email or password.");
        }
    }


    // =========================================================================
    //  MAIN MENU
    // =========================================================================

    private static void printBanner() {
        System.out.println("╔═══════════════════════════════╗");
        System.out.println("║        Welcome to Budgetly    ║");
        System.out.println("╚═══════════════════════════════╝");
    }

    private static void printMainMenu() {
        String profileLabel = controller.hasActiveProfile()
                ? "[" + controller.getActiveProfile().getDisplayName() + "]"
                : "[No Profile]";
        String accountLabel = controller.isLoggedIn()
                ? controller.getActiveAccount().getEmail()
                : "";
        System.out.println("\n── Main Menu " + profileLabel + " (" + accountLabel + ") ──");
        System.out.println("  1. Profiles");
        System.out.println("  2. Categories");
        System.out.println("  3. Transactions");
        System.out.println("  4. Transaction Groups");
        System.out.println("  5. Reports");
        System.out.println("  6. Exit");
    }

    // =========================================================================
    //  PROFILES
    // =========================================================================

    private static void profileMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── Profiles ──");
            System.out.println("  1. List profiles");
            System.out.println("  2. Create profile");
            System.out.println("  3. Select active profile");
            System.out.println("  4. Update profile");
            System.out.println("  5. Delete profile");
            System.out.println("  6. Back");
            int choice = input.nextInt("Choice: ");
            switch (choice) {
                case 1 -> listProfiles();
                case 2 -> createProfile();
                case 3 -> selectProfile();
                case 4 -> updateProfile();
                case 5 -> deleteProfile();
                case 6 -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    private static void listProfiles() {
        List<Profile> profiles = controller.getProfiles();
        if (profiles.isEmpty()) { System.out.println("  No profiles found."); return; }
        System.out.println();
        System.out.printf("  %-4s  %-20s  %s%n", "ID", "Name", "Description");
        System.out.println("  " + "─".repeat(50));
        for (Profile p : profiles) {
            String active = (controller.hasActiveProfile()
                    && controller.getActiveProfile().getID() == p.getID()) ? " *" : "";
            String desc   = p.getDescription() != null ? p.getDescription() : "—";
            System.out.printf("  %-4d  %-20s  %s%s%n", p.getID(), p.getDisplayName(), desc, active);
        }
    }

    private static void createProfile() {
        String name = input.nextLine("Name: ");
        String desc = input.nextLine("Description (optional, press Enter to skip): ");
        controller.createProfile(name, desc);
        System.out.println("  Profile '" + name + "' created.");
    }

    private static void selectProfile() {
        listProfiles();
        if (controller.getProfiles().isEmpty()) return;
        int id = input.nextInt("Enter profile ID: ");
        if (controller.selectProfile(id)) {
            System.out.println("  Active profile: " + controller.getActiveProfile().getDisplayName());
        } else {
            System.out.println("  Profile not found.");
        }
    }

    private static void deleteProfile() {
        listProfiles();
        if (controller.getProfiles().isEmpty()) return;
        int id = input.nextInt("Enter profile ID to delete: ");
        if (controller.deleteProfile(id)) {
            System.out.println("  Profile deleted.");
        } else {
            System.out.println("  Profile not found.");
        }
    }

    private static void updateProfile() {
        listProfiles();
        if (controller.getProfiles().isEmpty()) return;
        int id = input.nextInt("Enter profile ID to update: ");
        System.out.println("  Field:  1=name  2=description");
        int field = input.nextInt("Choice: ");
        String value = input.nextLine("New value: ");
        if (controller.updateProfile(id, field, value)) {
            System.out.println("  Profile updated.");
        } else {
            System.out.println("  Profile not found.");
        }
    }

    // =========================================================================
    //  CATEGORIES
    // =========================================================================

    private static void categoryMenu() {
        if (requireActiveProfile()) return;
        boolean back = false;
        while (!back) {
            System.out.println("\n── Categories [" + controller.getActiveProfile().getDisplayName() + "] ──");
            System.out.println("  1. List categories");
            System.out.println("  2. Add category");
            System.out.println("  3. Delete category");
            System.out.println("  4. Back");
            int choice = input.nextInt("Choice: ");
            switch (choice) {
                case 1 -> listCategories();
                case 2 -> createCategory();
                case 3 -> deleteCategory();
                case 4 -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    private static void listCategories() {
        List<Category> cats = controller.getCategoriesForActiveProfile();
        if (cats.isEmpty()) { System.out.println("  No categories."); return; }
        System.out.println();
        System.out.printf("  %-4s  %-20s  %-8s  %s%n", "ID", "Name", "Type", "Description");
        System.out.println("  " + "─".repeat(60));
        for (Category c : cats) {
            String desc = c.getDescription() != null ? c.getDescription() : "—";
            System.out.printf("  %-4d  %-20s  %-8s  %s%n",
                    c.getCategoryId(), c.getName(), c.getType(), desc);
        }
    }

    private static void createCategory() {
        String name = input.nextLine("Category name: ");
        System.out.println("  Type:  1=income  2=expense  3=both");
        int typeChoice = input.nextInt("Choice: ");
        String type = switch (typeChoice) {
            case 1  -> "income";
            case 2  -> "expense";
            default -> "both";
        };
        String desc = input.nextLine("Description (optional): ");
        try {
            controller.addCategory(name, type, desc);
            System.out.println("  Category '" + name + "' created.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void deleteCategory() {
        listCategories();
        if (controller.getCategoriesForActiveProfile().isEmpty()) return;
        int id = input.nextInt("Enter category ID to delete: ");
        if (controller.removeCategory(id)) {
            System.out.println("  Category deleted.");
        } else {
            System.out.println("  Category not found.");
        }
    }

    // =========================================================================
    //  TRANSACTIONS
    // =========================================================================

    private static void transactionMenu() {
        if (requireActiveProfile()) return;
        boolean back = false;
        while (!back) {
            System.out.println("\n── Transactions [" + controller.getActiveProfile().getDisplayName() + "] ──");
            System.out.println("  1. List transactions");
            System.out.println("  2. Add transaction");
            System.out.println("  3. Delete transaction");
            System.out.println("  4. Back");
            int choice = input.nextInt("Choice: ");
            switch (choice) {
                case 1 -> listTransactions();
                case 2 -> createTransaction();
                case 3 -> deleteTransaction();
                case 4 -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    private static void listTransactions() {
        List<Transaction> txs = controller.getTransactionsForActiveProfile();
        if (txs.isEmpty()) { System.out.println("  No transactions."); return; }
        System.out.println();
        System.out.printf("  %-4s  %-12s  %-8s  %-10s  %-6s  %s%n",
                "ID", "Date", "Type", "Amount", "Cat.", "Note");
        System.out.println("  " + "─".repeat(65));
        for (Transaction t : txs) {
            String note = t.getNote() != null ? t.getNote() : "—";
            System.out.printf("  %-4d  %-12s  %-8s  $%-9.2f  %-6d  %s%n",
                    t.getTransactionId(), t.getDate(), t.getType(),
                    t.getAmount(), t.getCategoryId(), note);
        }
    }

    private static void createTransaction() {
        if (controller.getGroupsForActiveProfile().isEmpty()) {
            System.out.println("  Create a group first. Transactions must belong to a group.");
            return;
        }
        listGroups();
        int gid = input.nextInt("Group ID to add transaction to: ");
        listCategories();
        if (controller.getCategoriesForActiveProfile().isEmpty()) {
            System.out.println("  Add a category first.");
            return;
        }
        try {
            double amount  = input.nextDouble("Amount: $");
            System.out.println("  Type:  1=income  2=expense");
            int typeChoice = input.nextInt("Choice: ");
            String type    = typeChoice == 1 ? "income" : "expense";
            int catId      = input.nextInt("Category ID: ");
            String dateStr = input.nextLine("Date (YYYY-MM-DD, blank = today): ");
            LocalDate date;
            try {
                date = dateStr.isBlank() ? LocalDate.now() : LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid date format. Using today.");
                date = LocalDate.now();
            }
            String note = input.nextLine("Note (optional): ");
            controller.addTransaction(gid, amount, type, catId, date, note);
            System.out.println("  Transaction added.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void deleteTransaction() {
        listTransactions();
        if (controller.getTransactionsForActiveProfile().isEmpty()) return;
        int id = input.nextInt("Enter transaction ID to delete: ");
        if (controller.removeTransaction(id)) {
            System.out.println("  Transaction deleted.");
        } else {
            System.out.println("  Transaction not found.");
        }
    }

    // =========================================================================
    //  TRANSACTION GROUPS
    // =========================================================================

    private static void groupMenu() {
        if (requireActiveProfile()) return;
        boolean back = false;
        while (!back) {
            System.out.println("\n── Transaction Groups [" + controller.getActiveProfile().getDisplayName() + "] ──");
            System.out.println("  1. List groups");
            System.out.println("  2. Create group");
            System.out.println("  3. View group transactions");
            System.out.println("  4. Delete group");
            System.out.println("  5. Back");
            int choice = input.nextInt("Choice: ");
            switch (choice) {
                case 1 -> listGroups();
                case 2 -> createGroup();
                case 3 -> viewGroupTransactions();
                case 4 -> deleteGroup();
                case 5 -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    private static void listGroups() {
        List<TransactionGroup> groups = controller.getGroupsForActiveProfile();
        if (groups.isEmpty()) { System.out.println("  No groups."); return; }
        System.out.println();
        System.out.printf("  %-4s  %-22s  %-14s  %s%n", "ID", "Name", "Transactions", "Description");
        System.out.println("  " + "─".repeat(60));
        for (TransactionGroup g : groups) {
            String desc = g.getDescription() != null ? g.getDescription() : "—";
            System.out.printf("  %-4d  %-22s  %-14d  %s%n",
                    g.getGroupId(), g.getName(), g.getTransactionList().size(), desc);
        }
    }

    private static void createGroup() {
        String name = input.nextLine("Group name: ");
        String desc = input.nextLine("Description (optional): ");
        try {
            controller.addGroup(name, desc);
            System.out.println("  Group '" + name + "' created.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void viewGroupTransactions() {
        listGroups();
        if (controller.getGroupsForActiveProfile().isEmpty()) return;
        int gid = input.nextInt("Group ID: ");

        List<Transaction> txs = controller.getGroupTransactions(gid);
        if (txs == null) {
            System.out.println("  Group not found.");
            return;
        }
        if (txs.isEmpty()) {
            System.out.println("  No transactions in this group.");
            return;
        }
        System.out.println("\n  Transactions in group '" + controller.getGroupName(gid) + "':");
        System.out.printf("  %-4s  %-12s  %-8s  %-10s  %s%n",
                "ID", "Date", "Type", "Amount", "Note");
        System.out.println("  " + "─".repeat(55));
        for (Transaction t : txs) {
            String note = t.getNote() != null ? t.getNote() : "—";
            System.out.printf("  %-4d  %-12s  %-8s  $%-9.2f  %s%n",
                    t.getTransactionId(), t.getDate(), t.getType(),
                    t.getAmount(), note);
        }
    }

    private static void deleteGroup() {
        listGroups();
        if (controller.getGroupsForActiveProfile().isEmpty()) return;
        int id = input.nextInt("Enter group ID to delete: ");
        if (controller.removeGroup(id)) {
            System.out.println("  Group and its transactions deleted.");
        } else {
            System.out.println("  Group not found.");
        }
    }

    // =========================================================================
    //  REPORTS
    // =========================================================================

    private static void reportMenu() {
        if (requireActiveProfile()) return;
        boolean back = false;
        while (!back) {
            System.out.println("\n── Reports [" + controller.getActiveProfile().getDisplayName() + "] ──");
            System.out.println("  1. Summary");
            System.out.println("  2. All transactions");
            System.out.println("  3. Net balance");
            System.out.println("  4. Chart: expenses by category");
            System.out.println("  5. Chart: income by category");
            System.out.println("  6. Chart: monthly expense trend");
            System.out.println("  7. Chart: income vs expenses by month");
            System.out.println("  8. Back");
            int choice = input.nextInt("Choice: ");
            switch (choice) {
                case 1 -> controller.generateReport("summary");
                case 2 -> controller.generateReport("transactions");
                case 3 -> controller.generateReport("balance");
                case 4 -> controller.printCategoryChart("expense");
                case 5 -> controller.printCategoryChart("income");
                case 6 -> controller.printMonthlyTrend();
                case 7 -> controller.printIncomeVsExpense();
                case 8 -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    /** Returns true (and prints a message) if no profile is active. */
    private static boolean requireActiveProfile() {
        if (!controller.hasActiveProfile()) {
            System.out.println("  No active profile. Go to Profiles → Create/Select first.");
            return true;
        }
        return false;
    }
}
