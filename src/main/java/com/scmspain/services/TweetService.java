package com.scmspain.services;

import com.scmspain.entities.Tweet;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TweetService {
    private EntityManager entityManager;
    private MetricWriter metricWriter;

    public TweetService(EntityManager entityManager, MetricWriter metricWriter) {
        this.entityManager = entityManager;
        this.metricWriter = metricWriter;
    }

    /**
      Push tweet to repository
      Parameter - publisher - creator of the Tweet
      Parameter - text - Content of the Tweet
      Result - recovered Tweet
    */
    public void publishTweet(String publisher, String text) {
        if (publisher != null && publisher.length() > 0 && text != null && text.length() > 0 && text.length() < 140) {
            Tweet tweet = new Tweet();
            tweet.setTweet(text);
            tweet.setPublisher(publisher);
            tweet.setPublicationDate(new Date());
            this.metricWriter.increment(new Delta<Number>("published-tweets", 1));
            this.entityManager.persist(tweet);
        } else {
            throw new IllegalArgumentException("Tweet must not be greater than 140 characters");
        }
    }

    /**
      Recover tweet from repository
      Parameter - id - id of the Tweet to retrieve
      Result - retrieved Tweet
    */
    public Tweet getTweet(Long id) {
      return this.entityManager.find(Tweet.class, id);
    }

    /**
      Recover tweet from repository
      Parameter - id - id of the Tweet to retrieve
      Result - retrieved Tweet
    */
    public List<Tweet> listAllTweets() {
        List<Tweet> result = new ArrayList<Tweet>();
        this.metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        TypedQuery<Long> query = this.entityManager
                .createQuery("SELECT id FROM Tweet AS tweetId WHERE pre2015MigrationStatus<>99 and discarded=false ORDER BY publicationDate DESC", Long.class);
        // No acabo de entender porque se recuperan los TWEETS de uno en uno. Esto ejecuta n+1 queries siendo n
        // el número de tweets. Obviamente, también se deberían de limitar el número de tweets a recuperar.
        // Bajo mi punto de vista se debería de refactorizar y recuperar todos los Tweets de una sola vez, tal
        // y como hago en el método listDiscardedTweets.
        List<Long> ids = query.getResultList();
        for (Long id : ids) {
            result.add(getTweet(id));
        }
        return result;
    }

    public List<Tweet> listDiscardedTweets() {
        this.metricWriter.increment(new Delta<Number>("times-queried-discardedTweets", 1));
        try {
            TypedQuery<Tweet> query = this.entityManager
                    .createQuery("SELECT t FROM Tweet t WHERE t.pre2015MigrationStatus<>99 and t.discarded=true ORDER BY t.discardedDate DESC", Tweet.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw (RuntimeException) e;
        }

    }

    public void discardTweeet(Long id) {
        if (id == null) {
            throw new UnkownTweetException(id);
        }
        Tweet tweet = entityManager.find(Tweet.class, id);
        if(tweet == null) {
            throw new UnkownTweetException(id);
        }
        this.metricWriter.increment(new Delta<Number>("discarded-tweets", 1));
        tweet.setDiscardedDate(new Date());
        tweet.setDiscarded(true);
    }
}
