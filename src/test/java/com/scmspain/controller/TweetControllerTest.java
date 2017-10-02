package com.scmspain.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scmspain.TweetsCleaner;
import com.scmspain.configuration.TestConfiguration;
import com.scmspain.entities.Tweet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class TweetControllerTest {

    @Autowired
    private TweetsCleaner tweetsCleaner;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(this.context).build();
        tweetsCleaner.clean();
    }

    @Test
    public void shouldReturn200WhenInsertingAValidTweet() throws Exception {
        mockMvc.perform(newTweet("Prospect", "Breaking the law"))
                .andExpect(status().is(201));
    }

    @Test
    public void shouldReturn400WhenInsertingAnInvalidTweet() throws Exception {
        mockMvc.perform(newTweet("Schibsted Spain", "We are Schibsted Spain (look at our home page http://www.schibsted.es/), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Welcome!"))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturnAllPublishedTweets() throws Exception {
        createTweet("Yo", "How are you?");

        MvcResult getResult = mockMvc.perform(get("/tweet"))
                .andExpect(status().is(200))
                .andReturn();

        String content = getResult.getResponse().getContentAsString();
        assertThat(new ObjectMapper().readValue(content, List.class).size()).isEqualTo(1);
    }

    @Test
    public void shouldReturnAllPublishedTweetsInOrder() throws Exception {
        createTweet("Yo", "Tweet1");
        createTweet("Yo", "Tweet2");

        List<Tweet> tweetList = getTweetList();
        assertThat(tweetList).isNotNull();
        assertThat(tweetList.size()).isEqualTo(2);
        assertThat(tweetList.get(0).getPublicationDate()).isAfter(tweetList.get(1).getPublicationDate());
    }

    @Test
    public void shouldReturn404WhenDiscardANonExistingTweet() throws Exception {
        mockMvc.perform(newDiscardTweet("1"))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldReturn404WhenDiscardAnInvalidId() throws Exception {
        mockMvc.perform(newDiscardTweet("xxx"))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldReturn200WhenDiscardAExistingTweet() throws Exception {
        Tweet tweet = createTweet("Prospect", "Breaking the law");
        assertThat(tweet.getId()).isNotNull();
        assertThat(tweet.getId()).isGreaterThan(0);

        mockMvc.perform(newDiscardTweet(String.valueOf(tweet.getId())))
                .andExpect(status().is(200));

    }

    @Test
    public void shouldReturnOnlyDiscardedTweetsInDiscardedDateOrder() throws Exception {
        int FIRST_DISCARD_IDX = 0;
        int SECOND_DISCARD_IDX = 4;
        int THIRD_DISCARD_IDX = 2;
        List<Tweet> tweets = asList(1, 2, 3, 4, 5).stream().map(x -> createTweet("yo", "tweet" + x)).collect(toList());
        discardTweets(tweets.get(FIRST_DISCARD_IDX).getId(),
                tweets.get(SECOND_DISCARD_IDX).getId(), tweets.get(THIRD_DISCARD_IDX).getId());
        // En este caso no puedo validar las fechas de descarte porque no son devueltas por el servicio rest ya
        // así se especificaba en la historia de usuario.
        // Este test funcionará en la medida que la base de datos devuelva un autonumérico incremental siempre.
        // Por ejemplo en un cluster de Oracle, podría llegar a fallar.
        List<Tweet> tweetList = getDiscardedTweetList();
        assertThat(tweetList.get(0).getId()).isEqualTo(tweets.get(THIRD_DISCARD_IDX).getId());
        assertThat(tweetList.get(1).getId()).isEqualTo(tweets.get(SECOND_DISCARD_IDX).getId());
        assertThat(tweetList.get(2).getId()).isEqualTo(tweets.get(FIRST_DISCARD_IDX).getId());

    }

    @Test
    public void shouldReturnOnlyNotDiscardedTweets() throws Exception {
        List<Tweet> tweets = asList(1, 2, 3, 4, 5).stream().map(x -> createTweet("yo", "tweet" + x)).collect(toList());
        discardTweets(tweets.get(0).getId(), tweets.get(4).getId(), tweets.get(2).getId());
        List<Tweet> tweetList = getTweetList();
        assertThat(tweetList).size().isEqualTo(2);
    }

    @Test
    public void ensureResponseNotChange() throws Exception {
        Tweet tweet = createTweet("Prospect", "Breaking the law");
        discardTweets(tweet.getId());
        MvcResult getResult = mockMvc.perform(get("/discarded"))
                .andExpect(status().is(200))
                .andReturn();

        String content = getResult.getResponse().getContentAsString();
        assertThat(content).contains("publisher");
        assertThat(content).doesNotContain("discarded");
        assertThat(content).doesNotContain("discardDate");
    }

    private void discardTweets(long ... ids) throws Exception {
        for (long id : ids) {
            mockMvc.perform(newDiscardTweet(String.valueOf(id)))
                    .andExpect(status().is(200));
        }
    }

    private Tweet createTweet(String publisher, String tweet) {
        try {
            mockMvc.perform(newTweet(publisher, tweet))
                    .andExpect(status().is(201));
            return getTweetList().get(0);
        } catch (Exception e) {
            // Para poder utilizar este método en las lambdas.
            throw new RuntimeException(e);
        }
    }

    private MockHttpServletRequestBuilder newTweet(String publisher, String tweet) {
        return post("/tweet")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"publisher\": \"%s\", \"tweet\": \"%s\"}", publisher, tweet));
    }

    private MockHttpServletRequestBuilder newDiscardTweet(String id) {
        return post("/discarded")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"tweet\": \"%s\"}", id));
    }

    private List<Tweet> getTweetList() throws Exception {
        return getList("/tweet");
    }

    private List<Tweet> getDiscardedTweetList() throws Exception {
        return getList("/discarded");
    }

    private List<Tweet> getList(String endpoint) throws Exception {
        MvcResult getResult = mockMvc.perform(get(endpoint))
                .andExpect(status().is(200))
                .andReturn();

        String content = getResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Tweet.class);
        return mapper.readValue(content, type);
    }
}