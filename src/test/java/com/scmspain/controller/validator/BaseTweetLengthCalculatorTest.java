package com.scmspain.controller.validator;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by xgimenez on 29/9/17.
 */
@FixMethodOrder
public abstract class BaseTweetLengthCalculatorTest {

    protected abstract ITweetLengthCalculator getCalculator();

    private ITweetLengthCalculator calculator;

    @Before
    public void init() {
        calculator = getCalculator();
    }

    @Test
    public void emptyTest() {
        assertThat(calculator.getLength("")).isEqualTo(0);
    }

    @Test
    public void nullTest() {
        assertThat(calculator.getLength(null)).isEqualTo(0);
    }


    @Test
    public void onlyLinkWithSpace() {
        assertThat(calculator.getLength("http://www.google.com ")).isEqualTo(0);
        assertThat(calculator.getLength("https://www.google.com ")).isEqualTo(0);
    }

    @Test
    public void onlyLinkWithEndLine() {
        assertThat(calculator.getLength("http://www.google.com\n")).isEqualTo(0);
        assertThat(calculator.getLength("https://www.google.com\n")).isEqualTo(0);
    }

    @Test
    public void testSingleLine() {
        assertThat(calculator.getLength("123http://www.google.com 456")).isEqualTo(6);
        assertThat(calculator.getLength("123 https://www.google.com  456")).isEqualTo(8);
    }

    @Test
    public void testWithoutLinks() {
        assertThat(calculator.getLength("123456")).isEqualTo(6);
        assertThat(calculator.getLength("123456\n123456")).isEqualTo(13);
    }

    @Test
    public void testMultipleLines() {
        assertThat(calculator.getLength("123http://www.google.com/maps 456\n123 https://www.google.com/maps  456\"")).isEqualTo(16);
    }
}