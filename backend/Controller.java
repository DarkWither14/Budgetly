import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    private final Map<Integer, Account> accounts;
    private final Map<Integer, TransactionGroup> transactionGroups;
    private final Map<Integer, Transaction> transactions;
    private final Map<Integer, Category> categories;

   
    private int activeProfileId;

    public Controller() {
        this(1);
    }

    public Controller(int activeProfileId) {
        this.accOperations = new AccountOperations();
        this.transOperations = new TransactionOperations();
        this.categoryOperations = new CategoryOperations();
        this.accounts = new HashMap<>();
        this.transactionGroups = new HashMap<>();
        this.transactions = new HashMap<>();
        this.categories = new HashMap<>();
        this.activeProfileId = activeProfileId;
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
        setAccountField(account, "accountID", accID);
        setAccountField(account, "email", email.trim());
        setAccountField(account, "passwordHash", passHash);

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

    private void setAccountField(Account account, String fieldName, Object value) {
        try {
            Field field = Account.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(account, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to set Account field '" + fieldName + "'.", e);
        }
    }
}
