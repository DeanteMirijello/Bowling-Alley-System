package com.bowling.transaction.config;

import com.bowling.transaction.dataaccesslayer.Transaction;
import com.bowling.transaction.dataaccesslayer.TransactionIdentifier;
import com.bowling.transaction.dataaccesslayer.TransactionRepository;
import com.bowling.transaction.dataaccesslayer.TransactionStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;;

@Profile("!test")
@Component
public class DatabaseLoaderService implements CommandLineRunner {

    private final TransactionRepository transactionRepository;

    public DatabaseLoaderService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (transactionRepository.count() == 0) {

            List<Transaction> transactions = List.of(
                    Transaction.builder()
                            .transactionIdentifier(new TransactionIdentifier(UUID.randomUUID().toString()))
                            .customerName("Alice Smith")
                            .laneId(UUID.randomUUID().toString())
                            .bowlingBallId(UUID.randomUUID().toString())
                            .shoeId(UUID.randomUUID().toString())
                            .laneZone("ZONE_1")
                            .status(TransactionStatus.OPEN)
                            .totalPrice(new BigDecimal("28.50"))
                            .dateCompleted(LocalDate.now().minusDays(2).toString())
                            .build(),

                    Transaction.builder()
                            .transactionIdentifier(new TransactionIdentifier(UUID.randomUUID().toString()))
                            .customerName("Bob Johnson")
                            .laneId(UUID.randomUUID().toString())
                            .bowlingBallId(UUID.randomUUID().toString())
                            .shoeId(UUID.randomUUID().toString())
                            .laneZone("ZONE_2")
                            .status(TransactionStatus.COMPLETED)
                            .totalPrice(new BigDecimal("35.00"))
                            .dateCompleted(LocalDate.now().minusDays(1).toString())
                            .build(),

                    Transaction.builder()
                            .transactionIdentifier(new TransactionIdentifier(UUID.randomUUID().toString()))
                            .customerName("Clara Wu")
                            .laneId(UUID.randomUUID().toString())
                            .bowlingBallId(UUID.randomUUID().toString())
                            .shoeId(UUID.randomUUID().toString())
                            .laneZone("ZONE_3")
                            .status(TransactionStatus.CANCELLED)
                            .totalPrice(new BigDecimal("0.00"))
                            .dateCompleted(LocalDate.now().minusDays(5).toString())
                            .build(),

                    Transaction.builder()
                            .transactionIdentifier(new TransactionIdentifier(UUID.randomUUID().toString()))
                            .customerName("Daniel Lee")
                            .laneId(UUID.randomUUID().toString())
                            .bowlingBallId(UUID.randomUUID().toString())
                            .shoeId(UUID.randomUUID().toString())
                            .laneZone("ZONE_1")
                            .status(TransactionStatus.COMPLETED)
                            .totalPrice(new BigDecimal("31.75"))
                            .dateCompleted(LocalDate.now().minusDays(3).toString())
                            .build(),

                    Transaction.builder()
                            .transactionIdentifier(new TransactionIdentifier(UUID.randomUUID().toString()))
                            .customerName("Ella Martinez")
                            .laneId(UUID.randomUUID().toString())
                            .bowlingBallId(UUID.randomUUID().toString())
                            .shoeId(UUID.randomUUID().toString())
                            .laneZone("ZONE_2")
                            .status(TransactionStatus.OPEN)
                            .totalPrice(new BigDecimal("26.99"))
                            .dateCompleted(LocalDate.now().minusDays(4).toString())
                            .build(),

                    Transaction.builder()
                            .transactionIdentifier(new TransactionIdentifier(UUID.randomUUID().toString()))
                            .customerName("Frank O'Reilly")
                            .laneId(UUID.randomUUID().toString())
                            .bowlingBallId(UUID.randomUUID().toString())
                            .shoeId(UUID.randomUUID().toString())
                            .laneZone("ZONE_3")
                            .status(TransactionStatus.OPEN)
                            .totalPrice(new BigDecimal("29.99"))
                            .dateCompleted(LocalDate.now().minusDays(6).toString())
                            .build(),

                    Transaction.builder()
                            .transactionIdentifier(new TransactionIdentifier(UUID.randomUUID().toString()))
                            .customerName("Grace Kim")
                            .laneId(UUID.randomUUID().toString())
                            .bowlingBallId(UUID.randomUUID().toString())
                            .shoeId(UUID.randomUUID().toString())
                            .laneZone("ZONE_1")
                            .status(TransactionStatus.CANCELLED)
                            .totalPrice(new BigDecimal("0.00"))
                            .dateCompleted(LocalDate.now().minusDays(7).toString())
                            .build(),

                    Transaction.builder()
                            .transactionIdentifier(new TransactionIdentifier(UUID.randomUUID().toString()))
                            .customerName("Henry Zhao")
                            .laneId(UUID.randomUUID().toString())
                            .bowlingBallId(UUID.randomUUID().toString())
                            .shoeId(UUID.randomUUID().toString())
                            .laneZone("ZONE_2")
                            .status(TransactionStatus.COMPLETED)
                            .totalPrice(new BigDecimal("33.00"))
                            .dateCompleted(LocalDate.now().minusDays(8).toString())
                            .build(),

                    Transaction.builder()
                            .transactionIdentifier(new TransactionIdentifier(UUID.randomUUID().toString()))
                            .customerName("Isabelle Dubois")
                            .laneId(UUID.randomUUID().toString())
                            .bowlingBallId(UUID.randomUUID().toString())
                            .shoeId(UUID.randomUUID().toString())
                            .laneZone("ZONE_3")
                            .status(TransactionStatus.OPEN)
                            .totalPrice(new BigDecimal("27.45"))
                            .dateCompleted(LocalDate.now().minusDays(9).toString())
                            .build(),

                    Transaction.builder()
                            .transactionIdentifier(new TransactionIdentifier(UUID.randomUUID().toString()))
                            .customerName("Jayden Patel")
                            .laneId(UUID.randomUUID().toString())
                            .bowlingBallId(UUID.randomUUID().toString())
                            .shoeId(UUID.randomUUID().toString())
                            .laneZone("ZONE_1")
                            .status(TransactionStatus.COMPLETED)
                            .totalPrice(new BigDecimal("34.99"))
                            .dateCompleted(LocalDate.now().minusDays(10).toString())
                            .build()
            );

            transactionRepository.saveAll(transactions);
        }
    }
}
