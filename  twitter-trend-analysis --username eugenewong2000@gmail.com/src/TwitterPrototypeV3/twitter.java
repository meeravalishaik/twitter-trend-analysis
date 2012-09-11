package TwitterPrototypeV3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Twitter application using Twitter4J
 */

public class twitter {  //this the file name of main class
    private final Logger logger = Logger.getLogger(twitter.class.getName());

    public static void main(String[] args) throws IOException {
        new twitter().retrieve();
    }

    /**
     * public void retrieve() 
     * Retrieves tweets, Prints log, Writes tweets to tweets.txt
     * @throws IOException
     */
    public void retrieve() throws IOException {
    	
    	BufferedWriter writer = null;
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	System.out.println("Search Keyword: ");
    	String keyWord = br.readLine();
    	
    	//String date="2012-01-01";
    	System.out.println("Tweets Since: ");
    	String date = br.readLine();
    	
    	//int num_tweets = 100;
    	System.out.println("Number of Tweets (Multiples of 100, Max pages~1500): ");
    	String num_tweetpage_str = br.readLine();
    	int num_tweetpage =  Integer.parseInt(num_tweetpage_str);
    	
        logger.info("Retrieving tweets...");
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query(""+keyWord);
        query.setRpp(100);
        query.setPage(num_tweetpage);
        query.setSince(date);
        query.setLang("en"); // Restrict tweets in English Language Encoding.
        //query.setUntil("2011-01-01");
        try {
	    	File tweetfile = new File("tweets.txt");
	    	writer = new BufferedWriter(new FileWriter(tweetfile));
	    	
            //int tweetpage_counter=1;
            
	    	//QueryResult result = twitter.search(query);
            
	    	int tweetcount=1;
	    	
            for(int page_counter=1 ; page_counter <= num_tweetpage ; page_counter++) {
                QueryResult result = twitter.search(query.page(page_counter)); //returns tweetpage as QueryResult.

                for (Tweet tweet : result.getTweets()) {
                    System.out.println( "\n" + tweetcount + " " + tweet.getText() );
                    
                    //Write tweets to tweets.txt
                    writer.write( tweetcount + " " + tweet.getText() );
                    writer.newLine();
                    ++tweetcount;
                }
            }
            
            System.out.println("\nCount : " + --tweetcount ) ;
            
           // System.out.println("Count : " + result.getTweets().size() ) ;
              	
	    	
        } catch (TwitterException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("done! ");
    }
}
