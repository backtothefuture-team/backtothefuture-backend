package com.backtothefuture.domain.common.util;

import java.util.Random;

public class RandomNumUtil {

    /**
     * random number 생성
     */

    public static String createRandomNum(int length) { // length는 자릿수

        Random random = new Random(System.currentTimeMillis()); // 시드 설정

        return String.valueOf(
                random.nextInt(9 * (int) Math.pow(10, length - 1)) + (int) Math.pow(10, length - 1));
    }

}
