package bg.softuni.model.binding;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class MoneyTransactionModel {
    private BigDecimal amount;

    public MoneyTransactionModel() {
        this.amount = BigDecimal.ZERO;
    }
    public MoneyTransactionModel(BigDecimal price) {
       this.setAmount(price);
    }

    @DecimalMin(value = "0.01", message = "Amount can't be lower than 0.01 euro!")
    @DecimalMax(value = "5000000", message = "Amount can't be grater than 5 million!")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
