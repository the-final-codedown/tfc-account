package fr.polytech.al.tfc.account.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BankAccount extends Account {

    public BankAccount() {
        super.setAccountId("bank");
        super.setMoney(Integer.MAX_VALUE);
        super.amountSlidingWindow = Integer.MAX_VALUE;
        super.setAccountType(AccountType.CHECK);
    }

    @Override
    public void setMoney(Integer integer) {
        // do nothing as the bank is immutable
    }
}
