import java.util.List;
public class Main {
    public static void main(String[] args) {
        String plannedFile = "src/planned_expenses.txt";
        String actualFile = "src/actual_expenses.txt";

        List<PlannedExpense> plannedExpenses = PlannedExpense.readFromFile(plannedFile);
        List<ActualExpense> actualExpenses = ActualExpense.readFromFile(actualFile);

        ExpenseAnalyzer analyzer = new ExpenseAnalyzer();
        analyzer.analyzeAndReport(plannedExpenses, actualExpenses);
    }
}