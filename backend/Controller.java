import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Controller {

    private AccountOperations accOperations;
    private TransactionOperations transOperations;
    private CategoryOperations categoryOperations;
    private VerifyData verifyData;

    private Account activeAccount;

    private final List<Profile> profiles;
    private Profile activeProfile;

    private final Map<Integer, Account> accounts;
    private final Map<Integer, TransactionGroup> transactionGroups;
    private final Map<Integer, Transaction> transactions;
    private final Map<Integer, Category> categories;

    private final AtomicInteger profileSeq     = new AtomicInteger(1);
    private final AtomicInteger transactionSeq = new AtomicInteger(1);
    private final AtomicInteger groupSeq       = new AtomicInteger(1);

    private int activeProfileId;

    public Controller() { this(1); }

    public Controller(int activeProfileId) {
        this.accOperations      = new AccountOperations();
        this.transOperations    = new TransactionOperations();
        this.categoryOperations = new CategoryOperations();
        this.profiles           = new ArrayList<>();
        this.activeProfile      = null;
        this.accounts           = new HashMap<>();
        this.transactionGroups  = new HashMap<>();
        this.transactions       = new HashMap<>();
        this.categories         = new HashMap<>();
        this.activeAccount      = null;
        this.activeProfileId    = activeProfileId;
        this.verifyData         = new VerifyData(this);
    }

    public int getActiveProfileId() { return activeProfileId; }

    VerifyData getVerifyData() { return verifyData; }

    public boolean hasActiveTransactionGroup() {
        return transOperations.getTransactionGroup() != null;
    }

    public boolean hasActiveCategory() {
        return categoryOperations.getCategory() != null;
    }

    // =========================================================================
    //  ACCOUNT / SESSION OPERATIONS
    // =========================================================================

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
        account.setPassword(password);  // changed
        accounts.put(id, account);
        accOperations.setAccount(account);
        activeAccount = account;
        return true;
    }

    public boolean login(String email, String password) {
        for (Account acc : accounts.values()) {
            if (acc.getEmail().equalsIgnoreCase(email.trim())
                    && acc.checkPassword(password)) {  // changed
                activeAccount = acc;
                accOperations.setAccount(acc);
                return true;
            }
        }
        return false;
    }

    public void logout() {
        activeAccount   = null;
        activeProfile   = null;
        activeProfileId = 1;
        accOperations.setAccount(null);
    }

    public boolean isLoggedIn() { return activeAccount != null; }

    public Account getActiveAccount() { return activeAccount; }

    // =========================================================================
    //  PROFILE OPERATIONS
    // =========================================================================

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
        accOperations.setProfile(p);
        accOperations.create(activeAccount);
        if (activeProfile == null) {
            activeProfile   = p;
            activeProfileId = p.getID();
        }
    }

    public List<Profile> getProfiles() { return new ArrayList<>(profiles); }

    public boolean selectProfile(int id) {
        for (Profile p : profiles) {
            if (p.getID() == id) {
                activeProfile   = p;
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
            activeProfile   = profiles.isEmpty() ? null : profiles.get(0);
            activeProfileId = activeProfile != null ? activeProfile.getID() : 1;
        }
        accOperations.updateProfileDB(toDelete);  // fixed — was incorrectly deleting the account
        return true;
    }

    public Profile getActiveProfile() { return activeProfile; }

    public boolean hasActiveProfile() { return activeProfile != null; }

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
    //  CATEGORY OPERATIONS
    // =========================================================================

    public void addCategory(String name, String type, String desc) {
        int id = nextAvailableCategoryId();
        Category c = new Category(
                id, name,
                desc == null || desc.isBlank() ? null : desc,
                type, activeProfileId);
        categories.put(id, c);
        categoryOperations.setCategory(c);
        categoryOperations.create(c);
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
        categoryOperations.delete(c);
        return true;
    }

    // =========================================================================
    //  TRANSACTION OPERATIONS
    // =========================================================================

    public void addTransaction(int groupId, double amount, String type, int catId, LocalDate date, String note) {
        TransactionGroup group = transactionGroups.get(groupId);
        if (group == null)
            throw new IllegalArgumentException("Group not found with ID " + groupId + ".");
        int id = transactionSeq.getAndIncrement();
        Transaction t = new Transaction(
                id, amount, type, catId,
                date, note == null || note.isBlank() ? null : note,
                null, groupId, activeProfileId);
        group.addTransacToList(t);
        transactions.put(id, t);
        transOperations.setTransaction(t);
        transOperations.create(t);
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
        transOperations.delete(t);
        return true;
    }

    // =========================================================================
    //  TRANSACTION GROUP OPERATIONS
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
    //  REPORT OPERATIONS
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

    // =========================================================================
    //  DIAGRAM-COMPLIANT METHODS
    // =========================================================================

    public void createAccount(int accID, String email, int passHash) {
        if (accounts.containsKey(accID))
            throw new IllegalArgumentException("An account with ID " + accID + " already exists.");
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email cannot be blank.");
        Account account = new Account();
        account.setAccountID(accID);
        account.setEmail(email.trim());
        //account.setPasswordHash(passHash);
        accounts.put(accID, account);
        accOperations.setAccount(account);
    }

    public void deleteAccount(int accID) {
        Account removed = accounts.remove(accID);
        if (removed == null)
            throw new IllegalArgumentException("No account found with ID " + accID + ".");
        if (accOperations.getAccount() == removed)
            accOperations.setAccount(null);
    }

    public void createTransGroup(int groupID, String name, String description, String rcptPath) {
        if (transactionGroups.containsKey(groupID))
            throw new IllegalArgumentException("A transaction group with ID " + groupID + " already exists.");
        TransactionGroup group = new TransactionGroup(groupID, name, description, rcptPath);
        transactionGroups.put(groupID, group);
        transOperations.setTransactionGroup(group);
        transOperations.createTransactionGroupDB(group);
    }

    public void deleteTransGroup(int groupID) {
        TransactionGroup group = transactionGroups.remove(groupID);
        if (group == null)
            throw new IllegalArgumentException("No transaction group found with ID " + groupID + ".");
        Iterator<Map.Entry<Integer, Transaction>> iterator = transactions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Transaction> entry = iterator.next();
            Transaction transaction = entry.getValue();
            Integer transactionGroupId = transaction.getTransactionGroupId();
            if (transactionGroupId != null && transactionGroupId == groupID) {
                transOperations.delete(transaction);
                iterator.remove();
            }
        }
        transOperations.deleteTransactionGroupDB(groupID);
        if (transOperations.getTransactionGroup() == group)
            transOperations.setTransactionGroup(null);
    }

    public void createTransaction(int transID, String date, double amount, boolean type, int transGroup) {
        if (transactions.containsKey(transID))
            throw new IllegalArgumentException("A transaction with ID " + transID + " already exists.");
        TransactionGroup group = transactionGroups.get(transGroup);
        if (group == null)
            throw new IllegalArgumentException("No transaction group found with ID " + transGroup + ".");
        String transactionType = type ? "income" : "expense";
        Category category = findOrCreateDefaultCategory(type);
        LocalDate parsedDate = parseDate(date);
        Transaction transaction = new Transaction(
                transID, amount, transactionType, category.getCategoryId(),
                parsedDate, null, group.getReceiptFilePath(),
                group.getGroupId(), activeProfileId);
        group.addTransacToList(transaction);
        transactions.put(transID, transaction);
        transOperations.create(transaction);
    }

    public void deleteTransaction(int transID) {
        Transaction removed = transactions.remove(transID);
        if (removed == null)
            throw new IllegalArgumentException("No transaction found with ID " + transID + ".");
        Integer groupId = removed.getTransactionGroupId();
        if (groupId != null) {
            TransactionGroup group = transactionGroups.get(groupId);
            if (group != null) group.deleteTransacFromList(transID);
        }
        transOperations.delete(removed);
    }

    public void createCategory(int categID, String categName, String description) {
        if (categories.containsKey(categID))
            throw new IllegalArgumentException("A category with ID " + categID + " already exists.");
        Category category = new Category(categID, categName, description, Category.CategoryType.BOTH, activeProfileId);
        categories.put(categID, category);
        categoryOperations.setCategory(category);
        categoryOperations.create(category);
    }

    public void deleteCategory(int categID) {
        Category removed = categories.remove(categID);
        if (removed == null)
            throw new IllegalArgumentException("No category found with ID " + categID + ".");
        categoryOperations.delete(removed);
        if (categoryOperations.getCategory() == removed)
            categoryOperations.setCategory(null);
    }

    // =========================================================================
    //  UTILITY LOOKUPS
    // =========================================================================

    public Account getAccount(int accID) { return accounts.get(accID); }
    public TransactionGroup getTransactionGroup(int groupID) { return transactionGroups.get(groupID); }
    public Transaction getTransaction(int transID) { return transactions.get(transID); }
    public Category getCategory(int categID) { return categories.get(categID); }

    // =========================================================================
    //  PRIVATE HELPERS
    // =========================================================================

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in yyyy-MM-dd format.", e);
        }
    }

    private Category findOrCreateDefaultCategory(boolean isIncome) {
        String desiredName = isIncome ? "Auto Income" : "Auto Expense";
        Category.CategoryType desiredType = isIncome
                ? Category.CategoryType.INCOME
                : Category.CategoryType.EXPENSE;
        for (Category category : categories.values()) {
            boolean sameProfile    = category.getProfileId() == activeProfileId;
            boolean sameName       = category.getName().equalsIgnoreCase(desiredName);
            boolean compatibleType = isIncome ? category.isIncomeCategory() : category.isExpenseCategory();
            if (sameProfile && sameName && compatibleType) return category;
        }
        int newCategoryId = nextAvailableCategoryId();
        Category category = new Category(
                newCategoryId, desiredName,
                "Automatically created by Controller for transactions created without an explicit category.",
                desiredType, activeProfileId);
        categories.put(newCategoryId, category);
        categoryOperations.setCategory(category);
        categoryOperations.create(category);
        return category;
    }

    private int nextAvailableCategoryId() {
        int nextId = 1;
        while (categories.containsKey(nextId)) nextId++;
        return nextId;
    }
}
