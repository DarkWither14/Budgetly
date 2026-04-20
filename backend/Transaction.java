import java.time.LocalDate;

// Represents a single financial transaction belonging to a user profile
public class Transaction {

    // Enum defining whether a transaction is income or an expense
    public enum TransactionType {
        INCOME,
        EXPENSE;

        // Converts a string value to the matching TransactionType enum constant
        public static TransactionType fromString(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Type cannot be null.");
            }

            String normalized = value.trim().toUpperCase();
            switch (normalized) {
                case "INCOME" -> {
                    return INCOME;
                }
                case "EXPENSE" -> {
                    return EXPENSE;
                }
                default -> throw new IllegalArgumentException("Type must be 'income' or 'expense'.");
            }
        }

        // Returns the enum name in lowercase
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    // Fields
    private int transactionId;
    private double amount;
    private TransactionType type;
    private int categoryId;
    private LocalDate date;
    private String note;           // Optional note
    private Category transactionCategory; // Optional reference to the Category object; can be null if only categoryId is used
    private String receiptPath;    // Optional file path to a receipt
    private Integer transactionGroupId; // Nullable; links to a recurring group if set
    private int profileId;

    // Constructor accepting category as an ID
    public Transaction(int transactionId, double amount, String type, int categoryId,
                       LocalDate date, String note, String receiptPath,
                       Integer transactionGroupId, int profileId) {
        if (transactionId <= 0) {
            throw new IllegalArgumentException("Transaction ID must be greater than 0.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = TransactionType.fromString(type);
        this.categoryId = categoryId;
        this.date = date;
        this.note = note;
        this.receiptPath = receiptPath;
        this.transactionGroupId = transactionGroupId;
        this.profileId = profileId;
    }

    // Constructor accepting a Category object; validates and assigns categoryId
    public Transaction(int transactionId, double amount, String type, Category category,
                       LocalDate date, String note, String receiptPath,
                       Integer transactionGroupId, int profileId) {
        if (transactionId <= 0) {
            throw new IllegalArgumentException("Transaction ID must be greater than 0.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = TransactionType.fromString(type);
        this.date = date;
        this.note = note;
        this.receiptPath = receiptPath;
        this.transactionGroupId = transactionGroupId;
        this.profileId = profileId;
        // Inline assignCategory logic
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        if (category.getProfileId() != profileId) {
            throw new IllegalArgumentException("Category profile must match transaction profile.");
        }
        boolean validForType = (isIncome() && category.isIncomeCategory()) ||
                               (isExpense() && category.isExpenseCategory());
        if (!validForType) {
            throw new IllegalArgumentException("Category type does not match transaction type.");
        }
        this.categoryId = category.getCategoryId();
        this.transactionCategory = category;
    }

    public int getTransactionId() {
        return transactionId;
    }

    // Validates that the ID is positive before setting
    public void setTransactionId(int transactionId) {
        if (transactionId <= 0) {
            throw new IllegalArgumentException("Transaction ID must be greater than 0.");
        }
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    // Validates that the amount is positive before setting
    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    // Parses a string and sets the transaction type
    public void setType(String type) {
        this.type = TransactionType.fromString(type);
    }

    // Sets the transaction type directly from an enum value
    public void setType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        this.type = type;
    }

    public int getCategoryId() {
        return categoryId;
    }

    // Validates that the category ID is positive before setting
    public void setCategoryId(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Category ID must be greater than 0.");
        }
        this.categoryId = categoryId;
    }

    // Validates that the category belongs to the same profile and matches the transaction type
    public void assignCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        if (category.getProfileId() != profileId) {
            throw new IllegalArgumentException("Category profile must match transaction profile.");
        }

        boolean validForType = (isIncome() && category.isIncomeCategory()) ||
                               (isExpense() && category.isExpenseCategory());

        if (!validForType) {
            throw new IllegalArgumentException("Category type does not match transaction type.");
        }

        this.categoryId = category.getCategoryId();
    }

    public LocalDate getDate() {
        return date;
    }

    // Validates that the date is not null before setting
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    // Allows null notes; enforces a 100-word limit if a note is provided
    public void setNote(String note) {
        if (note != null && note.trim().split("\\s+").length > 100) {
            throw new IllegalArgumentException("Note cannot exceed 100 words.");
        }
        this.note = note;
    }

    public String getReceiptPath() {
        return receiptPath;
    }

    // Stores null if no path provided; rejects blank (whitespace-only) strings
    public void setReceiptPath(String receiptPath) {
        if (receiptPath == null) {
            this.receiptPath = null;
            return;
        }

        String normalized = receiptPath.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Receipt path cannot be blank.");
        }

        this.receiptPath = normalized;
    }

    public Integer getTransactionGroupId() {
        return transactionGroupId;
    }

    // Allows null (no group); validates the ID is positive when provided
    public void setTransactionGroupId(Integer transactionGroupId) {
        if (transactionGroupId != null && transactionGroupId <= 0) {
            throw new IllegalArgumentException("Transaction Group ID must be greater than 0 when provided.");
        }
        this.transactionGroupId = transactionGroupId;
    }

    public int getProfileId() {
        return profileId;
    }

    // Validates that the profile ID is positive before setting
    public void setProfileId(int profileId) {
        if (profileId <= 0) {
            throw new IllegalArgumentException("Profile ID must be greater than 0.");
        }
        this.profileId = profileId;
    }

    // Returns true if this transaction is income
    public final boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    // Returns true if this transaction is an expense
    public final boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    // Returns a formatted string with all transaction details
    @Override
    public String toString() {
        return "Transaction ID: " + transactionId +
               "\nAmount: " + amount +
               "\nType: " + type +
               "\nCategory ID: " + categoryId +
               "\nDate: " + date +
               "\nNote: " + note +
               "\nReceipt Path: " + receiptPath +
               "\nTransaction Group ID: " + transactionGroupId +
               "\nProfile ID: " + profileId;
    }
}