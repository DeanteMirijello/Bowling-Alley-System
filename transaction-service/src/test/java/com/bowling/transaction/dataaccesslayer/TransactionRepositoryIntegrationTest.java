package com.bowling.transaction.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
class TransactionRepositoryIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private TransactionIdentifier id1;
    private TransactionIdentifier id2;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        id1 = TransactionIdentifier.generate();
        id2 = TransactionIdentifier.generate();

        Transaction tx1 = Transaction.builder()
                .transactionIdentifier(id1)
                .customerName("Alice Johnson")
                .laneId(UUID.randomUUID().toString())
                .bowlingBallId(UUID.randomUUID().toString())
                .shoeId(UUID.randomUUID().toString())
                .laneZone("ZONE_1")
                .status(TransactionStatus.OPEN)
                .totalPrice(new BigDecimal("25.00"))
                .dateCompleted(LocalDate.now().toString())
                .build();

        Transaction tx2 = Transaction.builder()
                .transactionIdentifier(id2)
                .customerName("Bob Smith")
                .laneId(UUID.randomUUID().toString())
                .bowlingBallId(UUID.randomUUID().toString())
                .shoeId(UUID.randomUUID().toString())
                .laneZone("ZONE_2")
                .status(TransactionStatus.COMPLETED)
                .totalPrice(new BigDecimal("35.00"))
                .dateCompleted(LocalDate.now().minusDays(2).toString())
                .build();

        transactionRepository.saveAll(List.of(tx1, tx2));
    }

    @Test
    void whenFindAll_thenReturnsAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(2);
    }

    @Test
    void whenFindById_thenReturnsTransaction() {
        List<Transaction> all = transactionRepository.findAll();
        String dbId = all.get(0).getId();

        Optional<Transaction> found = transactionRepository.findById(dbId);
        assertTrue(found.isPresent());
        assertEquals(all.get(0).getCustomerName(), found.get().getCustomerName());
    }

    @Test
    void whenFindByTransactionIdentifier_thenReturnsCorrectTransaction() {
        Transaction found = transactionRepository.findByTransactionIdentifier_TransactionId(id1.getId());
        assertNotNull(found);
        assertEquals("Alice Johnson", found.getCustomerName());
    }

    @Test
    void whenSave_thenTransactionIsStored() {
        TransactionIdentifier newId = TransactionIdentifier.generate();

        Transaction tx = Transaction.builder()
                .transactionIdentifier(newId)
                .customerName("Carla Reed")
                .laneId(UUID.randomUUID().toString())
                .bowlingBallId(UUID.randomUUID().toString())
                .shoeId(UUID.randomUUID().toString())
                .laneZone("ZONE_3")
                .status(TransactionStatus.CANCELLED)
                .totalPrice(new BigDecimal("0.00"))
                .dateCompleted(LocalDate.now().minusDays(1).toString())
                .build();

        Transaction saved = transactionRepository.save(tx);

        assertNotNull(saved.getId());
        assertEquals("Carla Reed", saved.getCustomerName());
    }

    @Test
    void whenDelete_thenTransactionIsRemoved() {
        List<Transaction> all = transactionRepository.findAll();
        Transaction toDelete = all.get(0);

        transactionRepository.delete(toDelete);

        Optional<Transaction> deleted = transactionRepository.findById(toDelete.getId());
        assertTrue(deleted.isEmpty());
    }
}