import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class AddTransactionUnitTesting {

    private Controller controller;

    @BeforeEach
    void setup() {
        controller = new Controller(1);
    }

    // ------------------------------------------------------------
    // 1. GROUP NOT FOUND
    // ------------------------------------------------------------
    @Test
    void testCreateTransaction_GroupNotFound() {
        int fakeGroupId = 999;

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            controller.createTransaction(1, "2025-01-01", 10.0, true, fakeGroupId)
        );

        assertTrue(ex.getMessage().contains("No transaction group found"));
    }

    // ------------------------------------------------------------
    // 2. DUPLICATE TRANSACTION ID
    // ------------------------------------------------------------
    @Test
    void testCreateTransaction_DuplicateTransactionId() {
        // First create a valid group
        controller.createTransGroup(10, "JUnit Group", "desc", null);

        // First transaction succeeds
        controller.createTransaction(1, "2025-01-01", 10.0, true, 10);

        // Second transaction with same ID should fail
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            controller.createTransaction(1, "2025-01-01", 20.0, false, 10)
        );

        assertTrue(ex.getMessage().contains("already exists"));
    }

    // ------------------------------------------------------------
    // 3. SUCCESSFUL TRANSACTION
    // ------------------------------------------------------------
    @Test
    void testCreateTransaction_Success() {
        controller.createTransGroup(10, "JUnit Group", "desc", null);

        controller.createTransaction(1, "2025-01-01", 50.0, false, 10);

        var t = controller.getTransaction(1);

        assertNotNull(t);
        assertEquals(50.0, t.getAmount());
        assertEquals("expense", t.getType());
        assertEquals(10, t.getTransactionGroupId());
    }
}
