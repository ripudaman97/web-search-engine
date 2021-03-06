package autocorrect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;



public class Autocorrect {

	public static int editDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
	 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
	}
	
	
	public static void testAutocorrect() {


    	
		File file = new File("Datasource/Accessibility - W3C.txt");
    	
		Hashtable<String, Integer> wordHashTable = new Hashtable<String, Integer>();
    	
		try {
			String fileContent = new String(Files.readAllBytes(file.toPath()));
			
			String[] fileWords = fileContent.split("\\s");
			
			for (int i = 0 ; i < fileWords.length; i ++) {
				
				fileWords[i] = fileWords[i].replaceAll("[^a-zA-Z]", "");
				
				if(fileWords[i].length() > 10 || fileWords[i].length() < 4) {
					continue;
				}
				String str = fileWords[i].toLowerCase();
				
				Integer wrdCnt = wordHashTable.get(str);
				if(wrdCnt != null){
					wordHashTable.put(str, wrdCnt + 1);
				}else {
					wordHashTable.put(str, 1);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		String[] inputWords = {"hello", "web", "html"};
		
		
		for (int i = 0 ; i < inputWords.length; i ++) {
			
			String str = inputWords[i];

			PriorityQueue<Map.Entry<String, Integer>> editDistanceDict = new PriorityQueue<>(Map.Entry.comparingByValue()); 
			
			wordHashTable.forEach((String word, Integer key) -> {
				editDistanceDict.offer(new AbstractMap.SimpleEntry<>(word, editDistance(str, word)));
			});
					
			int score = 0;
	   		System.out.println("\n--------------\n");
			
			System.out.println("Top Ten Least Edit Distance words for input word '" + str + "' are: \n" );
			
			System.out.println("Word, Edit distance");
			
			
			for(int i1 = 0 ; i1 < 10 ; i1 ++) {
				System.out.println(editDistanceDict.poll());
			}
		}    	

    	
	}
	
	
	public static HashMap getDictionary() throws IOException {
		
		FileInputStream fileIn = new FileInputStream("crunchify.ser");
		HashMap<String, HashMap<String, Integer>> dictionary = new HashMap();
        try (ObjectInputStream in = new ObjectInputStream(fileIn)) {
        	dictionary = (HashMap<String, HashMap<String, Integer>>) in.readObject();
//			System.out.print(dictionary.toString());
			return dictionary;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        return dictionary;
	}
	

	public static String autocorrectedWord(HashMap<String, HashMap<String, Integer>> wordHashTable, String word) throws IOException {
		String str = word; 

		PriorityQueue<Map.Entry<String, Integer>> editDistanceDict = new PriorityQueue<>(Map.Entry.comparingByValue()); 
		
		for (String key: wordHashTable.keySet()) {
			editDistanceDict.offer(new AbstractMap.SimpleEntry<>(key, editDistance(str, key)));
		}
		return editDistanceDict.poll().getKey();
	}
	
	
	
	public static void main(String[] args) throws IOException {
		HashMap<String, HashMap<String, Integer>> wordHashTable = getDictionary();

		System.out.println("finding nearest word for");
		String nearest_word = autocorrectedWord(wordHashTable, "help");
		System.out.println(nearest_word);
		
		System.out.println("Search Result  ii- >");
		Set<String> result_keys = wordHashTable.get(nearest_word).keySet();
		List<String> list = new ArrayList<>(result_keys);
		Collections.reverse(list);

		System.out.println(list);		
	}
}







