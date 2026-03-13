import java.time.LocalDate;

public class Transaction {
    public enum TransactionType {
        INCOME,
        EXPENSE;

        public static TransactionType fromString(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Type cannot be null.");
            }

            String normalized = value.trim().toUpperCase();
            switch (normalized) {
                case "INCOME":
                    return INCOME;
                case "EXPENSE":
                    return EXPENSE;
                default:
                    throw new IllegalArgumentException("Type must be 'income' or 'expense'.");
            }
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private int transactionId;
    private double amount;
    private TransactionType type;
    private int categoryId;
    private LocalDate date;
    private String note;
    private String receiptPath;
    private Integer transactionGroupId;
    private int profileId;

    public Transaction(int transactionId, double amount, String type, int categoryId,
                       LocalDate date, String note, String receiptPath,
                       Integer transactionGroupId, int profileId) {
        setTransactionId(transactionId);
        setAmount(amount);
        setType(type);
        setCategoryId(categoryId);
        setDate(date);
        setNote(note);
        setReceiptPath(receiptPath);
        setTransactionGroupId(transactionGroupId);
        setProfileId(profileId);
    }

    public Transaction(int transactionId, double amount, String type, Category category,
                       LocalDate date, String note, String receiptPath,
                       Integer transactionGroupId, int profileId) {
        setTransactionId(transactionId);
        setAmount(amount);
        setType(type);
        setDate(date);
        setNote(note);
        setReceiptPath(receiptPath);
        setTransactionGroupId(transactionGroupId);
        setProfileId(profileId);
        assignCategory(category);
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        if (transactionId <= 0) {
            throw new IllegalArgumentException("Transaction ID must be greater than 0.");
        }
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(String type) {
        this.type = TransactionType.fromString(type);
    }

    public void setType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        this.type = type;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Category ID must be greater than 0.");
        }
        this.categoryId = categoryId;
    }

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

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        if (note != null && note.trim().split("\\s+").length > 100) {
            throw new IllegalArgumentException("Note cannot exceed 100 words.");
        }
        this.note = note;
    }

    public String getReceiptPath() {
        return receiptPath;
    }

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

    public void setTransactionGroupId(Integer transactionGroupId) {
        if (transactionGroupId != null && transactionGroupId <= 0) {
            throw new IllegalArgumentException("Transaction Group ID must be greater than 0 when provided.");
        }
        this.transactionGroupId = transactionGroupId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        if (profileId <= 0) {
            throw new IllegalArgumentException("Profile ID must be greater than 0.");
        }
        this.profileId = profileId;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

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