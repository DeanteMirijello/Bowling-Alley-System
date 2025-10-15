package com.bowling.lane.dataaccesslayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class LaneZone implements Serializable {

    private String zone;

    protected LaneZone() {
    }

    public LaneZone(String zone) {
        this.zone = zone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LaneZone)) return false;
        LaneZone that = (LaneZone) o;
        return Objects.equals(zone, that.zone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zone);
    }
}

