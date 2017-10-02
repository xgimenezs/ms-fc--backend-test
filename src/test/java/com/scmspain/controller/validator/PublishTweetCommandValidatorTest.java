package com.scmspain.controller.validator;

import com.scmspain.controller.command.PublishTweetCommand;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by xgimenez on 29/9/17.
 */
@SpringBootTest(classes = {PublishTweetCommandValidator.class, FastTweetLengthCalculator.class})
@RunWith(SpringRunner.class)
public class PublishTweetCommandValidatorTest {

    @Autowired
    public ICommandValidator<PublishTweetCommand> validator;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testEmptyPublisher() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("A Tweet's Publisher name can't be empty.");
        PublishTweetCommand command = mock(PublishTweetCommand.class);
        when(command.getPublisher()).thenReturn("");
        when(command.getTweet()).thenReturn("Tweet");
        validator.validate(command);
    }

    @Test
    public void testEmptyTweet() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("A Tweet can't be empty");
        PublishTweetCommand command = mock(PublishTweetCommand.class);
        when(command.getPublisher()).thenReturn("Publisher");
        when(command.getTweet()).thenReturn("");

        validator.validate(command);
    }

    @Test
    public void testNullPublisher() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("A Tweet's Publisher name can't be empty.");
        PublishTweetCommand command = mock(PublishTweetCommand.class);
        when(command.getPublisher()).thenReturn(null);
        when(command.getTweet()).thenReturn("Tweet");
        validator.validate(command);
    }

    @Test
    public void testNullTweet() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("A Tweet can't be empty");
        PublishTweetCommand command = mock(PublishTweetCommand.class);
        when(command.getPublisher()).thenReturn("Publisher");
        when(command.getTweet()).thenReturn(null);

        validator.validate(command);
    }

    @Test
    public void testTooMuchLongTweet() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("A Tweet can't contain more than 140 characters.");
        PublishTweetCommand command = mock(PublishTweetCommand.class);
        when(command.getPublisher()).thenReturn("Publisher");
        when(command.getTweet()).thenReturn("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        validator.validate(command);
    }

    @Test
    public void testOkTweet() {
        expectedEx = ExpectedException.none();
        PublishTweetCommand command = mock(PublishTweetCommand.class);
        when(command.getPublisher()).thenReturn("Publisher");
        when(command.getTweet()).thenReturn("Lorem ipsum dolor sit amet, http://www.google.com/maps consectetur adipiscing elit...");

        validator.validate(command);
    }
}