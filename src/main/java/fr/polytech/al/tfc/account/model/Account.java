package fr.polytech.al.tfc.account.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Account {

    @Setter(AccessLevel.NONE)
    protected Integer amountSlidingWindow = 300;

    @Id
    private String accountId;

    @NonNull
    private Integer money;

    private Integer lastWindow = 0;

    @NonNull
    private AccountType accountType;

    public Account(String accountId, @NonNull Integer money, AccountType accountType) {
        this.accountType = accountType;
        this.accountId = accountId;
        this.money = money;
    }

    public Account(AccountDTO accountDTO) {
        this.accountType = accountDTO.getAccountType();
        this.money = accountDTO.getMoney();
    }

    public Integer getAmountSlidingWindow() {
        return amountSlidingWindow;
    }

    public void processTransaction(Transaction transaction, boolean source) {
        if (!"bank".equals(this.accountId)) {
            this.setMoney(this.money += source ? -transaction.getAmount() : transaction.getAmount());
        }
    }

}
