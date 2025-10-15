package com.bowling.lane.dataaccesslayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
public class LaneIdentifier implements Serializable {

    private String id;

    protected LaneIdentifier() {
        this.id = UUID.randomUUID().toString();
    }

    public LaneIdentifier(String id) {
        this.id = id;
    }

    public static LaneIdentifier generate() {
        return new LaneIdentifier(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LaneIdentifier)) return false;
        LaneIdentifier that = (LaneIdentifier) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

