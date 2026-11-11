import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ActualExpense {
    private final String categoryName;
    private final String itemName;
    private double spentAmount;

    public ActualExpense(String categoryName, String itemName, double spentAmount) {
        validateCategory(categoryName);
        validateAmount(spentAmount);
        this.categoryName = categoryName;
        this.itemName = itemName;
        this.spentAmount = spentAmount;
    }

    private static void validateCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название статьи расходов не может быть пустым.");
        }
    }

    private static void validateAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Потраченная сумма не может быть отрицательной.");
        }
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getItemName() {
        return itemName;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        validateAmount(spentAmount);
        this.spentAmount = spentAmount;
    }

    public static List<ActualExpense> readFromFile(String filePath) {
        List<ActualExpense> actualExpenses = new ArrayList<>();
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            System.err.println("Ошибка: Файл реальных расходов не найден: " + filePath);
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
                if (parts.length < 3) {
                    System.err.println("Ошибка в строке " + lineNumber + ": недостаточно данных. Строка пропущена.");
                    continue;
                }

                String categoryName = parts[0];
                String spentAmountStr = parts[parts.length - 1];

                StringBuilder itemNameBuilder = new StringBuilder();
                for (int i = 1; i < parts.length - 1; i++) {
                    itemNameBuilder.append(parts[i]);
                    if (i < parts.length - 2) {
                        itemNameBuilder.append(" ");
                    }
                }
                String itemName = itemNameBuilder.toString();

                try {
                    double spentAmount = Double.parseDouble(spentAmountStr);
                    ActualExpense expense = new ActualExpense(categoryName, itemName, spentAmount);
                    actualExpenses.add(expense);
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка в строке " + lineNumber + ": неверный формат потраченной суммы (" + spentAmountStr + "). Строка пропущена.");
                } catch (IllegalArgumentException e) {
                    System.err.println("Ошибка валидации в строке " + lineNumber + ": " + e.getMessage() + " Строка пропущена.");
                }
            }

        } catch (IOException e) {
            System.exit(1);
        }
        return actualExpenses;
    }
}