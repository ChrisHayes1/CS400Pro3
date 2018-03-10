import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class PerformanceAnalysisHash implements PerformanceAnalysis {

	/************************
     *Constants
     ***********************/
	private HashTable table; //hash table to be compared to TreeMap
	private TreeMap treemap; //TreeMap
    private ArrayList<String> inputData;//stores input from file
    private File details; //details file, which lists other files
    private BufferedReader br; //buffered reader for details file
    private String output; //report to be printed
    
    /************************
     *Constructors
     ***********************/
    public PerformanceAnalysisHash(){
    	output = "------------------------------------------------------------------------------------------------\r\n" +
    			"									Performance Analysis Report\r\n" + 
    			"------------------------------------------------------------------------------------------------\r\n" +
    			"|            FileName|      Operation| Data Structure|   Time Taken (micro sec)|     Bytes Used|\r\n" +
    			"------------------------------------------------------------------------------------------------";
    }
    public PerformanceAnalysisHash(String details_filename) {
    	this();
    	details = new File(details_filename);
    	try {
        br = new BufferedReader(new FileReader(details));
        br.readLine(); // discard first line of file
        br.close();
    	}catch (IOException e) {}
    }
    @Override
    public void compareDataStructures() {
    	try{
    		String currFile = br.readLine().split(",")[0];
	        while(currFile != null) {
	        	loadData(currFile);
	        	compareInsertion();
	        	compareSearch();
	        	compareDeletion();
	        	currFile = br.readLine().split(",")[0];
	        }
    	}catch(IOException e) {}
    }

    @Override
    public void printReport() {
        File results = new File("results.txt");
        
        try {
        BufferedWriter bw = new BufferedWriter(new FileWriter(results));
        bw.write(output);
        }catch(IOException e) {}
    }

    @Override
    public void compareInsertion() {
    	long startTime, time, mem; //time and memory value storage variables
    	startTime = System.currentTimeMillis();
        for(Object x : inputData) {
        	table.put(x, x);
        }
        time = System.currentTimeMillis() - startTime;
        //TODO: finish this method
    }

    @Override
    public void compareDeletion() {
        //TODO: Complete this method
    }

    @Override
    public void compareSearch() {
        //TODO: Complete this method
    }

    /*
    An implementation of loading files into local data structure is provided to you
    Please feel free to make any changes if required as per your implementation.
    However, this function can be used as is.
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
}
