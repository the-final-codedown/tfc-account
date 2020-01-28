package fr.polytech.al.tfc.account.controller;

import fr.polytech.al.tfc.account.model.*;
import fr.polytech.al.tfc.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Value("${profile.host}")
    private String profileHost;

    private final AccountRepository accountRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{accountType}/accounts")
    public ResponseEntity<List<Account>> viewAccounts(@PathVariable(value = "accountType") AccountType accountType) {
        List<Account> accounts = accountRepository.findAllByAccountType(accountType);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> viewAccount(@PathVariable(value = "id") String id) {
        Optional<Account> account = accountRepository.findById(id);
        return account
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/cap")
    public ResponseEntity<Cap> getCap(@PathVariable(value = "id") String id) {
        System.out.println("Fetching account with id " + id);
        Optional<Account> account = accountRepository.findById(id);
        return account
                .map(value -> new ResponseEntity<>(new Cap(value.getMoney(), value.getAmountSlidingWindow()), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{email}/accounts")
    public ResponseEntity<Account> createAccountForProfile(@PathVariable(value = "email") String email, @RequestBody AccountDTO accountDTO) throws URISyntaxException {
        //todo use global variable
        String host = "http://"+profileHost+"/profiles/"+email;
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(host);
        ResponseEntity<ProfileDTO> result = restTemplate.getForEntity(uri, ProfileDTO.class);
        ProfileDTO ower = result.getBody();
        System.out.println(ower);

        if (ower != null) {
            Account account = new Account(accountDTO);
            account.setOwner(ower);
            accountRepository.save(account);
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
