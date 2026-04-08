import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Terminal entry point for Budgetly.
 *
 * Data is currently held in memory using the existing model classes.
 * Each spot that will eventually call a DAO is marked with a TODO comment
 * so partners can slot in their database code without restructuring the menus.
 *
 * To wire in MySQL:
 *   1. Add your DAO classes (ProfileDAO, CategoryDAO, TransactionDAO, etc.)
 *   2. Replace every "TODO: DAO" comment below with the matching DAO call.
 *   3. Call DatabaseConnection.getConnection() at startup and
 *      DatabaseConnection.close() at shutdown (stubs are already here).
 */
public class Main {

    // ── In-memory stores (replace with DAO calls once DB layer is ready) ─────
    private static final List<Profile>          profiles     = new ArrayList<>();
    private static final List<Category>         categories   = new ArrayList<>();
    private static final List<Transaction>      transactions = new ArrayList<>();
    private static final List<TransactionGroup> groups       = new ArrayList<>();

    private static Profile activeProfile = null;

    // Auto-increment counters — swap these out for DB-generated IDs later
    private static final AtomicInteger profileSeq     = new AtomicInteger(1);
    private static final AtomicInteger categorySeq    = new AtomicInteger(1);
    private static final AtomicInteger transactionSeq = new AtomicInteger(1);
    private static final AtomicInteger groupSeq       = new AtomicInteger(1);

    private static final UserInput input = UserInput.getInstance();

    // =========================================================================
    //  ENTRY POINT
    // =========================================================================

    public static void main(String[] args) {
        printBanner();

        // TODO: DAO — DatabaseConnection.getConnection(); then load data from MySQL

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

        // TODO: DAO — DatabaseConnection.close();
        System.out.println("\nGoodbye!");
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
        String profileLabel = activeProfile != null
                ? "[" + activeProfile.getDisplayName() + "]"
                : "[No Profile]";
        System.out.println("\n── Main Menu " + profileLabel + " ──");
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
            System.out.println("  4. Delete profile");
            System.out.println("  5. Back");
            int choice = input.nextInt("Choice: ");
            switch (choice) {
                case 1 -> listProfiles();
                case 2 -> createProfile();
                case 3 -> selectProfile();
                case 4 -> deleteProfile();
                case 5 -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    private static void listProfiles() {
        // TODO: DAO — profiles = ProfileDAO.getAll();
        if (profiles.isEmpty()) { System.out.println("  No profiles found."); return; }
        System.out.println();
        System.out.printf("  %-4s  %-20s  %s%n", "ID", "Name", "Description");
        System.out.println("  " + "─".repeat(50));
        for (Profile p : profiles) {
            String active = (activeProfile != null && activeProfile.getID() == p.getID()) ? " *" : "";
            String desc   = p.getDescription() != null ? p.getDescription() : "—";
            System.out.printf("  %-4d  %-20s  %s%s%n", p.getID(), p.getDisplayName(), desc, active);
        }
    }

    private static void createProfile() {
        String name = input.nextLine("Name: ");
        String desc = input.nextLine("Description (optional, press Enter to skip): ");

        Profile p = new Profile();
        p.setID(profileSeq.getAndIncrement());
        p.setDisplayName(name);
        p.setDescription(desc.isBlank() ? null : desc);
        p.setBankRoll(0.0);

        profiles.add(p);
        if (activeProfile == null) activeProfile = p;

        // TODO: DAO — ProfileDAO.insert(p);
        System.out.println("  Profile '" + name + "' created.");
    }

    private static void selectProfile() {
        listProfiles();
        if (profiles.isEmpty()) return;
        int id = input.nextInt("Enter profile ID: ");
        profiles.stream()
                .filter(p -> p.getID() == id)
                .findFirst()
                .ifPresentOrElse(
                    p -> { activeProfile = p; System.out.println("  Active profile: " + p.getDisplayName()); },
                    ()  -> System.out.println("  Profile not found.")
                );
    }

    private static void deleteProfile() {
        listProfiles();
        if (profiles.isEmpty()) return;
        int id = input.nextInt("Enter profile ID to delete: ");
        boolean removed = profiles.removeIf(p -> p.getID() == id);
        if (removed) {
            transactions.removeIf(t -> t.getProfileId() == id);
            categories.removeIf(c -> c.getProfileId() == id);
            groups.clear(); // groups are profile-scoped; clear active profile's groups
            if (activeProfile != null && activeProfile.getID() == id) {
                activeProfile = profiles.isEmpty() ? null : profiles.get(0);
            }
            // TODO: DAO — ProfileDAO.delete(id);
            System.out.println("  Profile deleted.");
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
            System.out.println("\n── Categories [" + activeProfile.getDisplayName() + "] ──");
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
        // TODO: DAO — categories = CategoryDAO.getByProfile(activeProfile.getID());
        List<Category> mine = categoriesForProfile();
        if (mine.isEmpty()) { System.out.println("  No categories."); return; }
        System.out.println();
        System.out.printf("  %-4s  %-20s  %-8s  %s%n", "ID", "Name", "Type", "Description");
        System.out.println("  " + "─".repeat(60));
        for (Category c : mine) {
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
            Category c = new Category(
                    categorySeq.getAndIncrement(), name,
                    desc.isBlank() ? null : desc,
                    type, activeProfile.getID());
            categories.add(c);
            // TODO: DAO — CategoryDAO.insert(c);
            System.out.println("  Category '" + name + "' created.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void deleteCategory() {
        listCategories();
        if (categoriesForProfile().isEmpty()) return;
        int id = input.nextInt("Enter category ID to delete: ");
        boolean removed = categories.removeIf(
                c -> c.getCategoryId() == id && c.getProfileId() == activeProfile.getID());
        if (removed) {
            // TODO: DAO — CategoryDAO.delete(id);
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
            System.out.println("\n── Transactions [" + activeProfile.getDisplayName() + "] ──");
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
        // TODO: DAO — transactions = TransactionDAO.getByProfile(activeProfile.getID());
        List<Transaction> mine = transactionsForProfile();
        if (mine.isEmpty()) { System.out.println("  No transactions."); return; }
        System.out.println();
        System.out.printf("  %-4s  %-12s  %-8s  %-10s  %-6s  %s%n",
                "ID", "Date", "Type", "Amount", "Cat.", "Note");
        System.out.println("  " + "─".repeat(65));
        for (Transaction t : mine) {
            String note = t.getNote() != null ? t.getNote() : "—";
            System.out.printf("  %-4d  %-12s  %-8s  $%-9.2f  %-6d  %s%n",
                    t.getTransactionId(), t.getDate(), t.getType(),
                    t.getAmount(), t.getCategoryId(), note);
        }
    }

    private static void createTransaction() {
        listCategories();
        if (categoriesForProfile().isEmpty()) {
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

            Transaction t = new Transaction(
                    transactionSeq.getAndIncrement(), amount, type, catId,
                    date, note.isBlank() ? null : note, null, null,
                    activeProfile.getID());
            transactions.add(t);
            // TODO: DAO — TransactionDAO.insert(t);
            System.out.println("  Transaction added.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void deleteTransaction() {
        listTransactions();
        if (transactionsForProfile().isEmpty()) return;
        int id = input.nextInt("Enter transaction ID to delete: ");
        boolean removed = transactions.removeIf(
                t -> t.getTransactionId() == id && t.getProfileId() == activeProfile.getID());
        if (removed) {
            // TODO: DAO — TransactionDAO.delete(id);
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
            System.out.println("\n── Transaction Groups [" + activeProfile.getDisplayName() + "] ──");
            System.out.println("  1. List groups");
            System.out.println("  2. Create group");
            System.out.println("  3. Add transaction to group");
            System.out.println("  4. View group transactions");
            System.out.println("  5. Delete group");
            System.out.println("  6. Back");
            int choice = input.nextInt("Choice: ");
            switch (choice) {
                case 1 -> listGroups();
                case 2 -> createGroup();
                case 3 -> addTransactionToGroup();
                case 4 -> viewGroupTransactions();
                case 5 -> deleteGroup();
                case 6 -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    private static void listGroups() {
        // TODO: DAO — groups = TransactionGroupDAO.getByProfile(activeProfile.getID());
        List<TransactionGroup> mine = groupsForProfile();
        if (mine.isEmpty()) { System.out.println("  No groups."); return; }
        System.out.println();
        System.out.printf("  %-4s  %-22s  %-14s  %s%n", "ID", "Name", "Transactions", "Description");
        System.out.println("  " + "─".repeat(60));
        for (TransactionGroup g : mine) {
            String desc = g.getDescription() != null ? g.getDescription() : "—";
            System.out.printf("  %-4d  %-22s  %-14d  %s%n",
                    g.getGroupId(), g.getName(), g.getTransactionList().size(), desc);
        }
    }

    private static void createGroup() {
        String name = input.nextLine("Group name: ");
        String desc = input.nextLine("Description (optional): ");
        try {
            TransactionGroup g = new TransactionGroup(
                    groupSeq.getAndIncrement(), name,
                    desc.isBlank() ? null : desc, null);
            groups.add(g);
            activeProfile.getTransactionGroups().add(g);
            // TODO: DAO — TransactionGroupDAO.insert(g, activeProfile.getID());
            System.out.println("  Group '" + name + "' created.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void addTransactionToGroup() {
        listGroups();
        List<TransactionGroup> mine = groupsForProfile();
        if (mine.isEmpty()) return;
        int gid = input.nextInt("Group ID: ");

        listTransactions();
        if (transactionsForProfile().isEmpty()) return;
        int tid = input.nextInt("Transaction ID: ");

        TransactionGroup targetGroup = mine.stream()
                .filter(g -> g.getGroupId() == gid)
                .findFirst().orElse(null);

        if (targetGroup == null) { System.out.println("  Group not found."); return; }

        Transaction targetTx = transactions.stream()
                .filter(t -> t.getTransactionId() == tid
                          && t.getProfileId() == activeProfile.getID())
                .findFirst().orElse(null);

        if (targetTx == null) { System.out.println("  Transaction not found."); return; }

        targetGroup.addTransacToList(targetTx);
        // TODO: DAO — TransactionGroupDAO.addTransaction(gid, tid);
        System.out.println("  Transaction added to group.");
    }

    private static void viewGroupTransactions() {
        listGroups();
        List<TransactionGroup> mine = groupsForProfile();
        if (mine.isEmpty()) return;
        int gid = input.nextInt("Group ID: ");

        mine.stream()
            .filter(g -> g.getGroupId() == gid)
            .findFirst()
            .ifPresentOrElse(g -> {
                if (g.getTransactionList().isEmpty()) {
                    System.out.println("  No transactions in this group.");
                    return;
                }
                System.out.println("\n  Transactions in group '" + g.getName() + "':");
                System.out.printf("  %-4s  %-12s  %-8s  %-10s  %s%n",
                        "ID", "Date", "Type", "Amount", "Note");
                System.out.println("  " + "─".repeat(55));
                for (Transaction t : g.getTransactionList()) {
                    String note = t.getNote() != null ? t.getNote() : "—";
                    System.out.printf("  %-4d  %-12s  %-8s  $%-9.2f  %s%n",
                            t.getTransactionId(), t.getDate(), t.getType(),
                            t.getAmount(), note);
                }
            }, () -> System.out.println("  Group not found."));
    }

    private static void deleteGroup() {
        listGroups();
        List<TransactionGroup> mine = groupsForProfile();
        if (mine.isEmpty()) return;
        int id = input.nextInt("Enter group ID to delete: ");
        boolean removed = groups.removeIf(g -> g.getGroupId() == id);
        if (removed) {
            activeProfile.getTransactionGroups().removeIf(g -> g.getGroupId() == id);
            // TODO: DAO — TransactionGroupDAO.delete(id);
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
            System.out.println("\n── Reports [" + activeProfile.getDisplayName() + "] ──");
            System.out.println("  1. Summary");
            System.out.println("  2. All transactions");
            System.out.println("  3. Net balance");
            System.out.println("  4. Chart: expenses by category");
            System.out.println("  5. Chart: income by category");
            System.out.println("  6. Chart: monthly expense trend");
            System.out.println("  7. Chart: income vs expenses by month");
            System.out.println("  8. Back");
            int choice = input.nextInt("Choice: ");

            // Sync active profile's transaction groups with in-memory groups
            activeProfile.setTransactionGroups(groupsForProfile());

            List<Transaction> mine = transactionsForProfile();
            List<Category>    cats = categoriesForProfile();

            ReportGenerator rg = new ReportGenerator();
            rg.addProfile(activeProfile);

            switch (choice) {
                case 1 -> rg.generateReport("summary");
                case 2 -> rg.generateReport("transactions");
                case 3 -> rg.generateReport("balance");
                case 4 -> ChartPrinter.printCategoryChart(mine, cats, "expense");
                case 5 -> ChartPrinter.printCategoryChart(mine, cats, "income");
                case 6 -> ChartPrinter.printMonthlyTrend(mine);
                case 7 -> ChartPrinter.printIncomeVsExpense(mine);
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
        if (activeProfile == null) {
            System.out.println("  No active profile. Go to Profiles → Create/Select first.");
            return true;
        }
        return false;
    }

    private static List<Category> categoriesForProfile() {
        return categories.stream()
                .filter(c -> c.getProfileId() == activeProfile.getID())
                .collect(Collectors.toList());
    }

    private static List<Transaction> transactionsForProfile() {
        return transactions.stream()
                .filter(t -> t.getProfileId() == activeProfile.getID())
                .collect(Collectors.toList());
    }

    private static List<TransactionGroup> groupsForProfile() {
        // Groups are stored globally; filter by what belongs to the active profile.
        // TODO: DAO — once partners add TransactionGroupDAO, filter by profileId from DB.
        return new ArrayList<>(groups);
    }
}
