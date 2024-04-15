package com.backtothefuture.domain.reservation.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum TimeType {
    AM,
    PM;

    @JsonCreator
    public static TimeType fromRequest(String inputString) {
        for (TimeType timeType : TimeType.values()) {
            if (timeType.name().equals(inputString)) {
                return timeType;
            }
        }
        return null;
    }
}
