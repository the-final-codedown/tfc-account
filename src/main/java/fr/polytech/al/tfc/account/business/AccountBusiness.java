package fr.polytech.al.tfc.account.business;

import fr.polytech.al.tfc.account.model.Account;
import fr.polytech.al.tfc.account.model.AccountDTO;
import fr.polytech.al.tfc.account.model.ProfileDTO;
import fr.polytech.al.tfc.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class AccountBusiness {

    private final AccountRepository accountRepository;

    @Value("${profile.host}")
    private String profileHost;

    public AccountBusiness(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccountForProfile(String email, AccountDTO accountDTO) {
        try {
            String host = "http://" + profileHost + "/profiles/" + email;
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(host);
            ResponseEntity<ProfileDTO> result = restTemplate.getForEntity(uri, ProfileDTO.class);
            ProfileDTO owner = result.getBody();

            Account account = new Account(accountDTO)
                    .setOwner(owner);
            accountRepository.save(account);
            return account;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
