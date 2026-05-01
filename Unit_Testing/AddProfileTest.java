import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class AddProfileTest {

    private Controller controller;

    @BeforeEach
    public void setUp() {
        controller = new Controller();
    }

    // =========================================================================
    //  Test Case 1 – Path 1
    //  desc blank, activeAccount null, activeProfile null (first profile)
    // =========================================================================
    @Test
    public void testCreateProfile_BlankDesc_NoAccount_NoActiveProfile() {
        controller.createProfile("Personal", "");

        List<Profile> profiles = controller.getProfiles();
        assertEquals(1, profiles.size());
        assertEquals("Personal", profiles.get(0).getDisplayName());
        assertNull(profiles.get(0).getDescription());

        // activeProfile should be set since it was null before
        assertNotNull(controller.getActiveProfile());
        assertEquals("Personal", controller.getActiveProfile().getDisplayName());
    }

    // =========================================================================
    //  Test Case 2 – Path 2
    //  desc blank, activeAccount exists, activeProfile already set
    // =========================================================================
    @Test
    public void testCreateProfile_BlankDesc_AccountExists_ActiveProfileExists() {
        // Register account and create first profile to set activeProfile
        controller.registerAccount("test@test.com", "password");
        controller.createProfile("First", "first profile");

        Profile originalActiveProfile = controller.getActiveProfile();

        // Now create second profile with no description
        controller.createProfile("Work", "");

        List<Profile> profiles = controller.getProfiles();
        assertEquals(2, profiles.size());
        assertNull(profiles.get(1).getDescription());

        // activeProfile should remain unchanged
        assertEquals(originalActiveProfile.getDisplayName(),
                controller.getActiveProfile().getDisplayName());
    }

    // =========================================================================
    //  Test Case 3 – Path 3
    //  desc provided, activeAccount exists, activeProfile null (first profile)
    // =========================================================================
    @Test
    public void testCreateProfile_DescProvided_AccountExists_NoActiveProfile() {
        // Register account so activeAccount is set
        controller.registerAccount("test3@test.com", "password");

        controller.createProfile("Vacation", "Hawaii trip savings");

        List<Profile> profiles = controller.getProfiles();
        assertEquals(1, profiles.size());
        assertEquals("Vacation", profiles.get(0).getDisplayName());
        assertEquals("Hawaii trip savings", profiles.get(0).getDescription());

        // activeProfile should be set since it was null before
        assertNotNull(controller.getActiveProfile());
        assertEquals("Vacation", controller.getActiveProfile().getDisplayName());
    }

    // =========================================================================
    //  Test Case 4 – Path 4
    //  desc provided, activeAccount null, activeProfile already set
    // =========================================================================
    @Test
    public void testCreateProfile_DescProvided_NoAccount_ActiveProfileExists() {
        // Create first profile without account to set activeProfile
        controller.createProfile("First", "first profile");

        Profile originalActiveProfile = controller.getActiveProfile();

        // Create second profile with description, no account
        controller.createProfile("Business", "Business expenses");

        List<Profile> profiles = controller.getProfiles();
        assertEquals(2, profiles.size());
        assertEquals("Business expenses", profiles.get(1).getDescription());

        // activeProfile should remain unchanged
        assertEquals(originalActiveProfile.getDisplayName(),
                controller.getActiveProfile().getDisplayName());
    }
}
