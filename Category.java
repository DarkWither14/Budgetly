// Represents a budget category belonging to a user profile
public class Category {

    // Enum defining whether a category applies to income, expenses, or both
    public enum CategoryType {
        INCOME,
        EXPENSE,
        BOTH;

        // Converts a string value to the matching CategoryType enum constant
        public static CategoryType fromString(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Category type cannot be null.");
            }

            String normalized = value.trim().toUpperCase();
            switch (normalized) {
                case "INCOME":
                    return INCOME;
                case "EXPENSE":
                    return EXPENSE;
                case "BOTH":
                    return BOTH;
                default:
                    throw new IllegalArgumentException("Category type must be 'income', 'expense', or 'both'.");
            }
        }

        // Returns the enum name in lowercase
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    // Fields
    private int categoryId;
    private String name;
    private String description;
    private CategoryType type;
    private int profileId; // ID of the profile this category belongs to

    // Constructor accepting type as a String
    public Category(int categoryId, String name, String description, String type, int profileId) {
        setCategoryId(categoryId);
        setName(name);
        setDescription(description);
        setType(type);
        setProfileId(profileId);
    }

    // Constructor accepting type as a CategoryType enum
    public Category(int categoryId, String name, String description, CategoryType type, int profileId) {
        setCategoryId(categoryId);
        setName(name);
        setDescription(description);
        setType(type);
        setProfileId(profileId);
    }

    public int getCategoryId() {
        return categoryId;
    }

    // Validates that the ID is positive before setting
    public void setCategoryId(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Category ID must be greater than 0.");
        }
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    // Validates and trims the name before setting
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    // Stores null if description is null or blank, otherwise trims it
    public void setDescription(String description) {
        if (description == null) {
            this.description = null;
            return;
        }

        String normalized = description.trim();
        this.description = normalized.isEmpty() ? null : normalized;
    }

    public CategoryType getType() {
        return type;
    }

    // Parses a string and sets the category type
    public void setType(String type) {
        this.type = CategoryType.fromString(type);
    }

    // Sets the category type directly from an enum value
    public void setType(CategoryType type) {
        if (type == null) {
            throw new IllegalArgumentException("Category type cannot be null.");
        }
        this.type = type;
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

    // Returns true if this category can be used for income
    public boolean isIncomeCategory() {
        return type == CategoryType.INCOME || type == CategoryType.BOTH;
    }

    // Returns true if this category can be used for expenses
    public boolean isExpenseCategory() {
        return type == CategoryType.EXPENSE || type == CategoryType.BOTH;
    }

    // Returns a formatted string with all category details
    @Override
    public String toString() {
        return "Category ID: " + categoryId +
               "\nName: " + name +
               "\nDescription: " + description +
               "\nType: " + type +
               "\nProfile ID: " + profileId;
    }
}
