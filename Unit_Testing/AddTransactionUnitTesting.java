package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Tested by Michael Wagner
 */
public class AddTransactionUnitTesting {

    private Controller controller;

    @Before
    public void setup() {
        controller = new Controller(1);
    }

    // ------------------------------------------------------------
    // 1. GROUP NOT FOUND
    // ------------------------------------------------------------
    @Test
    public void testCreateTransaction_GroupNotFound() {
        int fakeGroupId = 999;

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            controller.createTransaction(1, "2025-01-01", 10.0, true, fakeGroupId)
        );

        if(ex.getMessage().contains("No transaction group found")) {
        	System.out.println("A transaction group was not found. Test passed!");
        }else {
        	System.out.println("A Transaction group was found. Test failed!");
        }
        assertTrue(ex.getMessage().contains("No transaction group found"));
    }

    // ------------------------------------------------------------
    // 2. DUPLICATE TRANSACTION ID
    // ------------------------------------------------------------
    @Test
    public void testCreateTransaction_DuplicateTransactionId() {
        controller.createTransGroup(10, "JUnit Group", "desc", null);

        controller.createTransaction(1, "2025-01-01", 10.0, true, 10);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            controller.createTransaction(1, "2025-01-01", 20.0, false, 10)
        );
        if(ex.getMessage().contains("already exists")) {
        	System.out.println("Transaction already exists. Test passed!");
        }else {
        	System.out.println("Transaction does not exists. Test failed!");
        }
        assertTrue(ex.getMessage().contains("already exists"));
    }

    // ------------------------------------------------------------
    // 3. SUCCESSFUL TRANSACTION
    // ------------------------------------------------------------
    @Test
    public void testCreateTransaction_Success() {
        controller.createTransGroup(10, "JUnit Group", "desc", null);

        controller.createTransaction(1, "2025-01-01", 50.0, false, 10);

        var t = controller.getTransaction(1);

        assertNotNull(t);
        assertEquals(50.0, t.getAmount(), 0.0001);
        assertEquals("expense", t.getType());
        assertEquals(Integer.valueOf(10), t.getTransactionGroupId());
        
    }
    @After
    public void teardown() {
        controller = null;
    }

}
