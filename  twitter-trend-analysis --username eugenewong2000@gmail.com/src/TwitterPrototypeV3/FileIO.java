package TwitterPrototypeV3;

//*********************************NEW*FEATURE****************************
//**ADDING*AI*CHECKING*THE*NEGATIVE*SUCH*AS*NOT*ISN'T***********************
//**ADDING*THE*EMOTICON*TO*FEELING*CHECKLIST********************************
//************************************************************************//

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FileIO
{
  public static void main(String[] args) throws IOException 
  {
	  // retrieve.
	  new twitter().retrieve();
	  
      ArrayList tweetList = loadTweet("tweets.txt");//loading tweets
      //extract the keyword and score from keyword list
      //Useful arrays and arraylists include: tweetList, scoreArrayKeyword, emoticonList, scoreArrayEmoticon.
      ArrayList scoreListKeyword = new ArrayList();
      ArrayList keywordList=loadKeyWord("EmotionLookupTable.txt",scoreListKeyword);
      int[] scoreArrayKeyword=new int[scoreListKeyword.size()];
      for(int i=0;i<scoreListKeyword.size();i++){
      	scoreArrayKeyword[i]=(Integer) scoreListKeyword.get(i);
      }
      
      ArrayList scoreListEoticon=new ArrayList();
      ArrayList emoticonList=loadEmoticon("EmoticonLookupTable.txt",scoreListEoticon);
      int [] scoreArrayEmoticon=new int[scoreListEoticon.size()];
      for(int i=0;i<scoreListEoticon.size();i++){
      	scoreArrayEmoticon[i]=(Integer) scoreListEoticon.get(i);
      }
      
      ArrayList NegwordList = loadNegwordList("NegatingWordList.txt");
      //Construct conjection list
      ArrayList ConjectionWordList=new ArrayList();
      ConjectionWordList.add(".");
      ConjectionWordList.add( ",");
      ConjectionWordList.add( "for");
      ConjectionWordList.add( "and");
      ConjectionWordList.add("nor" );
      ConjectionWordList.add("but" );
      ConjectionWordList.add( "or");
      ConjectionWordList.add( "yet");
      ConjectionWordList.add("so" );
      
      compareAllTweets(tweetList, keywordList, scoreArrayKeyword, emoticonList, scoreArrayEmoticon, NegwordList , ConjectionWordList);
      
  }
  
  
  
  public static ArrayList loadTweet(String fileName)
  {
      if ((fileName == null) || (fileName == ""))
          throw new IllegalArgumentException();
      
      String line;
      ArrayList file = new ArrayList();
      try
      {    
          BufferedReader in = new BufferedReader(new FileReader(fileName));
          if (!in.ready())
              throw new IOException();
          while ((line = in.readLine()) != null) 
              file.add(line);
          in.close();
      }
      catch (IOException e)
      {
          System.out.println(e);
          return null;
      }
      return file;
  }
  
  public static ArrayList loadKeyWord(String fileName, ArrayList scoreList)
  {
      if ((fileName == null) || (fileName == ""))
          throw new IllegalArgumentException();
      
      char singleChar;
      String word="";
      boolean wordNow=true;
      ArrayList wordList = new ArrayList();
      
      try
      {    
          BufferedReader in = new BufferedReader(new FileReader(fileName));
          if (!in.ready())
              throw new IOException();
          
          
          ////////////
          boolean isNum=false;
          word = "";
          int tempi=0;
          while (((tempi =  in.read())) != -1){
          	singleChar=(char)tempi;
          	          		
          	 if(singleChar == '\t'){
          		isNum=true;
          	}
          	else if(
          			isNum && 
          			(Character.isDigit(singleChar) || singleChar == '-')){
          		
          		String intTemp = ""+singleChar;
          		singleChar = (char)in.read();
          		
          		while(Character.isDigit(singleChar)){
          			intTemp+=singleChar;
          			singleChar = (char)in.read();
          		}
          		int theScore=Integer.parseInt(intTemp);
          		in.readLine();
          		isNum=false;
          		wordList.add(word);
          		scoreList.add(theScore);
          		word="";
          	}
          	
          	
          	 if((singleChar!='*' && singleChar!='\t' && singleChar!='-') 
          			 &&  !isNum
          			 ){
          	 	word =word+ singleChar;
          	 	
          	 }
          }
        
          	in.close();
      }
      
      
      catch (IOException e)
      {	System.out.println("ERROR!");
          System.out.println(e);
          return null;
      }
      return wordList;
  }
  
  public static ArrayList loadEmoticon(String fileName, ArrayList scoreList)
  {
      if ((fileName == null) || (fileName == ""))
          throw new IllegalArgumentException();
      
      int singleChar;
      ArrayList emoticonList = new ArrayList();
      try
      {    
          BufferedReader in = new BufferedReader(new FileReader(fileName));
          if (!in.ready())
              throw new IOException();
          String emoticon="";
          int score=0;
          String stringScore="";
          boolean nextNum=false;
          while ((singleChar = in.read()) != -1){
          	
          	if((char)singleChar=='\t')
          		nextNum=true;
          	else if ((char)singleChar=='\n'){
          		nextNum=false;
          		emoticonList.add(emoticon);
          		String subString = stringScore.substring(0, stringScore.length()-0);
          		score=Integer.parseInt(stringScore);
          		//System.out.println(subString);
          		scoreList.add(score);	
          		emoticon="";
          		stringScore="";
          		
          		
          	}
          	else if (singleChar!= 13){	//solve the unknown problem of a ASCII=13 char following \n
          	
          	if(!nextNum)
          	emoticon+=(char)singleChar;
          	else
          		stringScore+=(char)singleChar;
          	//System.out.print((char)singleChar+"["+singleChar+"]");
          	
          	}
          }

          in.close();
      }
      catch (IOException e)
      {
          System.out.println(e);
          return null;
      }
      return emoticonList;
  }
  
  public static int compareKeyWord(String tweet, ArrayList keywordList, int[] scoreList, ArrayList NegwordList ,ArrayList ConjectionWordList){	
  	String delims = "[ ]+";
  	String[] tokens = tweet.split(delims);
  	int score=0;
  	//int[] ConjectionWordIndex=new int[tokens.length];//
  	int lastConjectionWordIndex=0;
  	//int countCW=0;
  	boolean negwordExist=false;
  	int lastNegwordIndex=0;
  	for(int i=0;i<tokens.length;i++){
  		String tweetWord=tokens[i];
  		
  		//check if there are Negword and keep it
  		for(int k = 0; k < NegwordList.size(); k++){
  			if(tweetWord.equals(NegwordList.get(k))){
  				negwordExist=!negwordExist;
  				lastNegwordIndex=i;
  			}
  		}
  		
  		for(int k = 0; k < ConjectionWordList.size(); k++){
  			if(tweetWord.equals(ConjectionWordList.get(k))){
  				lastConjectionWordIndex= i;
  				k=ConjectionWordList.size();
  				//countCW++;
  			}
  		}
  		
  		//search if keyword matches
  		for(int j=0;j<keywordList.size();j++){
  			if(tweetWord.equals(keywordList.get(j))){
  				if(negwordExist){
  					boolean conjectionBetweenNWAndKeyword=false;
  					//for(int z=0; z<countCW; z++){
  						if(lastConjectionWordIndex<i && lastConjectionWordIndex>lastNegwordIndex){
  							//z=countCW;
  							//System.out.println("!!!DEBUG!!!"+tweet+" enter "+conjectionBetweenNWAndKeyword);
  							score=score+scoreList[j];
  							conjectionBetweenNWAndKeyword=true;						
  						}
  					//}
  					
  					if(!conjectionBetweenNWAndKeyword){
  					score=score-scoreList[j];
  					negwordExist=false;
  					}
  					}
  				
  				else{
  					score=score+scoreList[j];
  					}
  				
  				j=keywordList.size();
  				//System.out.println("-keyword: "+keywordList.get(j)+" -score"+score);
  			}
  		}
  	}	
  	return score;
  }
 
  
  public static int compareEmoticon(String tweet, ArrayList emoticonList, int[] scoreList){
  		int totalScore=0;
  		for(int j = 0; j < emoticonList.size(); j++){
  			int listIndex = tweet.indexOf((String) emoticonList.get(j));
  			if(listIndex != -1){
  				totalScore += scoreList[j];
  				
  			}
  		}
 
  	return totalScore;
  }
  
  public static ArrayList loadNegwordList(String fileName){
  	
  	  if ((fileName == null) || (fileName == ""))
            throw new IllegalArgumentException();
        
        String line;
        ArrayList file = new ArrayList();
        try
        {    
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            if (!in.ready())
                throw new IOException();
            while ((line = in.readLine()) != null) 
                file.add(line);
            in.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
            return null;
        }
        return file;
  	
  }
  
  public static void compareAllTweets(ArrayList tweetList,ArrayList keywordList, int[] scoreArraykeyword, ArrayList emoticonList, int[] scoreArrayEmoticon,  ArrayList NegwordList ,ArrayList ConjectionWordList){
  	
  	int countNumHaveScore=0;
  	int overallScore=0;
  	for(int i=0;i<tweetList.size();i++){
  		String ts=(String) tweetList.get(i);
  		int tweetScoreKeyword=compareKeyWord(ts,keywordList,scoreArraykeyword, NegwordList , ConjectionWordList);
  		int emoticonScore=compareEmoticon(ts, emoticonList, scoreArrayEmoticon);
  		if(emoticonScore!=0 || tweetScoreKeyword!=0)countNumHaveScore++;
  		System.out.println("Tweet:	"+ts);
  		System.out.println("Score due to keyword:	"+tweetScoreKeyword);
  		System.out.println("Score due to emoticon:	"+emoticonScore);
  		System.out.println("..............................................");
  		overallScore += (tweetScoreKeyword+emoticonScore);
  		
  	}
  	System.out.println("-------------------------------------------");
		System.out.println("Total No. of Tweets: "+tweetList.size());
		System.out.println("NO. of Tweets with non-zero score: "+countNumHaveScore);
		System.out.println("Overall feeling score: "+overallScore);
  }
  
  
}