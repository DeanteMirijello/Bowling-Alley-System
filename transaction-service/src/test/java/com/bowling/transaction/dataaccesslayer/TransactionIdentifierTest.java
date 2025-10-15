package com.bowling.transaction.dataaccesslayer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TransactionIdentifierTest {

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        String idValue = "abc-123";
        TransactionIdentifier id1 = new TransactionIdentifier(idValue);
        TransactionIdentifier id2 = new TransactionIdentifier(idValue);
        TransactionIdentifier id3 = TransactionIdentifier.generate();

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1, id3);
    }

    @Test
    void equalsShouldReturnTrueForSameInstance() {
        TransactionIdentifier id = TransactionIdentifier.generate();
        assertEquals(id, id);
    }

    @Test
    void equalsShouldReturnFalseForDifferentType() {
        TransactionIdentifier id = TransactionIdentifier.generate();
        assertNotEquals(id, "not-an-identifier");
    }

    @Test
    void equalsShouldReturnFalseForNull() {
        TransactionIdentifier id = TransactionIdentifier.generate();
        assertNotEquals(null, id);
    }
}