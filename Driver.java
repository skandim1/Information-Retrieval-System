package Indexing;

import java.io.*;

public class Driver {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File directory = new File(args[0]);
		tokenIdentifier tokenObj = new tokenIdentifier();
		DirProcessor dirObj = new DirProcessor(tokenObj);
		
		dirObj.dirProcessing(directory);
		//display content on console
		tokenObj.setOffset();
		tokenObj.writeToFile_Dictionary();
		tokenObj.writeToFile_PostingsList();
		dirObj.writeToFile_DocsTable();;
		tokenObj.writeToFile_Total();
		
		//Query Processing
		dirObj.queryFetch();
		//tokenObj.writeToFile_QueryTerms();
		//new changes
		//dirObj.simScore();
		
	}
	/*
	 * for every doc 
	 * docID ,score, snippet, ranking 
	 * for each word in queryindexing
	 * 	|D| value, |C| value, cf from postings list
	 *  for each posting list check 
	 *      //set flag and store tf;
	 *  if doc exist 
	 *  	product *= log(w(cf/|C|))	
	 *  else
	 *    just calculate similarity *= log(w(cf/|C|))
	 *  //get similarity score
	 *  print similarity
	 * 
	 * 
	 * 
	 */

}
