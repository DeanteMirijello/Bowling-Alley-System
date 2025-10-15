package com.bowling.transaction.config;

import com.bowling.transaction.dataaccesslayer.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseLoaderServiceTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldPreloadTransactionsOnStartup() {
        long count = transactionRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(10);
    }
}