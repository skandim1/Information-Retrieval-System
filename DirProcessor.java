package Indexing;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirProcessor {
	private tokenIdentifier tokenObj = null;
	private int DocId = 0;
	TreeMap<Integer, DocsTable> doctable;
	List<Output> listOutput;
	//TreeMap<Integer, Output> output;
	File file3;
	public DirProcessor(tokenIdentifier tokenObjIn) {
		tokenObj = tokenObjIn;
		doctable = new TreeMap<Integer, DocsTable>();
		//output = new TreeMap<Integer, Output>();
		listOutput = new ArrayList<Output>();
		file3 = new File("DocsTable.csv");
	}
	public void dirProcessing(File directory) {
		String path;
		for(File fileEntry : directory.listFiles()) {
			if(fileEntry.isDirectory()) {
				dirProcessing(fileEntry);
			} else {
				tokenIdentifier.wordCount = 0;
				tokenIdentifier.text = "";
				path = fileEntry.getPath();
				try {
					DocId++;
					Scanner input = new Scanner(fileEntry);
					String line,temp;	
					while(input.hasNextLine()) {
							line = input.nextLine();
							temp = line;
							temp = temp.replaceAll(",", "");
							tokenIdentifier.text = tokenIdentifier.text + temp.trim() + " ";
							String[] splited = line.split("\\s");
							for(String str : splited) {
							if(str.contains("-")) {
								Pattern pattern = Pattern.compile("-");
								String[] split = pattern.split(str);
								for(String element : split) {
									tokenObj.indexProcessing(element,DocId);
									if(element == "and") {
										break;
									}
								}
							}
							else
								tokenObj.indexProcessing(str,DocId);		
						   }
					}
					    input.close();			
				   } 
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				DocsTable d = new DocsTable();
				 d.no_words_doc = tokenIdentifier.wordCount;
				//new changes
				 d.filepath = path;
				final Pattern pattern = Pattern.compile("<TEXT>(.*)</TEXT>",Pattern.MULTILINE);
				final Matcher m = pattern.matcher(tokenIdentifier.text);
			    while (m.find())
			    {
			    	int i = 0;
			    	d.snippet = "";
			    	String[] s = m.group(1).split("\\.\\s");
			    	while((d.snippet.split("\\s+").length < 40)&&(i < s.length)) {
			    		s[i] = s[i].replaceAll("\\<.*?>", "");
			    		
					      d.snippet += s[i].trim();
					      i++;
			    	}
//			    	s[0] = s[0].replaceAll("\\<.*?>", "");
//				      d.snippet = s[0].trim();
			    }
				final Pattern pattern2 = Pattern.compile("<HEADLINE>(.*)</HEADLINE>",Pattern.MULTILINE);
				final Matcher m2 = pattern2.matcher(tokenIdentifier.text);
			    while (m2.find())
			    {	
			    	d.headline = m2.group(1).trim();
			    }		    
			   doctable.put(DocId, d);    
			}
		}
}
	//private static int count = 0;
	public void queryFetch() {
		
			Scanner queryinput = new Scanner(System.in);			
			String line;	
			System.out.println("Enter Query :");
			
			while(!(line = queryinput.nextLine()).equals("exit")) {
				//System.out.println(str);
				
				tokenObj.queryIndex.clear();
				String[] splited = line.split("\\s");
				for(String str : splited) {
				if(str.contains("-")) {
					Pattern pattern = Pattern.compile("-");
					String[] split = pattern.split(str);
					for(String element : split) {
						tokenObj.queryProcessing(element);
						if(element == "and") {
							break;
						}
					}
				}
				else
					tokenObj.queryProcessing(str);
				}
				simScore();
				writeToFile_Output(line);
				System.out.println("Enter Query :");
			}
			//tokenObj.writeToFile();
			queryinput.close();			

	}
	
	
	
	//new changes
	public void simScore() {
		listOutput.clear();
		double similarity;
		int total_words_in_doc;
		int totalwords = tokenIdentifier.totai_words_all_docs;
//		System.out.println(totalwords);
		int flag,flag2 = 0,count = 0;
		for(Map.Entry<Integer, DocsTable> entry: doctable.entrySet()) {
			similarity = 0;
			total_words_in_doc = entry.getValue().no_words_doc;
			flag = 0;
			for(String token: tokenObj.queryIndex) {
				if(tokenObj.plist.containsKey(token)) {
					
					if(tokenObj.plist.get(token).containsKey(entry.getKey())) {
						
						flag = 1;
						similarity += (Math.log10((0.9)*((double)tokenObj.plist.get(token).get(entry.getKey())/total_words_in_doc) + (0.1)*((double)tokenObj.index.get(token).cf/totalwords)))/(Math.log10(2)); 
					}
					else {
						similarity += (Math.log10((0.1)*((double)(tokenObj.index.get(token).cf)/totalwords)))/(Math.log10(2));
					}
				}				
			}
			if(flag == 1) {
				count++;
				Output o = new Output();
				o.docId = entry.getKey();
				o.filepath = entry.getValue().filepath;
				o.headline = entry.getValue().headline;
				o.snippet = entry.getValue().snippet;
				o.no_words_doc = entry.getValue().no_words_doc;
				o.similarity = similarity;
				listOutput.add(o);
				flag2 = 1;
				//System.out.println(entry.getValue().filepath);
			}	
		}
		//System.out.println("count " + count);
		if(flag2 == 0) 
			System.out.println("NO RESULT");
	}
	
	public void writeToFile_Output(String query) {
		File outputfile = new File("output.txt");
		int flag = 0;
		if(!outputfile.exists()){
	        try {
				outputfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		try {
			fileWriter = new FileWriter(outputfile,true);
			bufferWriter = new BufferedWriter(fileWriter);
			bufferWriter.write("The query is "+"\""+query+"\""+"\n");
			Collections.sort(listOutput, new OutputComparator(
	                new OutputSmilarityComparator()).reversed()
	        );
			for(Output entry: listOutput) {
				flag = 1;
				bufferWriter.write("The document is "+"\""+entry.filepath+"\""+"\n");
				bufferWriter.write("The headline is "+"\""+entry.headline+"\""+"\n");
				bufferWriter.write("The snippet is "+"\""+entry.snippet+"\""+"\n");
				bufferWriter.write("The similarity is "+"\""+entry.similarity+"\""+"\n");
				System.out.println(entry.filepath);
			}	
			if(flag == 0)
				bufferWriter.write("NO RESULT \n");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bufferWriter != null && fileWriter!= null) {
				try {
					bufferWriter.close();
					fileWriter.close();		
				} catch (IOException e) {
					e.printStackTrace();
				}}}
		}
	
	public void writeToFile_DocsTable() {
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		try {
			fileWriter = new FileWriter(file3);
			bufferWriter = new BufferedWriter(fileWriter);
			for(Map.Entry<Integer, DocsTable> entry: doctable.entrySet()) {
				bufferWriter.write(entry.getValue().filepath+","+entry.getKey() + ","+entry.getValue().headline+","+entry.getValue().snippet+","+"\n");
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
				}}}
		}
	
}
