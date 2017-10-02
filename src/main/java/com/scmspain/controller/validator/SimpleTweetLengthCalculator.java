package com.scmspain.controller.validator;

/**
 * Created by xgimenez on 29/9/17.
 */
public class SimpleTweetLengthCalculator implements ITweetLengthCalculator {

    @Override
    public int getLength(String tweet) {
        int value = 0;
        if(tweet != null) {
            value = tweet.replaceAll("https?://[\\S]*\\s", "").length();
        }
        return value;
    }
}
