package com.bowling.shoe.dataaccesslayer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
public class ShoeIdentifier implements Serializable {

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    protected ShoeIdentifier() {
        this.id = UUID.randomUUID();
    }

    public ShoeIdentifier(UUID id) {
        this.id = id;
    }

    public static ShoeIdentifier generate() {
        return new ShoeIdentifier(UUID.randomUUID());
    }

    public static ShoeIdentifier fromString(String id) {
        return new ShoeIdentifier(UUID.fromString(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShoeIdentifier)) return false;
        ShoeIdentifier that = (ShoeIdentifier) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}