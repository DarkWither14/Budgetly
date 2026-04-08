import java.util.ArrayList;
import java.util.List;

public class Profile {

    private int profileID;
    private String displayName;
    private String description;
    private List<TransactionGroup> transactionGroups;
    private Account assocAccount;
    private double bankRoll;

    public Profile() {
        transactionGroups = new ArrayList<>();
    }

    public int getID() {
        return profileID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public double getBankRoll() {
        return bankRoll;
    }

    public List<TransactionGroup> getTransactionGroups() {
        return transactionGroups;
    }

    public Account getAssocAccount() {
        return assocAccount;
    }

    public void setID(int id) {
        profileID = id;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTransactionGroups(List<TransactionGroup> group) {
        transactionGroups = group;
    }

    public void setAssocAccount(Account account) {
        assocAccount = account;
    }

    public void setBankRoll(double amount) {
        bankRoll = amount;
    }
}
