package dio.budgeting.application;

import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersistTransactionUseCaseTests {
    @Test
    void savesValidTransactionAndConvertsCentsToReais() {
        var repository = mock(TransactionRepository.class);
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var useCase = new PersistTransactionUseCase(repository);

        var output = useCase.execute(new PersistTransactionInput("Café", 1250, Category.GROCERIES));

        assertEquals(new BigDecimal("12.50"), output.value());
        verify(repository).save(any(Transaction.class));
    }

    @Test
    void rejectsInvalidInputBeforeSaving() {
        var repository = mock(TransactionRepository.class);
        var useCase = new PersistTransactionUseCase(repository);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(new PersistTransactionInput("Café", 0, Category.GROCERIES)));

        verify(repository, never()).save(any(Transaction.class));
    }
}
