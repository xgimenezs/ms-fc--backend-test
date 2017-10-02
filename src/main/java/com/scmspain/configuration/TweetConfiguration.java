package com.scmspain.configuration;

import com.scmspain.controller.TweetController;
import com.scmspain.controller.command.PublishTweetCommand;
import com.scmspain.controller.validator.FastTweetLengthCalculator;
import com.scmspain.controller.validator.ICommandValidator;
import com.scmspain.controller.validator.ITweetLengthCalculator;
import com.scmspain.controller.validator.PublishTweetCommandValidator;
import com.scmspain.services.TweetService;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class TweetConfiguration {
    @Bean
    public TweetService getTweetService(EntityManager entityManager, MetricWriter metricWriter) {
        return new TweetService(entityManager, metricWriter);
    }

    @Bean
    public TweetController getTweetConfiguration(TweetService tweetService) {
        return new TweetController(tweetService);
    }

    @Bean
    public ICommandValidator<PublishTweetCommand> getPublishTweetCommandValidator(TweetService tweetService) {
        return new PublishTweetCommandValidator();
    }

    @Bean
    public ITweetLengthCalculator getFastTweetLengthCalculator() {
        return new FastTweetLengthCalculator();
    }
}
