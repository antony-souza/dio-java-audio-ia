package dio.budgeting;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BudgetingApplicationTests {

    @Test
    void rejectsInvalidTransactions() {
        assertThrows(IllegalArgumentException.class, () -> new Transaction(" ", 100, Category.GROCERIES));
        assertThrows(IllegalArgumentException.class, () -> new Transaction("Mercado", 0, Category.GROCERIES));
        assertThrows(IllegalArgumentException.class, () -> new Transaction("Mercado", -1, Category.GROCERIES));
        assertThrows(IllegalArgumentException.class, () -> new Transaction("Mercado", 100, null));
    }

    @Test
    void keepsAmountInCentsInDomain() {
        var transaction = new Transaction("Mercado", 1250, Category.GROCERIES);
        assertEquals(1250, transaction.getAmount());
        assertEquals("Mercado", transaction.getDescription());
    }

}
