import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

public class ChartPrinterTest {

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(out));
            action.run();
        } finally {
            System.setOut(originalOut);
        }

        return out.toString();
    }

    @Test
    public void testSingleExpenseCategory() {
        Category food = new Category(1, "Food", "Food spending", "expense", 1);

        Transaction t1 = new Transaction(
                1,
                25.00,
                "expense",
                1,
                LocalDate.of(2026, 5, 1),
                "Lunch",
                null,
                1,
                1
        );

        String output = captureOutput(() ->
                ChartPrinter.printCategoryChart(
                        List.of(t1),
                        List.of(food),
                        "expense"
                )
        );

        assertTrue(output.contains("Expenses by Category"));
        assertTrue(output.contains("Food"));
        assertTrue(output.contains("$25.00"));
        assertTrue(output.contains("Total: $25.00"));
    }

    @Test
    public void testMultipleTransactionsSameCategoryAreSummed() {
        Category food = new Category(1, "Food", "Food spending", "expense", 1);

        Transaction t1 = new Transaction(
                1, 25.00, "expense", 1,
                LocalDate.of(2026, 5, 1),
                "Lunch", null, 1, 1
        );

        Transaction t2 = new Transaction(
                2, 15.00, "expense", 1,
                LocalDate.of(2026, 5, 2),
                "Snack", null, 1, 1
        );

        String output = captureOutput(() ->
                ChartPrinter.printCategoryChart(
                        List.of(t1, t2),
                        List.of(food),
                        "expense"
                )
        );

        assertTrue(output.contains("Food"));
        assertTrue(output.contains("$40.00"));
        assertTrue(output.contains("Total: $40.00"));
    }

    @Test
    public void testMultipleExpenseCategories() {
        Category food = new Category(1, "Food", "Food spending", "expense", 1);
        Category rent = new Category(2, "Rent", "Monthly rent", "expense", 1);

        Transaction t1 = new Transaction(
                1, 25.00, "expense", 1,
                LocalDate.of(2026, 5, 1),
                "Lunch", null, 1, 1
        );

        Transaction t2 = new Transaction(
                2, 800.00, "expense", 2,
                LocalDate.of(2026, 5, 1),
                "May rent", null, 1, 1
        );

        String output = captureOutput(() ->
                ChartPrinter.printCategoryChart(
                        List.of(t1, t2),
                        List.of(food, rent),
                        "expense"
                )
        );

        assertTrue(output.contains("Food"));
        assertTrue(output.contains("$25.00"));
        assertTrue(output.contains("Rent"));
        assertTrue(output.contains("$800.00"));
        assertTrue(output.contains("Total: $825.00"));
    }

    @Test
    public void testIncomeIsIgnoredForExpenseChart() {
        Category food = new Category(1, "Food", "Food spending", "expense", 1);
        Category paycheck = new Category(2, "Paycheck", "Job income", "income", 1);

        Transaction expense = new Transaction(
                1, 25.00, "expense", 1,
                LocalDate.of(2026, 5, 1),
                "Lunch", null, 1, 1
        );

        Transaction income = new Transaction(
                2, 1000.00, "income", 2,
                LocalDate.of(2026, 5, 1),
                "Work", null, 1, 1
        );

        String output = captureOutput(() ->
                ChartPrinter.printCategoryChart(
                        List.of(expense, income),
                        List.of(food, paycheck),
                        "expense"
                )
        );

        assertTrue(output.contains("Food"));
        assertTrue(output.contains("$25.00"));
        assertFalse(output.contains("Paycheck"));
        assertFalse(output.contains("$1000.00"));
        assertTrue(output.contains("Total: $25.00"));
    }

    @Test
    public void testNoExpenseTransactionsDisplaysNoData() {
        Category paycheck = new Category(1, "Paycheck", "Job income", "income", 1);

        Transaction income = new Transaction(
                1, 1000.00, "income", 1,
                LocalDate.of(2026, 5, 1),
                "Work", null, 1, 1
        );

        String output = captureOutput(() ->
                ChartPrinter.printCategoryChart(
                        List.of(income),
                        List.of(paycheck),
                        "expense"
                )
        );

        assertTrue(output.contains("No data to chart."));
    }

    @Test
    public void testMissingCategoryUsesFallbackLabel() {
        Transaction unknownCategoryTransaction = new Transaction(
                1, 50.00, "expense", 99,
                LocalDate.of(2026, 5, 1),
                "Unknown category", null, 1, 1
        );

        String output = captureOutput(() ->
                ChartPrinter.printCategoryChart(
                        List.of(unknownCategoryTransaction),
                        List.of(),
                        "expense"
                )
        );

        assertTrue(output.contains("Cat #99"));
        assertTrue(output.contains("$50.00"));
        assertTrue(output.contains("Total: $50.00"));
    }

    @Test
    public void testBothModeIncludesIncomeAndExpense() {
        Category food = new Category(1, "Food", "Food spending", "expense", 1);
        Category paycheck = new Category(2, "Paycheck", "Job income", "income", 1);

        Transaction expense = new Transaction(
                1, 25.00, "expense", 1,
                LocalDate.of(2026, 5, 1),
                "Lunch", null, 1, 1
        );

        Transaction income = new Transaction(
                2, 1000.00, "income", 2,
                LocalDate.of(2026, 5, 1),
                "Work", null, 1, 1
        );

        String output = captureOutput(() ->
                ChartPrinter.printCategoryChart(
                        List.of(expense, income),
                        List.of(food, paycheck),
                        "both"
                )
        );

        assertTrue(output.contains("Spending by Category"));
        assertTrue(output.contains("Food"));
        assertTrue(output.contains("Paycheck"));
        assertTrue(output.contains("Total: $1025.00"));
    }
}