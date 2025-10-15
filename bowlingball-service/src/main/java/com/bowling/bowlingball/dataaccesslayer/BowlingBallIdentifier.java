package com.bowling.bowlingball.dataaccesslayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
public class BowlingBallIdentifier implements Serializable {

    private String id;

    protected BowlingBallIdentifier() {
        this.id = UUID.randomUUID().toString();
    }

    public BowlingBallIdentifier(String id) {
        this.id = id;
    }

    public static BowlingBallIdentifier generate() {
        return new BowlingBallIdentifier(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BowlingBallIdentifier)) return false;
        BowlingBallIdentifier that = (BowlingBallIdentifier) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
