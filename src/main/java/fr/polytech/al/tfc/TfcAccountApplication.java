package fr.polytech.al.tfc;


import fr.polytech.al.tfc.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TfcAccountApplication implements CommandLineRunner {

    @Autowired
    private AccountRepository accountRepository;

    public static void main(String[] args) {
        SpringApplication.run(TfcAccountApplication.class, args);
    }

    @Override
    public void run(String... args) {

    }

}
