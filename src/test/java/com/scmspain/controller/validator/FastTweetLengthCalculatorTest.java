package com.scmspain.controller.validator;

import static org.junit.Assert.*;

/**
 * Created by xgimenez on 29/9/17.
 */
public class FastTweetLengthCalculatorTest extends BaseTweetLengthCalculatorTest {

    @Override
    protected ITweetLengthCalculator getCalculator() {
        return new FastTweetLengthCalculator();
    }
}