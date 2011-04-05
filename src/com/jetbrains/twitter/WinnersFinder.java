package com.jetbrains.twitter;


import twitter4j.*;

import java.util.*;

public class WinnersFinder {

    private static final String WHOSE_FOLLOWERS = "i_love_intellij";
    private static final String QUERY = "#i_love_intellij";
//    private static final String WHOSE_FOLLOWERS = "johnlindquist";
//    private static final String QUERY = "#FFK11";

    public static final int RPP_MAX = 100;
    public static final int MAX_EXCEPTIONS = 3;

    public static void main(final String[] args) {
        System.out.print("Progress: ");
        final Set<String> followers = getFollowerNames(WHOSE_FOLLOWERS);
        System.out.println();
        System.out.println("Followers found: " + followers.size());
        System.out.println();
        outputFollowers(followers);

        System.out.print("Progress: ");
        final Collection<Tweet> tweets = search(QUERY);
        System.out.println();
        System.out.println("Tweets found: " + tweets.size());
        System.out.println();

        final Map<String, Collection<Tweet>> followersToTweets = getFollowersToTweets(followers, tweets);
        System.out.println();

        outputFollowersToTweets(followersToTweets);
        outputFollowers(followersToTweets.keySet());
    }

    /**
     * @return sorted
     */
    public static Long[] getFollowerIds(final String user) {
        final Collection<Long> result = new LinkedList<Long>();

        try {
            final Twitter twitter = new TwitterFactory().getInstance();
            long cursor = -1;
            IDs ids;
            do {
                ids = twitter.getFollowersIDs(user, cursor);
                for (long id : ids.getIDs()) {
                    result.add(id);
                }
            } while ((cursor = ids.getNextCursor()) != 0);
        } catch (TwitterException te) {
            te.printStackTrace();
        }

        final Long[] sorted = result.toArray(new Long[result.size()]);
        Arrays.sort(sorted);
        return sorted;
    }

    public static Set<String> getFollowerNames(final String userToFollow) {
        final Set<String> result = new HashSet<String>();
        long cursor = -1;
        int exceptionCount = 0;

        do {
            try {
                final Twitter twitter = new TwitterFactory().getInstance();
                PagableResponseList<User> users;
                do {
                    users = twitter.getFollowersStatuses(userToFollow, cursor);
                    System.out.print('.');
                    for (User user : users) {
                        result.add(user.getScreenName());
                    }
                } while ((cursor = users.getNextCursor()) != 0);
            } catch (TwitterException te) {
                te.printStackTrace();
                if (++exceptionCount < MAX_EXCEPTIONS) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {/**/}
                }
            }
        } while (cursor != 0 && exceptionCount < MAX_EXCEPTIONS);

        return result;
    }

    public static Collection<Tweet> search(final String queryText) {
        final Collection<Tweet> result = new LinkedList<Tweet>();

        int currentPage = 1;
        int exceptionCount = 0;
        int lastResultSize = 0;

        do {
            try {
                final Twitter twitter = new TwitterFactory().getInstance();
                do {
                    final Query query = new Query(queryText);
                    query.setRpp(RPP_MAX);
                    query.setPage(currentPage);
                    final QueryResult queryResult = twitter.search(query);
                    currentPage++;
                    System.out.print('.');
                    final List<Tweet> tweets = queryResult.getTweets();
                    result.addAll(tweets);
                    lastResultSize = tweets.size();
                } while (lastResultSize == RPP_MAX);
            } catch (TwitterException te) {
                te.printStackTrace();
                if (++exceptionCount < MAX_EXCEPTIONS) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {/**/}
                }
            }
        } while (lastResultSize == RPP_MAX && exceptionCount < MAX_EXCEPTIONS);

        return result;
    }

    private static Map<String, Collection<Tweet>> getFollowersToTweets(final Set<String> followerNames, final Collection<Tweet> tweets) {
        final Map<String, Collection<Tweet>> followersToTweets = new HashMap<String, Collection<Tweet>>(150);

        for (Tweet tweet : tweets) {
            final String userName = tweet.getFromUser();
            Collection<Tweet> userTweets = followersToTweets.get(userName);
            if (userTweets == null && followerNames.contains(userName)) {
                userTweets = new LinkedList<Tweet>();
                followersToTweets.put(userName, userTweets);
            }
            if (userTweets != null) {
                userTweets.add(tweet);
            }
        }

        return followersToTweets;
    }

    private static void outputFollowersToTweets(final Map<String, Collection<Tweet>> followersToTweets) {
        for (Map.Entry<String, Collection<Tweet>> followerAndTweets : followersToTweets.entrySet()) {
            final String follower = followerAndTweets.getKey();
            final Collection<Tweet> tweets = followerAndTweets.getValue();
            System.out.println(follower);
            for (Tweet tweet : tweets) {
                System.out.println(tweet.getCreatedAt() + ": " + tweet.getText());
            }
            System.out.println();
        }
    }

    private static void outputFollowers(final Set<String> followers) {
        int i = 0;
        for (String follower : followers) {
            System.out.println(++i + " " + follower);
        }
        System.out.println();
    }
}
