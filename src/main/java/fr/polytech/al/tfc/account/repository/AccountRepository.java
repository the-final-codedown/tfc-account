package fr.polytech.al.tfc.account.repository;

import fr.polytech.al.tfc.account.model.Account;
import fr.polytech.al.tfc.account.model.AccountType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("tfc.account")
public interface AccountRepository extends MongoRepository<Account, String> {

    List<Account> findAllByAccountType(AccountType accountType);
}
