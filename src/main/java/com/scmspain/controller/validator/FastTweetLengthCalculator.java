package com.scmspain.controller.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xgimenez on 29/9/17.
 */
public class FastTweetLengthCalculator implements ITweetLengthCalculator {

    private final Pattern pattern;

    public FastTweetLengthCalculator() {
        this.pattern = Pattern.compile("https?://[\\S]*\\s");
    }


    @Override
    public int getLength(String tweet) {
        int count = 0;
        if(tweet != null) {
            Matcher matcher = pattern.matcher(tweet);
            int lastEnd = 0;
            while (matcher.find()) {
                int start = matcher.start();
                count +=  start - lastEnd;
                lastEnd = matcher.end();
            }
            if(lastEnd < tweet.length()) {
                count += (tweet.length() - lastEnd);
            }
        }



        return count;
    }
}
