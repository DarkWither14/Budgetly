import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Prints ASCII bar charts directly to the terminal.
 *
 * Charts available:
 *   - printCategoryChart  : spending/income broken down by category
 *   - printMonthlyTrend   : total expenses per month (chronological)
 *   - printIncomeVsExpense: side-by-side income vs expenses per month
 */
public class ChartPrinter {

    private static final int MAX_BAR_WIDTH = 30; // max number of block characters
    private static final char BAR_CHAR     = '█';
    private static final char HALF_CHAR    = '▌'; // used for fractional bar rounding

    // ─────────────────────────────────────────────────────────────────────────
    //  PUBLIC CHART METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Bar chart of totals per category for the active profile's transactions.
     *
     * @param txList    all transactions for the profile
     * @param catList   all categories for the profile
     * @param type      "expense", "income", or "both"
     */
    public static void printCategoryChart(List<Transaction> txList,
                                          List<Category> catList,
                                          String type) {
        // Sum amounts per category ID
        Map<Integer, Double> totals = new LinkedHashMap<>();
        for (Transaction t : txList) {
            boolean include = switch (type.toLowerCase()) {
                case "income"  -> t.isIncome();
                case "expense" -> t.isExpense();
                default        -> true;
            };
            if (include) {
                totals.merge(t.getCategoryId(), t.getAmount(), Double::sum);
            }
        }

        if (totals.isEmpty()) {
            System.out.println("  No data to chart.");
            return;
        }

        // Build label → value pairs
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : totals.entrySet()) {
            String label = catList.stream()
                    .filter(c -> c.getCategoryId() == entry.getKey())
                    .map(Category::getName)
                    .findFirst()
                    .orElse("Cat #" + entry.getKey());
            labels.add(label);
            values.add(entry.getValue());
        }

        String title = switch (type.toLowerCase()) {
            case "income"  -> "Income by Category";
            case "expense" -> "Expenses by Category";
            default        -> "Spending by Category";
        };
        printBarChart(title, labels, values, "$");
    }

    /**
     * Bar chart of total expenses per calendar month, oldest → newest.
     *
     * @param txList transactions for the profile
     */
    public static void printMonthlyTrend(List<Transaction> txList) {
        // Group expense totals by "YYYY-MM"
        Map<String, Double> monthly = new TreeMap<>(); // TreeMap = sorted by key
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        for (Transaction t : txList) {
            if (t.isExpense()) {
                String key = t.getDate().format(fmt);
                monthly.merge(key, t.getAmount(), Double::sum);
            }
        }

        if (monthly.isEmpty()) {
            System.out.println("  No expense data to chart.");
            return;
        }

        List<String> labels = new ArrayList<>(monthly.keySet());
        List<Double> values = new ArrayList<>(monthly.values());
        printBarChart("Monthly Expense Trend", labels, values, "$");
    }

    /**
     * Side-by-side income vs expense bars per calendar month.
     *
     * @param txList transactions for the profile
     */
    public static void printIncomeVsExpense(List<Transaction> txList) {
        Map<String, Double> incomeMap  = new TreeMap<>();
        Map<String, Double> expenseMap = new TreeMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Transaction t : txList) {
            String key = t.getDate().format(fmt);
            if (t.isIncome()) {
                incomeMap.merge(key, t.getAmount(), Double::sum);
            } else {
                expenseMap.merge(key, t.getAmount(), Double::sum);
            }
        }

        // Collect all months present in either map
        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(incomeMap.keySet());
        allMonths.addAll(expenseMap.keySet());

        if (allMonths.isEmpty()) {
            System.out.println("  No data to chart.");
            return;
        }

        double max = allMonths.stream()
                .mapToDouble(m -> Math.max(
                        incomeMap.getOrDefault(m, 0.0),
                        expenseMap.getOrDefault(m, 0.0)))
                .max().orElse(1.0);

        int labelWidth = allMonths.stream().mapToInt(String::length).max().orElse(7);
        String sep = "  " + "─".repeat(labelWidth + MAX_BAR_WIDTH + 16);

        System.out.println();
        System.out.println("  Income vs Expenses by Month");
        System.out.println(sep);
        System.out.printf("  %-" + labelWidth + "s  %-6s  %s%n", "Month", "Type", "Amount");
        System.out.println(sep);

        for (String month : allMonths) {
            double inc = incomeMap.getOrDefault(month, 0.0);
            double exp = expenseMap.getOrDefault(month, 0.0);

            System.out.printf("  %-" + labelWidth + "s  INC    %s  $%.2f%n",
                    month, buildBar(inc, max), inc);
            System.out.printf("  %-" + labelWidth + "s  EXP    %s  $%.2f%n",
                    "",    buildBar(exp, max), exp);
            System.out.println();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GENERIC BAR CHART
    // ─────────────────────────────────────────────────────────────────────────

    private static void printBarChart(String title,
                                      List<String> labels,
                                      List<Double> values,
                                      String unit) {
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        int labelWidth = labels.stream().mapToInt(String::length).max().orElse(5);
        String sep = "  " + "─".repeat(labelWidth + MAX_BAR_WIDTH + 14);

        System.out.println();
        System.out.println("  " + title);
        System.out.println(sep);

        for (int i = 0; i < labels.size(); i++) {
            String bar   = buildBar(values.get(i), max);
            double value = values.get(i);
            System.out.printf("  %-" + labelWidth + "s  %s  %s%.2f%n",
                    labels.get(i), bar, unit, value);
        }

        System.out.println(sep);

        // Totals footer
        double total = values.stream().mapToDouble(Double::doubleValue).sum();
        System.out.printf("  %-" + labelWidth + "s  Total: %s%.2f%n", "", unit, total);
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  BAR BUILDER
    // ─────────────────────────────────────────────────────────────────────────

    /** Builds a bar string scaled to MAX_BAR_WIDTH for the given value/max. */
    private static String buildBar(double value, double max) {
        if (max <= 0) return "";
        double ratio     = value / max;
        double rawBlocks = ratio * MAX_BAR_WIDTH;
        int fullBlocks   = (int) rawBlocks;
        boolean half     = (rawBlocks - fullBlocks) >= 0.5;

        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(BAR_CHAR).repeat(fullBlocks));
        if (half && fullBlocks < MAX_BAR_WIDTH) sb.append(HALF_CHAR);
        // Pad to fixed width so columns align
        int padded = MAX_BAR_WIDTH - fullBlocks - (half ? 1 : 0);
        sb.append(" ".repeat(Math.max(0, padded)));
        return sb.toString();
    }
}
