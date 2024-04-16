package com.backtothefuture.domain.store;

import java.util.Arrays;

public enum FilteringOption {

    STAR("star"),
    DISTANCE("distance");

    private final String text;

    FilteringOption(String text) {
        this.text = text;
    }

    public static FilteringOption from(String text) {
        return Arrays.stream(FilteringOption.values())
                .filter(filteringOption -> filteringOption.text.equals(text))
                .findAny()
                .orElseThrow(); // TODO 예외 처리
    }
}
