import java.util.ArrayList;
import java.util.List;

public class Account {
    private int accountID;
    private String email;
    private int passwordHash;
    private final List<Profile> profileList;

    public Account() {
        profileList = new ArrayList<>();
    }

    public int getAccountID() { return accountID; }

    public void setAccountID(int accountID) {
        if (accountID <= 0) throw new IllegalArgumentException("Account ID must be greater than 0.");
        this.accountID = accountID;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email cannot be blank.");
        this.email = email.trim();
    }

    public int getPasswordHash() { return passwordHash; }

    public void setPasswordHash(int passwordHash) { this.passwordHash = passwordHash; }

    public List<Profile> getProfileList() { return profileList; }

    public Profile getProfile(String name) {
        int i = index(name);
        if (i != -1) {
            return profileList.get(i);
        }
        return null;
    }

    public void addProfileToList(String name) {
        int profileID = name.hashCode();
        Profile p = makeProfile(profileID, name);
        addProfileToList(p);
    }

    public Profile deleteProfileList(String name) {
        int i = index(name);
        if (i != -1) {
            return profileList.remove(i);
        }
        return null;
    }

    // Binary search by profile ID (list kept sorted by addProfileToList)
    private int index(String name) {
        int id = name.hashCode();
        int left = 0;
        int right = profileList.size() - 1;
        while (left <= right) {
            int m = (left + right) / 2;
            Profile p = profileList.get(m);
            if (p.getID() == id) {
                return m;
            } else if (p.getID() > id) {
                right = m - 1;
            } else {
                left = m + 1;
            }
        }
        return -1;
    }

    /**
     * Adds an existing Profile object into the sorted list.
     * Used by Controller to register a profile it already created.
     */
    public void addProfileToList(Profile p) {
        profileList.add(p);
        for (int i = 1; i < profileList.size(); i++) {
            Profile cur = profileList.get(i);
            int j = i - 1;
            while (j >= 0 && profileList.get(j).getID() > cur.getID()) {
                profileList.set(j + 1, profileList.get(j));
                j--;
            }
            profileList.set(j + 1, cur);
        }
    }

    private Profile makeProfile(int id, String name) {
        Profile p = new Profile();
        p.setID(id);
        p.setDisplayName(name);
        return p;
    }

    // changeProfile: 1 = name, 2 = description
    public void updateProfile(String name, int changeProfile, String change) {
        Profile p = getProfile(name);
        if (p == null) return;
        switch (changeProfile) {
            case 1 -> updateProfileName(p, change);
            case 2 -> updateProfileDescription(p, change);
        }
    }

    /**
     * Removes a profile from this account's list by its numeric ID.
     * Used by Controller when deleting a profile.
     *
     * @return the removed Profile, or null if not found
     */
    public Profile removeProfileById(int id) {
        for (int i = 0; i < profileList.size(); i++) {
            if (profileList.get(i).getID() == id) {
                return profileList.remove(i);
            }
        }
        return null;
    }

    private void updateProfileName(Profile p, String change) {
        p.setDisplayName(change);
    }

    private void updateProfileDescription(Profile p, String change) {
        p.setDescription(change);
    }
}
