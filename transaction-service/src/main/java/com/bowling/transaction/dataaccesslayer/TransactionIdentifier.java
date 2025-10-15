package com.bowling.transaction.dataaccesslayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
public class TransactionIdentifier implements Serializable {

    private String id;

    protected TransactionIdentifier() { this.id = UUID.randomUUID().toString(); }

    public TransactionIdentifier(String id) { this.id = id; }

    public static TransactionIdentifier generate() { return new TransactionIdentifier(UUID.randomUUID().toString()); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionIdentifier)) return false;
        TransactionIdentifier that = (TransactionIdentifier) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

