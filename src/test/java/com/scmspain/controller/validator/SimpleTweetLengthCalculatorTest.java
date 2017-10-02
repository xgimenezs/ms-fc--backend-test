package com.scmspain.controller.validator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by xgimenez on 29/9/17.
 */
public class SimpleTweetLengthCalculatorTest extends BaseTweetLengthCalculatorTest {
    @Override
    protected ITweetLengthCalculator getCalculator() {
        return new SimpleTweetLengthCalculator();
    }
}