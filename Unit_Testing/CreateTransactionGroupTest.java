import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CreateTransactionGroupTest {
	public static Controller controller;

	@BeforeClass
	public static void initialize() {
		controller = new Controller();
		// User cannot cause addGroup() to be called without a Profile existing and selected
		controller.createProfile("Person1", null);
	}

	// Path A
	@Test
	public void testNullDesc() {
		controller.addGroup("Default", null);
		assertNull(controller.getActiveProfile().getTransactionGroups().getLast().getDescription());
	}

	// Path B
	@Test
	public void testNotNullDesc() {
		controller.addGroup("Default", "Main group");
		assertEquals("Main group", controller.getActiveProfile().getTransactionGroups().getLast().getDescription());
	}
}