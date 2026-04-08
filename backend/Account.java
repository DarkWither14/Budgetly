import java.util.ArrayList;
import java.util.List;

public class Account {
    private int accountID;
    private String email;
    private int passwordHash;
    private List<Profile> profileList;

    public Account() {
        profileList = new ArrayList<>();
    }

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

    // Insertion-sort to keep profileList sorted by profileID
    private void addProfileToList(Profile p) {
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

    private void updateProfileName(Profile p, String change) {
        p.setDisplayName(change);
    }

    private void updateProfileDescription(Profile p, String change) {
        p.setDescription(change);
    }
}
