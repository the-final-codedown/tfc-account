package fr.polytech.al.tfc.account.controller;

import fr.polytech.al.tfc.account.model.Account;
import fr.polytech.al.tfc.account.model.AccountDTO;
import fr.polytech.al.tfc.account.model.Cap;
import fr.polytech.al.tfc.account.model.ProfileDTO;
import fr.polytech.al.tfc.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
        String host = "http://localhost:8083/profiles/"+email;
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(host);
        ResponseEntity<ProfileDTO> result = restTemplate.getForEntity(uri, ProfileDTO.class);
        ProfileDTO ower = result.getBody();

        if (ower != null) {
            Account account = new Account(accountDTO);
            account.setOwner(ower);
            accountRepository.save(account);
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
