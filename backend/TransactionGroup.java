import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a group of related financial transactions belonging to a user profile.
 * A TransactionGroup can be used to organize transactions for trips, projects,
 * events, or any logical grouping the user chooses.
 *
 * <p>According to the system specification, deleting a TransactionGroup
 * also deletes all Transactions inside it, making this a composition relationship.</p>
 */
public class TransactionGroup {

    /** Unique identifier for the transaction group. */
    private int groupId;

    /** Name of the group (e.g., "Hawaii Trip", "Car Repairs"). */
    private String name;

    /** Optional description providing additional details about the group. */
    private String description;

    /** List of transactions belonging to this group. */
    private final List<Transaction> transactionList;

    /** List of categories associated with this group (optional). */
    private final List<Category> categoryList;

    /** Optional file path to a receipt or document associated with the group. */
    private String receiptFilePath;

    /**
     * Creates a new TransactionGroup with the required fields.
     *
     * @param groupId the unique ID of the group; must be greater than 0
     * @param name the name of the group; cannot be null or blank
     * @param description optional description; may be null or blank
     * @param receiptFilePath optional file path; may be null but not blank
     */
    public TransactionGroup(int groupId, String name, String description, String receiptFilePath) {
        // groupId validation
        if (groupId <= 0) {
            throw new IllegalArgumentException("Group ID must be greater than 0.");
        }
        this.groupId = groupId;

        // name validation
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }
        this.name = name.trim();

        // description normalization
        if (description == null) {
            this.description = null;
        } else {
            String normalized = description.trim();
            this.description = normalized.isEmpty() ? null : normalized;
        }

        // receiptFilePath validation
        if (receiptFilePath == null) {
            this.receiptFilePath = null;
        } else {
            String normalized = receiptFilePath.trim();
            if (normalized.isEmpty()) {
                throw new IllegalArgumentException("Receipt file path cannot be blank.");
            }
            this.receiptFilePath = normalized;
        }

        this.transactionList = new ArrayList<>();
        this.categoryList = new ArrayList<>();
    }

    /** @return the unique ID of this transaction group */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Sets the group ID.
     *
     * @param groupId must be greater than 0
     * @throws IllegalArgumentException if groupId is not positive
     */
    public void setGroupId(int groupId) {
        if (groupId <= 0) {
            throw new IllegalArgumentException("Group ID must be greater than 0.");
        }
        this.groupId = groupId;
    }

    /** @return the name of this transaction group */
    public String getName() {
        return name;
    }

    /**
     * Sets the group name.
     *
     * @param name cannot be null or blank
     * @throws IllegalArgumentException if name is null or blank
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }
        this.name = name.trim();
    }

    /** @return the description of this group, or null if none */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the group description.
     *
     * @param description may be null; blank values are stored as null
     */
    public void setDescription(String description) {
        if (description == null) {
            this.description = null;
            return;
        }

        String normalized = description.trim();
        this.description = normalized.isEmpty() ? null : normalized;
    }

    /** @return the optional receipt file path */
    public String getReceiptFilePath() {
        return receiptFilePath;
    }

    /**
     * Sets the receipt file path.
     *
     * @param receiptFilePath may be null; blank values are rejected
     * @throws IllegalArgumentException if non-null but blank
     */
    public void setReceiptFilePath(String receiptFilePath) {
        if (receiptFilePath == null) {
            this.receiptFilePath = null;
            return;
        }

        String normalized = receiptFilePath.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Receipt file path cannot be blank.");
        }

        this.receiptFilePath = normalized;
    }

    /** @return the list of transactions in this group */
    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    /** @return the list of categories associated with this group */
    public List<Category> getCategoryList() {
        return categoryList;
    }

    /**
     * Adds a transaction to this group.
     * The transaction's transactionGroupId is updated to match this group.
     *
     * @param trans the transaction to add; cannot be null
     * @throws IllegalArgumentException if trans is null
     */
    public void addTransacToList(Transaction trans) {
        if (trans == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }

        trans.setTransactionGroupId(groupId);
        transactionList.add(trans);
    }

    /**
     * Removes a transaction from the group by its ID.
     *
     * @param id the transaction ID to remove
     * @return the removed Transaction, or null if not found
     */
    public Transaction deleteTransacFromList(int id) {
        Iterator<Transaction> iterator = transactionList.iterator();

        while (iterator.hasNext()) {
            Transaction t = iterator.next();
            if (t.getTransactionId() == id) {
                iterator.remove();
                return t;
            }
        }

        return null;
    }

    /**
     * Updates an existing transaction in the group.
     * Matches by transaction ID.
     *
     * @param trans the updated transaction; cannot be null
     * @throws IllegalArgumentException if trans is null
     */
    public void updateTransacInList(Transaction trans) {
        if (trans == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }

        for (int i = 0; i < transactionList.size(); i++) {
            if (transactionList.get(i).getTransactionId() == trans.getTransactionId()) {
                transactionList.set(i, trans);
                return;
            }
        }
    }

    /**
     * Returns a formatted string with all group details.
     *
     * @return formatted group information
     */
    @Override
    public String toString() {
        return "Transaction Group ID: " + groupId +
               "\nName: " + name +
               "\nDescription: " + description +
               "\nReceipt File Path: " + receiptFilePath +
               "\nTransactions: " + transactionList.size() +
               "\nCategories: " + categoryList.size();
    }
}
