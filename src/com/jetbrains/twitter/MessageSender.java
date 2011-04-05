package com.jetbrains.twitter;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class MessageSender {

    private static final String CONSUMER_KEY = "9VP4jVWri9IHOnP7LrZbg";
    private static final String CONSUMER_SECRET = "DhjatrcCPmxzlmQ6unDOfCxZ3yzTk8VNgdoByIGWzA";
    private static final String TOKEN = "274585227-Tdl2pyG2qBS2tjTKuq07HiEFV9AaYpPUPMSy3ryP";
    private static final String TOKEN_SECRET = "ycPIjixnogPU6wsZZWK1uaknMIBduSE5AqWaX2TKo";

    public static void main(final String[] args) {
        final Configuration config = new ConfigurationBuilder()
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(TOKEN)
                .setOAuthAccessTokenSecret(TOKEN_SECRET)
                .build();
        final Twitter twitter = new TwitterFactory(config).getInstance();

        for (String arg : args) {
            sendDirectMessage(twitter, "alexander_812",
                    "Thanks for loving IntelliJ IDEA! Your FREE Personal license is waiting at http://www.jetbrains.com/idea/ffk11/?" + arg);
        }
    }

    public static void sendDirectMessage(final Twitter twitter, final String recipient, final String text) {
        try {
            final DirectMessage message = twitter.sendDirectMessage(recipient, text);
            System.out.println("Direct message successfully sent to " + message.getRecipientScreenName());
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to send a direct message: " + te.getMessage());
            System.exit(-1);
        }

    }
}
