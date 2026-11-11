import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseAnalyzer {

    public void analyzeAndReport(List<PlannedExpense> plannedExpenses, List<ActualExpense> actualExpenses) {
        Map<String, Double> actualTotalsByCategory = actualExpenses.stream()
                .collect(Collectors.groupingBy(
                        ActualExpense::getCategoryName,
                        Collectors.summingDouble(ActualExpense::getSpentAmount)
                ));

        System.out.println("\n ТАБЛИЦА РАСХОДОВ");
        System.out.printf("%-20s | %-15s | %-15s | %-15s%n",
                "Статья расходов", "План (руб.)", "Факт (руб.)", "Выполнение (%)");
        System.out.println("-".repeat(75));

        for (PlannedExpense planned : plannedExpenses) {
            String category = planned.getCategoryName();
            double plannedAmount = planned.getPlannedAmount();
            double actualAmount = actualTotalsByCategory.getOrDefault(category, 0.0);

            double percentage = plannedAmount > 0 ? (actualAmount / plannedAmount) * 100 : (actualAmount > 0 ? 100.0 : 0.0);

            System.out.printf("%-20s | %-15.2f | %-15.2f | %-15.2f%%%n",
                    category, plannedAmount, actualAmount, percentage);
        }
        System.out.println("-".repeat(75));

        printExceededCategories(plannedExpenses, actualExpenses, actualTotalsByCategory);
    }

    private void printExceededCategories(List<PlannedExpense> plannedExpenses,
                                         List<ActualExpense> actualExpenses,
                                         Map<String, Double> actualTotalsByCategory) {
        System.out.println("\n СТАТЬИ С ПРЕВЫШЕНИЕМ ПЛАНА");

        boolean hasExceeds = false;

        for (PlannedExpense planned : plannedExpenses) {
            String category = planned.getCategoryName();
            double plannedAmount = planned.getPlannedAmount();
            double actualAmount = actualTotalsByCategory.getOrDefault(category, 0.0);

            if (actualAmount > plannedAmount) {
                hasExceeds = true;
                System.out.println("\nСтатья расходов: " + category + " (План: " + plannedAmount + ", Факт: " + actualAmount + ")");
                System.out.println("Список трат (по убыванию суммы):");

                // Фильтруем продукты/услуги для данной категории и сортируем по убыванию потраченной суммы
                List<ActualExpense> categoryItems = actualExpenses.stream()
                        .filter(e -> e.getCategoryName().equalsIgnoreCase(category))
                        .sorted(Comparator.comparingDouble(ActualExpense::getSpentAmount).reversed())
                        .toList();

                for (ActualExpense item : categoryItems) {
                    System.out.printf("  - %s: %.2f руб.%n", item.getItemName(), item.getSpentAmount());
                }
            }
        }

        if (!hasExceeds) {
            System.out.println("Превышений плана расходов по статьям не обнаружено.");
        }
    }
}