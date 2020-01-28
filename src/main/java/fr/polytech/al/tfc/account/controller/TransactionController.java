package fr.polytech.al.tfc.account.controller;

import fr.polytech.al.tfc.account.business.TransactionBusiness;
import fr.polytech.al.tfc.account.model.Account;
import fr.polytech.al.tfc.account.model.Transaction;
import fr.polytech.al.tfc.account.repository.AccountRepository;
import fr.polytech.al.tfc.account.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final AccountRepository accountRepository;

    private final TransactionBusiness transactionBusiness;

    private final TransactionRepository transactionRepository;

    public TransactionController(AccountRepository accountRepository, TransactionBusiness transactionBusiness, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionBusiness = transactionBusiness;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions() {
        return new ResponseEntity<>(transactionRepository.findAll(), HttpStatus.OK);
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
