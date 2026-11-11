import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PlannedExpense {
    private static final int REQUIRED_LINES_COUNT = 5;
    private final String categoryName;
    private double plannedAmount;

    public PlannedExpense(String categoryName, double plannedAmount) {
        validateCategory(categoryName);
        validateAmount(plannedAmount);
        this.categoryName = categoryName;
        this.plannedAmount = plannedAmount;
    }

    private static void validateCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название статьи расходов не может быть пустым.");
        }
    }

    private static void validateAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Запланированная сумма не может быть отрицательной.");
        }
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getPlannedAmount() {
        return plannedAmount;
    }

    public void setPlannedAmount(double plannedAmount) {
        validateAmount(plannedAmount);
        this.plannedAmount = plannedAmount;
    }


    public static List<PlannedExpense> readFromFile(String filePath) {
        List<PlannedExpense> plannedExpenses = new ArrayList<>();
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            System.err.println("Ошибка: Файл плана расходов не найден: " + filePath);
            System.exit(1);
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    System.err.println("Ошибка в строке " + lineNumber + ": неверный формат данных. Строка пропущена.");
                    continue;
                }

                String categoryName = parts[0];
                try {
                    double amount = Double.parseDouble(parts[1]);
                    PlannedExpense expense = new PlannedExpense(categoryName, amount);
                    plannedExpenses.add(expense);
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка в строке " + lineNumber + ": неверный формат суммы (" + parts[1] + "). Строка пропущена.");
                } catch (IllegalArgumentException e) {
                    System.err.println("Ошибка валидации в строке " + lineNumber + ": " + e.getMessage() + " Строка пропущена.");
                }
            }
            if (plannedExpenses.size() != REQUIRED_LINES_COUNT) {
                System.err.println("Критическая ошибка: Файл должен содержать ровно " + REQUIRED_LINES_COUNT +
                        " корректных строк с планом. Найдено валидных: " + plannedExpenses.size() + ". Продолжение работы невозможно.");
                System.exit(1);
            }

        } catch (IOException e) {
            System.exit(1);
        }

        return plannedExpenses;
    }
}