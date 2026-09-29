package dio.budgeting.domain;

import lombok.Getter;

@Getter
public class Transaction {
    private final TransactionId id;
    private final String description;
    private final long amount;
    private final Category category;

    public Transaction(String description, long amount, Category category) {
        this(new TransactionId(), description, amount, category);
    }

    public Transaction(TransactionId id, String description, long amount, Category category) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador é obrigatório");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero (em centavos)");
        }
        if (category == null) {
            throw new IllegalArgumentException("A categoria é obrigatória");
        }

        this.id = id;
        this.description = description.trim();
        this.amount = amount;
        this.category = category;
    }
}
