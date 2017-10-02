package com.scmspain.services;

import com.scmspain.entities.Tweet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import javax.persistence.EntityManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TweetServiceTest {
    private EntityManager entityManager;
    private MetricWriter metricWriter;
    private TweetService tweetService;

    @Before
    public void setUp() throws Exception {
        this.entityManager = mock(EntityManager.class);
        this.metricWriter = mock(MetricWriter.class);

        this.tweetService = new TweetService(entityManager, metricWriter);
    }

    @Test
    public void shouldInsertANewTweet() throws Exception {
        tweetService.publishTweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");

        verify(entityManager).persist(any(Tweet.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionWhenTweetLengthIsInvalid() throws Exception {
        tweetService.publishTweet("Pirate", "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");
    }

    @Test
    public void shouldModifyATweet() throws Exception {
        Tweet tweet = mock(Tweet.class);
        when(entityManager.find(any(), any())).thenReturn(tweet);
        tweetService.discardTweeet(1L);

        verify(entityManager).find(any(), any());
        verify(tweet).setDiscarded(true);
        verify(tweet).setDiscardedDate(any());
    }


    @Test(expected = UnkownTweetException.class)
    public void shouldThrowAnExceptionWhenNullId() throws Exception {
        tweetService.discardTweeet(null);
    }
}
