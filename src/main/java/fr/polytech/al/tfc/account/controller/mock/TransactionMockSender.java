package fr.polytech.al.tfc.account.controller.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.polytech.al.tfc.account.model.Account;
import fr.polytech.al.tfc.account.model.Transaction;
import fr.polytech.al.tfc.account.repository.AccountRepository;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

//@EnableScheduling
@Component
public class TransactionMockSender {

    private AccountRepository accountRepository;
    private String topic;
    private Producer<String, String> producer;

    public TransactionMockSender(AccountRepository accountRepository, @Value("${kafkabroker}") String broker) {
        //todo warning kafka broker here
        String customer = "kafka-transaction";
        this.topic = String.format("%s", customer);

        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, broker);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<>(props);

        this.accountRepository = accountRepository;
    }

    public void addTransactionIntoAccounts() throws JsonProcessingException {
        List<Account> accounts = giveRandomAccount(accountRepository.findAll());
        Account account1 = accounts.get(0);
        Account account2 = accounts.get(1);
        ObjectMapper objectMapper = new ObjectMapper();
        Random r = new Random();
        Integer randomInteger = r.nextInt();
        Transaction transaction = new Transaction(account1.getAccountId(),account2.getAccountId(),randomInteger,LocalDateTime.now());
        producer.send(new ProducerRecord<>(topic, transaction.getSource(), objectMapper.writeValueAsString(transaction)));
        producer.flush();
    }

    public List<Account> giveRandomAccount(List<Account> accounts) {
        List<Account> accountsRandomChosen = new ArrayList<>();
        Account account = new Account();
        Random random = new Random();
        while (accountsRandomChosen.size() != 2) {
            int randomInteger = random.nextInt(accounts.size() - 1);
            if (accountsRandomChosen.size() == 1) {
                account = accounts.get(randomInteger);
                if (accountsRandomChosen.get(0).equals(account)) {
                    continue;
                }
            }
            accountsRandomChosen.add(account);
        }
        return accountsRandomChosen;

    }

}