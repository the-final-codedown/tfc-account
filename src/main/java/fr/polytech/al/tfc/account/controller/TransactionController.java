package fr.polytech.al.tfc.account.controller;

import fr.polytech.al.tfc.account.business.TransactionBusiness;
import fr.polytech.al.tfc.account.model.Account;
import fr.polytech.al.tfc.account.model.Transaction;
import fr.polytech.al.tfc.account.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final AccountRepository accountRepository;

    private final TransactionBusiness transactionBusiness;

    public TransactionController(AccountRepository accountRepository, TransactionBusiness transactionBusiness) {
        this.accountRepository = accountRepository;
        this.transactionBusiness = transactionBusiness;
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        Optional<Account> optionalSourceAccount = accountRepository.findById(transaction.getSource());
        Optional<Account> optionalDestinationAccount = accountRepository.findById(transaction.getReceiver());
        if (optionalSourceAccount.isPresent() && optionalDestinationAccount.isPresent()) {
            transactionBusiness.processTransaction(transaction, optionalSourceAccount.get(), optionalDestinationAccount.get());
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
