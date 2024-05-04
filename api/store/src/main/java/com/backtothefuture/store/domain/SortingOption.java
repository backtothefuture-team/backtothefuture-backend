package com.backtothefuture.store.domain;

import com.backtothefuture.domain.common.enums.StoreErrorCode;
import com.backtothefuture.store.exception.StoreException;
import java.util.Arrays;

public enum SortingOption {

    DEFAULT("default"),
    STAR("star"),
    DISTANCE("distance");

    private final String text;

    SortingOption(String text) {
        this.text = text;
    }

    public static SortingOption from(String text) {
        return Arrays.stream(SortingOption.values())
                .filter(sortingOption -> sortingOption.text.equals(text))
                .findAny()
                .orElseThrow(() -> new StoreException(StoreErrorCode.INVALID_SORTING_OPTION));
    }
}
