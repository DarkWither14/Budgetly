import java.util.*;
public class Profile{

  private int profileID;
  private String displayName;
  private List<TransactionGroup> transactionGroups;
  private Account assocAccount;
  private double bankRoll;
  public Profile(){

  }
  public int getID(){
    return profileID;
  }
  public String getDisplayName(){
    return displayName;
  }
  public double getBankRoll(){
    return bankroll;
  }
  public List<TransactionGroup> getTransactionGroups(){
    return transactionGroups;
  }
  public Account getAssocAccount(){
    return assocAccount;
  }
  public void setID(int id){
    profileID = id;
  }
  public void setDisplayName(String name){
    displayName = name;
  }
  public void setTransactionGroups(List<TransactionGroup> group){
    transactionGroups = group;
  }
  public void setAssocAccount(Account account){
    assocAccount = account;
  }
  public void setBankRoll(double amount){
    bankroll = amount;
  }
}
