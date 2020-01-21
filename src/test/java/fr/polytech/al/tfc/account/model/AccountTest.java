package fr.polytech.al.tfc.account.model;

import fr.polytech.al.tfc.account.repository.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void immutableBank() {
        if (!accountRepository.existsById("bank")) {
            accountRepository.save(new BankAccount());
        }
        Account bank = accountRepository.findById("bank").get();
        assertEquals(Integer.MAX_VALUE, (int) bank.getMoney());
        bank.setMoney(200);
        assertEquals(Integer.MAX_VALUE, (int) bank.getMoney());
        assertEquals("bank", bank.getAccountId());
        assertEquals(Integer.MAX_VALUE, (int) bank.getAmountSlidingWindow());
    }
}
