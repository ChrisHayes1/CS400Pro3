import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

///////////////////////////////////////////////////////////////////////////////////////////////////
//CS 400 Programming Assignment 3: Performance Analysis
//@author: Mostafa Hassan (mwhassan@wisc.edu) and Christopher Hayes (hayesbirchle@ctri.wisc.edu)
//@due date: 3/19/18
///////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Produces a report comparing the time and memory performance of the HashTable and TreeMap classes.
 */
public class PerformanceAnalysisHash implements PerformanceAnalysis {

	/************************
     *Constants
     ***********************/
	
	private HashTable<Object, Object> table; //hash table to be compared to TreeMap
	private TreeMap<Object, Object> treemap; //TreeMap
	
	 private File details; //details file, which lists other files
	
    private ArrayList<String> inputData;//stores input from file
    
    private String directory; //directory in which detail file is
    private String currFile; //current working file
    private String output; //report to be printed
    
    /************************
     *Constructors
     ***********************/
    
    /**
     * Initializes fields and loads details file.
     * @param details_filename
     * @throws IOException
     */
    public PerformanceAnalysisHash(String details_filename) throws IOException {
    	//initialize structures
    	table = new HashTable<Object, Object>();
    	treemap = new TreeMap<Object, Object>();
    	
    	//header
    	output = "------------------------------------------------------------------------------------------------\r\n" +
    			"									Performance Analysis Report\r\n" + 
    			"------------------------------------------------------------------------------------------------\r\n" +
    			"|\t\tFileName|\t\tOperation|\t\tData Structure|\t\tTime Taken (micro sec)|\t\tBytes Used|\r\n" +
    			"------------------------------------------------------------------------------------------------";
    	
    	//open details file and sets directory
    	details = new File(details_filename);
    	directory = details.getParent() + "/";
    	System.out.println(directory);
    }
    
    /************************
     *Public Interface
     ***********************/
    
    /**
     * Loads data from files specified in the details file into each data structure.
     * Compares insertion, searching, and deletion for each case.
     */
    @Override
    public void compareDataStructures() {
    	try{
    		BufferedReader br = new BufferedReader(new FileReader(details));
            br.readLine(); // discard first line of file
    		
    		String currLine = br.readLine();
    		if(currLine != null) currFile = currLine.split(",")[0]; //get file name
    		
	        while(currFile != null) {
	        	//load this file
	        	loadData(directory + currFile);
	        	
	        	//compare the structures
	        	compareInsertion();
	        	compareSearch();
	        	compareDeletion();
	        	
	        	//go to next file
	        	currLine = br.readLine();
	        	if(currLine != null) currFile = currLine.split(",")[0];
	        	else currFile = null;
	        }
	        br.close();
    	}catch(IOException e) {}
    }

    /**
     * Prints the comparison report.
     */
    @Override
    public void printReport() {
        File results = new File(directory + "results.txt");
        
        try {
        BufferedWriter bw = new BufferedWriter(new FileWriter(results));
        bw.write(output);
        bw.close();
        }catch(IOException e) {}
    }

    /**
     * Compares time and memory of inserting into the data structures.
     */
    @Override
    public void compareInsertion() {
    	long startTime, time, mem; //time and memory value storage variables
    	Runtime runtime = Runtime.getRuntime();
    	
    	//put into HashTable
    	startTime = System.nanoTime();
        for(Object x : inputData) {
        	table.put(x, x);
        }
        runtime.gc();
        mem = runtime.totalMemory() - runtime.freeMemory();
        time = (System.nanoTime() - startTime)/1000; //convert to microseconds
        
        print(currFile, "PUT", "HASHTABLE", time, mem);
        table = new HashTable<Object, Object>(); //reset HashTable
        
        //put into TreeMap
        startTime = System.nanoTime();
        for(Object x : inputData) {
        	treemap.put(x, x);
        }
        runtime.gc(); //should delete tree previously inserted into
        mem = runtime.totalMemory() - runtime.freeMemory();
        time = (System.nanoTime() - startTime)/1000; //convert to microseconds
        
        print(currFile, "PUT", "TREEMAP", time, mem);
        treemap = new TreeMap<Object, Object>(); //reset TreeMap
        
    }

    /**
     * Compares the time it takes to insert into the data structures.
     */
	@Override
    public void compareDeletion() {
		long startTime, time; //time value storage variables
    	
    	//put into HashTable
        for(Object x : inputData) {
        	table.put(x, x);
        }
        
        startTime = System.nanoTime();
        //remove from HashTable
        for(Object x : inputData) {
        	table.remove(x);
        }
        time = (System.nanoTime() - startTime)/1000; //convert to microseconds
        
        print(currFile, "REMOVE", "HASHTABLE", time, 0);
        table = new HashTable<Object, Object>(); //reset HashTable
        
        //put into TreeMap
        for(Object x : inputData) {
        	treemap.put(x, x);
        }
        
        startTime = System.nanoTime();
        //remove from TreeMap
        for(Object x : inputData) {
        	treemap.remove(x);
        }
        time = (System.nanoTime() - startTime)/1000; //convert to microseconds
        
        print(currFile, "REMOVE", "TREEMAP", time, 0);
        treemap = new TreeMap<Object, Object>(); //reset TreeMap
    }

	/**
	 * Compares the time it takes to search in each of the data structures.
	 */
    @Override
    public void compareSearch() {
    	long startTime, time; //time value storage variables
    	
    	//put into HashTable
        for(Object x : inputData) {
        	table.put(x, x);
        }
        
        startTime = System.nanoTime();
        //search for all elements in HashTable
        for(Object x : inputData) {
        	table.get(x);
        }
        time = (System.nanoTime() - startTime)/1000; //convert to microseconds
        
        print(currFile, "GET", "HASHTABLE", time, 0);
        table = new HashTable<Object, Object>(); //reset HashTable
        
        //put into TreeMap
        for(Object x : inputData) {
        	treemap.put(x, x);
        }
        
        startTime = System.nanoTime();
        //search for all elements in TreeMap
        for(Object x : inputData) {
        	treemap.get(x);
        }
        time = (System.nanoTime() - startTime)/1000; //convert to microseconds
        
        print(currFile, "GET", "TREEMAP", time, 0);
        treemap = new TreeMap<Object, Object>(); //reset TreeMap
    }

    /**
     * Loads a file to be used for testing.
     */
    @Override
    public void loadData(String filename) throws IOException {

        // Opens the given test file and stores the objects each line as a string
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        inputData = new ArrayList<>();
        String line = br.readLine();
        while (line != null) {
            inputData.add(line);
            line = br.readLine();
        }
        br.close();
    }
    
    /************************
     *Private Helpers
     ***********************/
    
    /**
     * Adds a line to the report.
     * @param file
     * @param operation
     * @param structure
     * @param time
     * @param mem
     */
    private void print(String file, String operation, String structure, long time, long mem) {
		output += "\n|\t\t" + file + "|\t\t" + operation + "|\t\t" + structure + "|\t\t" + time + "|\t\t" + mem + "|";
	}
}