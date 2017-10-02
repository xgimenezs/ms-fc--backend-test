package com.scmspain.controller.validator;

/**
 * Created by xgimenez on 29/9/17.
 */
public class FastTweetLengthCalculatorLoadTest extends BaseLoadTest {

    @Override
    protected ITweetLengthCalculator getCalculator() {
        return new FastTweetLengthCalculator();
    }
}
