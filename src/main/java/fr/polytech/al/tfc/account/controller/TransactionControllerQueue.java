package fr.polytech.al.tfc.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.polytech.al.tfc.account.business.TransactionBusiness;
import fr.polytech.al.tfc.account.exception.NonExistentAccountException;
import fr.polytech.al.tfc.account.model.Account;
import fr.polytech.al.tfc.account.model.Transaction;
import fr.polytech.al.tfc.account.repository.AccountRepository;
import fr.polytech.al.tfc.account.repository.TransactionRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EnableScheduling
@Component
public class TransactionControllerQueue {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionBusiness transactionBusiness;
    private KafkaConsumer<String, String> consumerTransaction;

    @Autowired
    public TransactionControllerQueue(TransactionRepository transactionRepository,
                                      AccountRepository accountRepository,
                                      KafkaConsumer<String, String> consumerTransaction, TransactionBusiness transactionBusiness) {
        this.transactionBusiness = transactionBusiness;
        this.consumerTransaction = consumerTransaction;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;

        String receivingQueue = "kafka-transaction";
        List<String> topics = new ArrayList<>();
        String topic = String.format("%s", receivingQueue);
        topics.add(topic);
        consumerTransaction.subscribe(topics);
    }

    @Scheduled(fixedDelay = 5000)
    public void listenTransaction() {
        try {
            ConsumerRecords<String, String> records = consumerTransaction.poll(Duration.ofSeconds(1));
            ObjectMapper objectMapper = new ObjectMapper();
            for (ConsumerRecord<String, String> record : records) {
                String key = record.key();
                String value = record.value();
                Transaction transaction = objectMapper.readValue(value, Transaction.class);
                Optional<Account> optionalSourceAccount = accountRepository.findById(transaction.getSource());
                Optional<Account> optionalDestinationAccount = accountRepository.findById(transaction.getReceiver());
                if (optionalSourceAccount.isPresent() && optionalDestinationAccount.isPresent()) {
                    transactionBusiness.processTransaction(transaction, optionalSourceAccount.get(), optionalDestinationAccount.get());
                } else {
                    throw new NonExistentAccountException();
                }
            }
        } catch (KafkaException | IOException e) {
            System.out.println(e);
        }
    }
}