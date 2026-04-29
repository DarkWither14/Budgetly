import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Controller coordinates high-level create/delete operations for Accounts,
 * TransactionGroups, Transactions, and Categories.
 *
 * <p>This class uses the provided operations classes as the persistence-facing
 * collaborators and also keeps simple in-memory maps so that objects can be
 * found again by ID while the program is running.</p>
 *
 *TO BE ADDED: DATABASE SUPPORT
 * </ul>
 */
public class Controller {

    private AccountOperations accOperations;
    private TransactionOperations transOperations;
    private CategoryOperations categoryOperations;
    private DatabaseOperations databaseOperations;
    private VerifyData verifyData;
    private DatabaseConnection databaseConnection;

    // ── Account / session state ───────────────────────────────────────────────
    private Account activeAccount;

    // ── Profile state ─────────────────────────────────────────────────────────
    private final List<Profile> profiles;
    private Profile              activeProfile;

    // ── ID-keyed maps (used by diagram-compliant methods and profile-scoped ops) ─
    private final Map<Integer, Account>          accounts;
    private final Map<Integer, TransactionGroup> transactionGroups;
    private final Map<Integer, Transaction>      transactions;
    private final Map<Integer, Category>         categories;

    // ── Auto-increment counters ───────────────────────────────────────────────
    private final AtomicInteger profileSeq     = new AtomicInteger(1);
    private final AtomicInteger transactionSeq = new AtomicInteger(1);
    private final AtomicInteger groupSeq       = new AtomicInteger(1);

    private int activeProfileId;

    public Controller() {
        this(1);
    }

    public Controller(int activeProfileId) {
        this.accOperations      = new AccountOperations();
        this.transOperations    = new TransactionOperations();
        this.categoryOperations = new CategoryOperations();
        this.databaseOperations = new DatabaseOperations();
        this.profiles           = new ArrayList<>();
        this.activeProfile      = null;
        this.accounts           = new HashMap<>();
        this.transactionGroups  = new HashMap<>();
        this.transactions       = new HashMap<>();
        this.categories         = new HashMap<>();
        this.activeAccount      = null;
        this.activeProfileId    = activeProfileId;
        this.verifyData         = new VerifyData(this);
        
        this.databaseConnection = MySQLDatabaseConnection.getInstance();
        this.accOperations.setDatabaseConnection(this.databaseConnection);
        this.transOperations.setDatabaseConnection(this.databaseConnection);
        this.categoryOperations.setDatabaseConnection(this.databaseConnection);
        this.databaseOperations.setDatabaseConnection(this.databaseConnection);

        this.databaseOperations.initializeDatabase();
        this.databaseOperations.initializeTables();
    }

    public AccountOperations getAccOperations() {
        return accOperations;
    }

    public TransactionOperations getTransOperations() {
        return transOperations;
    }

    public CategoryOperations getCategoryOperations() {
        return categoryOperations;
    }

    public int getActiveProfileId() {
        return activeProfileId;
    }

    public void setActiveProfileId(int activeProfileId) {
        if (activeProfileId <= 0) {
            throw new IllegalArgumentException("Active profile ID must be greater than 0.");
        }
        this.activeProfileId = activeProfileId;
    }

    public VerifyData getVerifyData() { return verifyData; }

    // =========================================================================
    //  ACCOUNT / SESSION OPERATIONS
    // =========================================================================

    /**
     * Registers a new account with the given email and password.
     * The password is stored as a hash. Logs the new user in automatically.
     *
     * @return true if registration succeeded, false if the email is already taken
     */
    public boolean registerAccount(String email, String password) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email cannot be blank.");
        for (Account acc : accounts.values()) {
            if (acc.getEmail().equalsIgnoreCase(email.trim())) return false;
        }
        int id = accounts.size() + 1;
        Account account = new Account();
        account.setAccountID(id);
        account.setEmail(email.trim());
        account.setPasswordHash(password.hashCode());
        accounts.put(id, account);
        accOperations.setAccount(account);
        activeAccount = account;
        return true;
    }

    /**
     * Logs in with the given email and password.
     *
     * @return true if credentials matched, false otherwise
     */
    public boolean login(String email, String password) {
        for (Account acc : accounts.values()) {
            if (acc.getEmail().equalsIgnoreCase(email.trim())
                    && acc.getPasswordHash() == password.hashCode()) {
                activeAccount = acc;
                accOperations.setAccount(acc);
                return true;
            }
        }
        return false;
    }

    public void logout() {
        activeAccount  = null;
        activeProfile  = null;
        activeProfileId = 1;
        accOperations.setAccount(null);
    }

    public boolean isLoggedIn() { return activeAccount != null; }

    public Account getActiveAccount() { return activeAccount; }



    public void createProfile(String name, String desc) {
        Profile p = new Profile();
        p.setID(profileSeq.getAndIncrement());
        p.setDisplayName(name);
        p.setDescription(desc == null || desc.isBlank() ? null : desc);
        p.setBankRoll(0.0);
        profiles.add(p);
        if (activeAccount != null) {
            activeAccount.addProfileToList(p);
        }
        accOperations.addProfileDB(p);
        if (activeProfile == null) {
            activeProfile = p;
            activeProfileId = p.getID();
        }
    }

    public List<Profile> getProfiles() {
        return new ArrayList<>(profiles);
    }

    public boolean selectProfile(int id) {
        for (Profile p : profiles) {
            if (p.getID() == id) {
                activeProfile = p;
                activeProfileId = p.getID();
                return true;
            }
        }
        return false;
    }

    public boolean deleteProfile(int id) {
        Profile toDelete = null;
        for (Profile p : profiles) {
            if (p.getID() == id) { toDelete = p; break; }
        }
        if (toDelete == null) return false;
        profiles.remove(toDelete);
        transactions.entrySet().removeIf(e -> e.getValue().getProfileId() == id);
        categories.entrySet().removeIf(e -> e.getValue().getProfileId() == id);
        transactionGroups.clear();
        if (activeAccount != null) {
            activeAccount.removeProfileById(id);
        }
        if (activeProfile != null && activeProfile.getID() == id) {
            activeProfile = profiles.isEmpty() ? null : profiles.get(0);
            activeProfileId = activeProfile != null ? activeProfile.getID() : 1;
        }
        accOperations.deleteProfileDB(toDelete);
        return true;
    }

    public Profile getActiveProfile() {
        return activeProfile;
    }

    public boolean hasActiveProfile() {
        return activeProfile != null;
    }

    /**
     * Updates a profile field. field: 1 = display name, 2 = description.
     */
    public boolean updateProfile(int id, int field, String value) {
        for (Profile p : profiles) {
            if (p.getID() == id) {
                if (field == 1) p.setDisplayName(value);
                else if (field == 2) p.setDescription(value.isBlank() ? null : value);
                accOperations.updateProfileDB(p);
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    //  CATEGORY OPERATIONS (profile-scoped, used by Main via Controller)
    // =========================================================================

    public void addCategory(String name, String type, String desc) {
        int id = nextAvailableCategoryId();
        Category c = new Category(
                id, name,
                desc == null || desc.isBlank() ? null : desc,
                type, activeProfileId);
        categories.put(id, c);
        categoryOperations.setCategory(c);
        categoryOperations.createCategoryDB(id, name);
    }

    public List<Category> getCategoriesForActiveProfile() {
        return categories.values().stream()
                .filter(c -> c.getProfileId() == activeProfileId)
                .collect(Collectors.toList());
    }

    public boolean removeCategory(int id) {
        Category c = categories.get(id);
        if (c == null || c.getProfileId() != activeProfileId) return false;
        categories.remove(id);
        categoryOperations.deleteCategoryDB(id);
        return true;
    }

    // =========================================================================
    //  TRANSACTION OPERATIONS (profile-scoped, used by Main via Controller)
    // =========================================================================

    /**
     * Creates a Transaction inside the specified group (composition).
     * A Transaction cannot exist without a parent TransactionGroup.
     */
    public void addTransaction(int groupId, double amount, String type, int catId, LocalDate date, String note) {
        TransactionGroup group = transactionGroups.get(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found with ID " + groupId + ".");
        }
        int id = transactionSeq.getAndIncrement();
        Transaction t = new Transaction(
                id, amount, type, catId,
                date, note == null || note.isBlank() ? null : note,
                null, groupId, activeProfileId);
        group.addTransacToList(t);
        transactions.put(id, t);
        transOperations.createTransactionDB(t);
    }

    public List<Transaction> getTransactionsForActiveProfile() {
        return transactionGroups.values().stream()
                .flatMap(g -> g.getTransactionList().stream())
                .filter(t -> t.getProfileId() == activeProfileId)
                .collect(Collectors.toList());
    }

    public boolean removeTransaction(int id) {
        Transaction t = transactions.get(id);
        if (t == null || t.getProfileId() != activeProfileId) return false;
        Integer groupId = t.getTransactionGroupId();
        if (groupId != null) {
            TransactionGroup g = transactionGroups.get(groupId);
            if (g != null) g.deleteTransacFromList(id);
        }
        transactions.remove(id);
        transOperations.deleteTransactionDB(id);
        return true;
    }

    // =========================================================================
    //  TRANSACTION GROUP OPERATIONS (profile-scoped, used by Main via Controller)
    // =========================================================================

    public void addGroup(String name, String desc) {
        int id = groupSeq.getAndIncrement();
        TransactionGroup g = new TransactionGroup(
                id, name,
                desc == null || desc.isBlank() ? null : desc, null);
        transactionGroups.put(id, g);
        activeProfile.getTransactionGroups().add(g);
        transOperations.setTransactionGroup(g);
        transOperations.createTransactionGroupDB(g);
    }

    public List<TransactionGroup> getGroupsForActiveProfile() {
        return new ArrayList<>(transactionGroups.values());
    }

    public List<Transaction> getGroupTransactions(int gid) {
        TransactionGroup g = transactionGroups.get(gid);
        return g != null ? g.getTransactionList() : null;
    }

    public String getGroupName(int gid) {
        TransactionGroup g = transactionGroups.get(gid);
        return g != null ? g.getName() : null;
    }

    public boolean removeGroup(int id) {
        TransactionGroup g = transactionGroups.remove(id);
        if (g == null) return false;
        if (activeProfile != null) {
            activeProfile.getTransactionGroups().removeIf(gr -> gr.getGroupId() == id);
        }
        transOperations.deleteTransactionGroupDB(id);
        return true;
    }

    // =========================================================================
    //  REPORT OPERATIONS (used by Main via Controller)
    // =========================================================================

    public void generateReport(String type) {
        activeProfile.setTransactionGroups(getGroupsForActiveProfile());
        ReportGenerator rg = new ReportGenerator();
        rg.addProfile(activeProfile);
        rg.generateReport(type);
    }

    public void printCategoryChart(String chartType) {
        ChartPrinter.printCategoryChart(
                getTransactionsForActiveProfile(),
                getCategoriesForActiveProfile(),
                chartType);
    }

    public void printMonthlyTrend() {
        ChartPrinter.printMonthlyTrend(getTransactionsForActiveProfile());
    }

    public void printIncomeVsExpense() {
        ChartPrinter.printIncomeVsExpense(getTransactionsForActiveProfile());
    }

    /**
     * Supports the class diagram method:
     * createAccount(accID:int, email:String, passHash:int): void
     */
    public void createAccount(int accID, String email, int passHash) {
        if (accounts.containsKey(accID)) {
            throw new IllegalArgumentException("An account with ID " + accID + " already exists.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be blank.");
        }
        Account account = new Account();
        account.setAccountID(accID);
        account.setEmail(email.trim());
        account.setPasswordHash(passHash);
        accounts.put(accID, account);
        accOperations.setAccount(account);
    }

    /**
     * Supports the class diagram method:
     * deleteAccount(accID:int): void
     */
    public void deleteAccount(int accID) {
        Account removed = accounts.remove(accID);
        if (removed == null) {
            throw new IllegalArgumentException("No account found with ID " + accID + ".");
        }

        if (accOperations.getAccount() == removed) {
            accOperations.setAccount(null);
        }
    }

    /**
     * Supports the class diagram method:
     * createTransGroup(groupID:int, name:String, description:String, rcptPath:String)
     */
    public void createTransGroup(int groupID, String name, String description, String rcptPath) {
        if (transactionGroups.containsKey(groupID)) {
            throw new IllegalArgumentException("A transaction group with ID " + groupID + " already exists.");
        }

        TransactionGroup group = new TransactionGroup(groupID, name, description, rcptPath);
        transactionGroups.put(groupID, group);
        transOperations.setTransactionGroup(group);
        transOperations.createTransactionGroupDB(group);
    }

    /**
     * Supports the class diagram method:
     * deleteTransGroup(groupID:int)
     *
     * Deleting a group also removes all transactions belonging to that group
     * from this controller and calls the transaction delete operation for each.
     */
    public void deleteTransGroup(int groupID) {
        TransactionGroup group = transactionGroups.remove(groupID);
        if (group == null) {
            throw new IllegalArgumentException("No transaction group found with ID " + groupID + ".");
        }

        Iterator<Map.Entry<Integer, Transaction>> iterator = transactions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Transaction> entry = iterator.next();
            Transaction transaction = entry.getValue();
            Integer transactionGroupId = transaction.getTransactionGroupId();

            if (transactionGroupId != null && transactionGroupId == groupID) {
                transOperations.deleteTransactionDB(transaction.getTransactionId());
                iterator.remove();
            }
        }

        transOperations.deleteTransactionGroupDB(groupID);

        if (transOperations.getTransactionGroup() == group) {
            transOperations.setTransactionGroup(null);
        }
    }

    /**
     * Supports the class diagram method:
     * createTransaction(transID:int, date:String, amount:double, type:boolean, transGroup:int)
     *
     * Assumption: type == true means income, false means expense.
     * A default compatible category is created/used automatically because the
     * uploaded Transaction constructor requires category information while the
     * diagram method does not provide it.
     */
    public void createTransaction(int transID, String date, double amount, boolean type, int transGroup) {
        if (transactions.containsKey(transID)) {
            throw new IllegalArgumentException("A transaction with ID " + transID + " already exists.");
        }

        TransactionGroup group = transactionGroups.get(transGroup);
        if (group == null) {
            throw new IllegalArgumentException("No transaction group found with ID " + transGroup + ".");
        }

        String transactionType = type ? "income" : "expense";
        Category category = findOrCreateDefaultCategory(type);
        LocalDate parsedDate = parseDate(date);

        Transaction transaction = new Transaction(
                transID,
                amount,
                transactionType,
                category.getCategoryId(),
                parsedDate,
                null,
                group.getReceiptFilePath(),
                group.getGroupId(),
                activeProfileId
        );

        group.addTransacToList(transaction);
        transactions.put(transID, transaction);
        transOperations.createTransactionDB(transaction);
    }

    /**
     * Supports the class diagram method:
     * deleteTransaction(transID:int)
     */
    public void deleteTransaction(int transID) {
        Transaction removed = transactions.remove(transID);
        if (removed == null) {
            throw new IllegalArgumentException("No transaction found with ID " + transID + ".");
        }

        Integer groupId = removed.getTransactionGroupId();
        if (groupId != null) {
            TransactionGroup group = transactionGroups.get(groupId);
            if (group != null) {
                group.deleteTransacFromList(transID);
            }
        }

        transOperations.deleteTransactionDB(transID);
    }

    /**
     * Supports the class diagram method:
     * createCategory(categID:int, categName:String, description:String)
     *
     * Assumption: since the diagram snippet does not include a type, this
     * method creates a category of type BOTH for the active profile.
     */
    public void createCategory(int categID, String categName, String description) {
        if (categories.containsKey(categID)) {
            throw new IllegalArgumentException("A category with ID " + categID + " already exists.");
        }

        Category category = new Category(categID, categName, description, Category.CategoryType.BOTH, activeProfileId);
        categories.put(categID, category);
        categoryOperations.setCategory(category);
        categoryOperations.createCategoryDB(categID, categName);
    }

    /**
     * Supports the class diagram method:
     * deleteCategory(categID:int)
     */
    public void deleteCategory(int categID) {
        Category removed = categories.remove(categID);
        if (removed == null) {
            throw new IllegalArgumentException("No category found with ID " + categID + ".");
        }

        categoryOperations.deleteCategoryDB(categID);

        if (categoryOperations.getCategory() == removed) {
            categoryOperations.setCategory(null);
        }
    }

    /** Utility lookup if other classes need direct access. */
    public Account getAccount(int accID) {
        return accounts.get(accID);
    }

    /** Utility lookup if other classes need direct access. */
    public TransactionGroup getTransactionGroup(int groupID) {
        return transactionGroups.get(groupID);
    }

    /** Utility lookup if other classes need direct access. */
    public Transaction getTransaction(int transID) {
        return transactions.get(transID);
    }

    /** Utility lookup if other classes need direct access. */
    public Category getCategory(int categID) {
        return categories.get(categID);
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in yyyy-MM-dd format.", e);
        }
    }

    /**
     * Creates or reuses a fallback category for transactions created through the
     * diagram method that lacks category input.
     */
    private Category findOrCreateDefaultCategory(boolean isIncome) {
        String desiredName = isIncome ? "Auto Income" : "Auto Expense";
        Category.CategoryType desiredType = isIncome
                ? Category.CategoryType.INCOME
                : Category.CategoryType.EXPENSE;

        for (Category category : categories.values()) {
            boolean sameProfile = category.getProfileId() == activeProfileId;
            boolean sameName = category.getName().equalsIgnoreCase(desiredName);
            boolean compatibleType = isIncome ? category.isIncomeCategory() : category.isExpenseCategory();

            if (sameProfile && sameName && compatibleType) {
                return category;
            }
        }

        int newCategoryId = nextAvailableCategoryId();
        Category category = new Category(
                newCategoryId,
                desiredName,
                "Automatically created by Controller for transactions created without an explicit category.",
                desiredType,
                activeProfileId
        );

        categories.put(newCategoryId, category);
        categoryOperations.setCategory(category);
        categoryOperations.createCategoryDB(category.getCategoryId(), category.getName());
        return category;
    }

    private int nextAvailableCategoryId() {
        int nextId = 1;
        while (categories.containsKey(nextId)) {
            nextId++;
        }
        return nextId;
    }
}
