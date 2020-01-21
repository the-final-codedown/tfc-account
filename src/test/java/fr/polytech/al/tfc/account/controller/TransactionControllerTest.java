package fr.polytech.al.tfc.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import fr.polytech.al.tfc.account.model.Account;
import fr.polytech.al.tfc.account.model.AccountType;
import fr.polytech.al.tfc.account.model.Transaction;
import fr.polytech.al.tfc.account.repository.AccountRepository;
import fr.polytech.al.tfc.account.repository.TransactionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    private final String idAccount1 = "idAccount1";
    private final String idAccount2 = "idAccount2";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private Account account1;
    private Account account2;

    @Before
    public void setUp() {
        account1 = accountRepository.save(new Account(idAccount1, 300, AccountType.CHECK));
        account2 = accountRepository.save(new Account(idAccount2, 300, AccountType.CHECK));
    }

    @Test
    public void addTransaction() throws Exception {
        LocalDateTime transactionDate = LocalDateTime.now();
        Integer transactionAmount = 29;
        JsonObject transactionJsonObject = new JsonObject();
        transactionJsonObject.addProperty("source", idAccount1);
        transactionJsonObject.addProperty("receiver", idAccount2);
        transactionJsonObject.addProperty("amount", transactionAmount);
        transactionJsonObject.addProperty("date", transactionDate.toString());

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(transactionJsonObject.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Transaction transaction = objectMapper.readValue(result, Transaction.class);

        assertEquals(idAccount2, transaction.getReceiver());
        assertEquals(idAccount1, transaction.getSource());
        assertEquals(transactionDate, transaction.getDate());
        assertEquals(transactionAmount, transaction.getAmount());

        List<Transaction> transactionList = transactionRepository.findAllBySourceAndReceiverAndDate(idAccount2, idAccount1, transactionDate);
        for (Transaction transaction1 : transactionList) {
            assertEquals(idAccount1, transaction1.getSource());
            assertEquals(idAccount2, transaction1.getReceiver());
            assertEquals(transactionDate, transaction1.getDate());
            assertEquals(transactionAmount, transaction1.getAmount());
        }
    }
}