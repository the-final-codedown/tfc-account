package fr.polytech.al.tfc.account.business;

import fr.polytech.al.tfc.account.model.Account;
import fr.polytech.al.tfc.account.model.Transaction;
import fr.polytech.al.tfc.account.repository.AccountRepository;
import fr.polytech.al.tfc.account.repository.TransactionRepository;
import org.springframework.stereotype.Component;

@Component
public class TransactionBusiness {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    public TransactionBusiness(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public void processTransaction(Transaction transaction, Account source, Account destination) {
        transactionRepository.save(transaction);
        source.processTransaction(transaction, true);
        accountRepository.save(source);
        destination.processTransaction(transaction, false);
        accountRepository.save(destination);
    }
}
