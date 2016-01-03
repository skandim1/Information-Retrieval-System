package Indexing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class tokenIdentifier {
	List<String> ignoreAll;
	List<String> queryIndex;
	TreeMap<String, Dictionary> index;
	TreeMap<Integer,Integer> l;
	TreeMap<String,TreeMap<Integer,Integer>> plist; 
	HashMap<Integer, Integer> conversion;
	File file1,file2,file4;
	public static int count = 0;
	public static int wordCount = 0;
	public static String text = null;
	public static int totai_words_all_docs = 0;
	public tokenIdentifier() {
		queryIndex=new ArrayList<String>();
		file1 = new File("Dictionary.csv");
		file2 = new File("Postings.csv");
		file4 = new File("Total.csv");
		index = new TreeMap<String, Dictionary>();
		plist=new TreeMap<String,TreeMap<Integer,Integer>>(); 
		conversion = new HashMap<Integer, Integer>();
		ignoreAll= Arrays.asList("and","an", "by", "from", "of" , "the", "with", "a", "in");
	}
	public void indexProcessing(String str,int docId) {
		
		//2 converting to lower case
		str = str.toLowerCase();
		//1 deleting tags
		str = str.replaceAll("\\<.*?>", "");
		str = str.trim();
		
		//5				
		//6
		str = str.replace("(", "");
		str = str.replace("[", "");
		str = str.replace("\'", "");
		str = str.replace("`", "");
		str = str.replace("\"", "");
		//7
		str = str.replace(")", "");
		str = str.replace("]", "");
		//8
		str = str.replaceAll("[^\\dA-Za-z ]", "");
		if(str.endsWith(";")||str.endsWith(":")||str.endsWith("!")||str.endsWith("?")||str.endsWith(",")||str.endsWith(".")) {
			str = str.substring(0, str.length() - 1);
		}				
		//11
		if(str.endsWith("ies") && !str.endsWith("eies") && !str.endsWith("aies")) {
			str = str.substring(0, str.length() - 3) + "y";
		}
		else if(str.endsWith("es") && !str.endsWith("aes") && !str.endsWith("ees")&& !str.endsWith("oes")) {
			str = str.substring(0, str.length() - 2) + "e";
		}
		else if(str.endsWith("s") && !str.endsWith("us") && !str.endsWith("ss")) {
			str = str.substring(0, str.length() - 1);
		}
		str = str.replaceAll("[^\\dA-Za-z ]", "");
		//3 ignoring stop words
				if(ignoreAll.contains(str)){
		             return;
		        }
		//4	
		if(str.length() < 2) {
			return;
		}
		if(str.startsWith("$1")) {
			System.out.println(str);
		}
		wordCount++;
		if(index.containsKey(str)) {
			Dictionary dictObj = index.get(str);
			dictObj.cf++;
			if(plist.get(str).containsKey(docId)) {
				int x = plist.get(str).get(docId);
				x++;
				plist.get(str).put(docId, x);
			}
			else {
				plist.get(str).put(docId, 1);
				dictObj.df++;
			}
		}
		else {
			Dictionary dictObj = new Dictionary();
			dictObj.cf = 1;
			dictObj.df = 1;
			dictObj.offset = count++;
			index.put(str, dictObj);
			l = new TreeMap<Integer,Integer>();
			l.put(docId, 1);
			plist.put(str,l);
		}
	}
	public void setOffset() {
		int s,x = 0;
		for(Map.Entry<String, Dictionary> entry: index.entrySet()) {
			s = entry.getValue().offset;
			Dictionary d = entry.getValue();
			d.offset = x;
			x = d.offset + d.df;
			conversion.put(d.offset, s);
		}
	}
	public void writeToFile_Dictionary() {
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		try {
			fileWriter = new FileWriter(file1);
			bufferWriter = new BufferedWriter(fileWriter);
			for(Map.Entry<String, Dictionary> entry: index.entrySet()) {			
				bufferWriter.write(entry.getKey() + ","+entry.getValue().cf+","+entry.getValue().df+","+entry.getValue().offset + "\n");
			}			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferWriter != null && fileWriter!= null) {
				try {
					bufferWriter.close();
					fileWriter.close();		
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
			}
		}
	public void writeToFile_PostingsList() {
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		try {
			fileWriter = new FileWriter(file2);
			bufferWriter = new BufferedWriter(fileWriter);
			for(Map.Entry<String, TreeMap<Integer, Integer>> entry1: plist.entrySet()) {
				for(Map.Entry<Integer,Integer> entry2: entry1.getValue().entrySet()) {
					bufferWriter.write(entry2.getKey() + ","+entry2.getValue()+"\n");
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferWriter != null && fileWriter!= null) {
				try {
					bufferWriter.close();
					fileWriter.close();		
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
			}
		}
	public void writeToFile_Total() {
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		int sum = 0;
		try {
			fileWriter = new FileWriter(file4);
			bufferWriter = new BufferedWriter(fileWriter);
			for(Map.Entry<String, Dictionary> entry: index.entrySet()) {			
				sum += entry.getValue().cf;
			}	
				bufferWriter.write("Total Number of words : " + sum);
				totai_words_all_docs = sum;
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferWriter != null && fileWriter!= null) {
				try {
					bufferWriter.close();
					fileWriter.close();		
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
			}
		}

	public void queryProcessing(String str) {
		str = str.toLowerCase();
		//1 deleting tags
		str = str.replaceAll("\\<.*?>", "");
		str = str.trim();
		
		//5				
		//6
		//str = str.replaceAll("\\p{P}","");
		str = str.replace("(", "");
		str = str.replace("[", "");
		str = str.replace("\'", "");
		str = str.replace("`", "");
		str = str.replace("\"", "");
		//7
		str = str.replace(")", "");
		str = str.replace("]", "");
		//8
		
		str = str.replaceAll("[^\\dA-Za-z ]", "");
		
		
		if(str.endsWith(";")||str.endsWith(":")||str.endsWith("!")||str.endsWith("?")||str.endsWith(",")||str.endsWith(".")) {
			str = str.substring(0, str.length() - 1);
		}				

		//11
		if(str.endsWith("ies") && !str.endsWith("eies") && !str.endsWith("aies")) {
			str = str.substring(0, str.length() - 3) + "y";
		}
		else if(str.endsWith("es") && !str.endsWith("aes") && !str.endsWith("ees")&& !str.endsWith("oes")) {
			str = str.substring(0, str.length() - 2) + "e";
		}
		else if(str.endsWith("s") && !str.endsWith("us") && !str.endsWith("ss")) {
			str = str.substring(0, str.length() - 1);
		}
		//3 ignoring stop words
				if(ignoreAll.contains(str)){
		             return;
		        }
		//4
		if(str.length() < 2) {
			return;
		}
		if(!queryIndex.contains(str))
		 queryIndex.add(str);
		
	}
	public void CalculateWeight() {
		
	}
}
