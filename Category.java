public class Category {
    public enum CategoryType {
        INCOME,
        EXPENSE,
        BOTH;

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

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private int categoryId;
    private String name;
    private String description;
    private CategoryType type;
    private int profileId;

    public Category(int categoryId, String name, String description, String type, int profileId) {
        setCategoryId(categoryId);
        setName(name);
        setDescription(description);
        setType(type);
        setProfileId(profileId);
    }

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

    public void setCategoryId(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Category ID must be greater than 0.");
        }
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

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

    public void setType(String type) {
        this.type = CategoryType.fromString(type);
    }

    public void setType(CategoryType type) {
        if (type == null) {
            throw new IllegalArgumentException("Category type cannot be null.");
        }
        this.type = type;
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

    public boolean isIncomeCategory() {
        return type == CategoryType.INCOME || type == CategoryType.BOTH;
    }

    public boolean isExpenseCategory() {
        return type == CategoryType.EXPENSE || type == CategoryType.BOTH;
    }

    @Override
    public String toString() {
        return "Category ID: " + categoryId +
               "\nName: " + name +
               "\nDescription: " + description +
               "\nType: " + type +
               "\nProfile ID: " + profileId;
    }
}
