package com.scmspain.controller.validator;

import com.scmspain.controller.command.PublishTweetCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Created by xgimenez on 29/9/17.
 */
public class PublishTweetCommandValidator implements ICommandValidator<PublishTweetCommand> {

    @Autowired
    private ITweetLengthCalculator lengthCalculator;

    @Override
    public void validate(PublishTweetCommand command) {
        if(StringUtils.isEmpty(command.getPublisher())) {
            throw new IllegalArgumentException("A Tweet's Publisher name can't be empty.");
        }
        if(StringUtils.isEmpty(command.getTweet())) {
            throw new IllegalArgumentException("A Tweet can't be empty.");
        }
        if(lengthCalculator.getLength(command.getTweet()) > 140) {
            throw new IllegalArgumentException("A Tweet can't contain more than 140 characters.");
        }
    }

}
