public class Account{
    private int accountID;
    private String email;
    private int passwordHash;
    private List<Profile> profileList;
    public Account(){
       profileList = new ArrayList<>();   
    }
    public Profile getProfile(String name){
        int i = index(name);
        if(i!=-1){
            Profile p = profileList.get(i);
            return p;
        }
        return null;
    }
    public void addProfileToList(String name){
        int profileID = name.hashCode();
        Profile p = makeProfile(profileID,name);
        addProfileToList(p);
        
    }
    public Profile deleteProfileList(String name){
        int i = index(name);
        if(i!=-1){
            Profile p = profileList.remove(i);
            return p;
        }
        return null;
    }
    private void index(String name){
        int size = profileList.size();
        int id = name.hashCode();
        int right = size - 1;
        int left = 0;
        while(left<=right){
            int m = (l+r)/2
            Profile p = profileList.get(m);
            if(p.getProfileID()==id){
                return m;
            }else if(p.getProfileID()>m){
                r = m - 1;
            }else{
                l = m+1;
            }
        }
        return -1;
    }
    private void addProfileToList(Profile p){
        profileList.add(p);
        for(int i = 1;i<profileList.size();i++){
            Profile cur = profileList.get(i);
            int j = i-1;
            while(j>=0&&profileList.get(j).getProfileID()>cur.getProfileID()){
                profileList.set(profileList.get(j),j+1);
                j--;
            }
            profileList.set(cur,j+1);
        }
    }
    private Profile makeProfile(int id,String name){
        Profile p = new Profile();
        p.setID(id);
        p.setDisplayName(name);
        return p;
    }
    /**
    Update profile
    */
    public boolean updateProfile(String name,int changeProfile,String change){
        int pIndex = index(name);
        if(pIndex!=-1){
            Profile p = profileList.get(pIndex);
            if(changeProfile%2==0){
                updateProfileName(p,change);
            }else{
                updateProfileDescription(p,change);
            }
            return true;
        }
        return false;
    }

    private void updateProfileName(Profile p,String change){
        p.setDisplayName(change);
   
    private void updateProfileDescription(Profile p, String change){
            p.setDescription(change);
        }
    }
