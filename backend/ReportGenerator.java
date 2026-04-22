import java.util.*;

public class ReportGenerator {

    // 1 to 3 profiles per ReportGenerator
    private List<Profile> profileList;

    public ReportGenerator() {
        profileList = new ArrayList<>();
    }

    public List<Profile> getProfileList() {
        return profileList;
    }

    // Enforces the 1-to-3 profile constraint
    public void addProfile(Profile profile) {
        if (profileList.size() >= 3) {
            throw new IllegalStateException("A ReportGenerator can have at most 3 profiles.");
        }
        profileList.add(profile);
    }

    public void setProfileList(List<Profile> profiles) {
        if (profiles == null || profiles.size() < 1 || profiles.size() > 3) {
            throw new IllegalArgumentException("profileList must contain between 1 and 3 profiles.");
        }
        profileList = profiles;
    }

    public void generateReport(String reportType) {
        switch (reportType.trim().toLowerCase()) {
            case "summary" -> {
                for (Profile profile : profileList) {
                    double totalIncome = 0, totalExpenses = 0;
                    for (TransactionGroup group : profile.getTransactionGroups()) {
                        for (Transaction t : group.getTransactionList()) {
                            if (t.getType() == Transaction.TransactionType.INCOME) {
                                totalIncome += t.getAmount();
                            } else {
                                totalExpenses += t.getAmount();
                            }
                        }
                    }
                    System.out.println("Profile: " + profile.getDisplayName());
                    System.out.println("  BankRoll:      $" + profile.getBankRoll());
                    System.out.println("  Total Income:  $" + totalIncome);
                    System.out.println("  Total Expenses:$" + totalExpenses);
                }
            }

            case "transactions" -> {
                for (Profile profile : profileList) {
                    System.out.println("Profile: " + profile.getDisplayName());
                    for (TransactionGroup group : profile.getTransactionGroups()) {
                        System.out.println("  Group: " + group.getName());
                        for (Transaction t : group.getTransactionList()) {
                            System.out.println("    [" + t.getDate() + "] "
                                    + t.getType() + " $" + t.getAmount()
                                    + " (Category ID: " + t.getCategoryId() + ")");
                        }
                    }
                }
            }

            case "balance" -> {
                for (Profile profile : profileList) {
                    double net = 0;
                    for (TransactionGroup group : profile.getTransactionGroups()) {
                        for (Transaction t : group.getTransactionList()) {
                            if (t.getType() == Transaction.TransactionType.INCOME) {
                                net += t.getAmount();
                            } else {
                                net -= t.getAmount();
                            }
                        }
                    }
                    System.out.println("Profile: " + profile.getDisplayName()
                            + " | Net Balance: $" + net);
                }
            }

            default -> throw new IllegalArgumentException("Unknown report type: " + reportType);
        }
    }
}
