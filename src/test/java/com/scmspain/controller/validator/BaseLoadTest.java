package com.scmspain.controller.validator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by xgimenez on 29/9/17.
 */
@Ignore
public abstract class BaseLoadTest {
    protected abstract ITweetLengthCalculator getCalculator();

    private ITweetLengthCalculator calculator;

    private static List<String> tweets;

    @BeforeClass
    public static void initData() {
        tweets = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            String str = UUID.randomUUID().toString() + "http://google.com/maps " + UUID.randomUUID().toString();
            tweets.add(str);
        }
    }

    @Before
    public void init() {
        calculator = getCalculator();
    }



    @Test
    public void loadTest() {
        tweets.forEach(x -> calculator.getLength(x));
    }
}
