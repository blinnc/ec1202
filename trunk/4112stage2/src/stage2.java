import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Databases 4112 / Project 2 / Stage 2 Algorithm implementation
 * 
 * @author Sean Wang
 * @author Jeff Sinckler/jcs2137
 *
 */
public class stage2 {
	
	public static int arrayAccessCost;
	public static int ifTestCost;
	public static int logicalAndCost;
	public static int bMispredictCost;
	public static int arrayWriteCost;
	public static int funcApplyCost;
	public static int numFunctions;

	public static double[] getCostList(int setNumber, double[] selectivities)
	{
		/*switch(setNumber) {
		case 1:
			break;
		case 1:
			break;
		case 1:
			break;
		case 1:
			break;
		case 1:
			break;
		case 1:
			break;
		case 1:
			break;
		case 1:
			break;
		case 1:
			break;
		}*/
		return null;
	}
	
	public static ArrayList<BasicTermSet> getSetList(double[] selectivityArray) {
		int numTerms = selectivityArray.length;
		int numSets = (int)Math.pow(2, numTerms);
		boolean[][] bitmap = new boolean[numTerms][numSets];
		
		/* Fill bitmap */
		for(int i = 0; i < numTerms; i++) {
			int skip = (int)Math.pow(2, numTerms - i - 1);
			int count = skip;
			boolean fill = false;
			for(int j = 0; j < numSets; j++) {
				if(count == 0) {
					count = skip;
					fill = !fill;
				}
				bitmap[i][j] = fill;
				count--;				
			}
		}
		
		/* Fill setList */
		ArrayList<BasicTermSet> setList = new ArrayList<BasicTermSet>();
		
		for(int j = 1; j < numSets; j++) {
			ArrayList<Double> termList = new ArrayList<Double>();
			
			for(int i = 0; i < numTerms; i++) {
				if(bitmap[i][j]) {
					termList.add(new Double(selectivityArray[i]));
				}
			}
			
			double[] termArray = new double[termList.size()];
			
			for(int i = 0; i < termList.size(); i++) {
				termArray[i] = termList.get(i);
			}
			
			setList.add(new BasicTermSet(j, termArray));
		}
				
		return setList;
	}
	
	
	
	/**
	 * @param args - takes the location of the query file as the first argument and the location
	 * of the configuration file as the second query parameter
	 */
	public static void main(String[] args) {		
		//some helper data
		String queryData;
		
		//set up the properties file to read in the values for the algorithm
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(args[1]));
		} 
		catch (IOException e)
		{
			System.out.println("Error: File not found at specified location");
		}
		
		//read in the actual values from the configuration file
		arrayAccessCost = Integer.parseInt(properties.getProperty("r"));
		ifTestCost = Integer.parseInt(properties.getProperty("t"));
		logicalAndCost = Integer.parseInt(properties.getProperty("l"));
		bMispredictCost = Integer.parseInt(properties.getProperty("m"));
		arrayWriteCost = Integer.parseInt(properties.getProperty("a"));
		funcApplyCost = Integer.parseInt(properties.getProperty("f"));
		
		//Read in the query file
		ArrayList<String> queryList = new ArrayList<String>();
		try {
			//use buffering, reading one line at a time
			BufferedReader input =  new BufferedReader(new FileReader(args[0]));
			try {
				String line = null; //not declared within while loop
				while (( line = input.readLine()) != null){
		        	queryList.add(line);
		        }
			}
			finally {
		        input.close();
			}
    	}
	    catch (IOException ex){
	        ex.printStackTrace();
	    }
	    
	    //get the first set of selectivities from the query file
	    for(String query : queryList) {
		    
		    //write the selectivity values from the query into an array of doubles
		    String[] selectivityStrings = query.split(" ");
		    double[] selectivities = new double[selectivityStrings.length];
		    for(int i = 0; i < selectivityStrings.length; i++) {
		    	selectivities[i] = Double.parseDouble(selectivityStrings[i]);
		    }
		    
		    //loop that iterates over the selectivities and creates an extensive set. Needs work right now
		    //TODO: Make this loop work...
		    
		    ArrayList<BasicTermSet> basicTermSets = getSetList(selectivities);
		    
		    //iterate through the list of sets previously created and keep the cheapest cost
		    for(int i = 0; i < basicTermSets.size(); i++)
		    {
		    	basicTermSets.get(i).calculateLogicalAnd(arrayAccessCost, logicalAndCost, 
		    			ifTestCost, bMispredictCost, arrayWriteCost);
		    	basicTermSets.get(i).calculateNoBranch(arrayAccessCost, logicalAndCost, 
		    			arrayWriteCost, funcApplyCost);
		    }
		    
		    //iterate through each set pair
		    for(int i = 0; i < basicTermSets.size() - 1; i++)
		    {
		    	for(int j = i + 1; j < basicTermSets.size(); j++)
		    	{
		    		BasicTermSet leftChild = basicTermSets.get(i);
		    		BasicTermSet rightChild = basicTermSets.get(j);
		    		
		    		if(leftChild.compareCMetric(rightChild, funcApplyCost) == 1)
		    		{
		    			//do nothing
		    		}
		    		else if(leftChild.compareDMetric(rightChild, funcApplyCost) == 1)
		    		{
		    				//do nothing
		    		}
		    		else
		    		{
		    			//change the shit
		    		}
		    	}
		    }
		    
		    //print out the values (we may have to write to a file)
		    System.out.println("====================");
		    System.out.println("--------------------");
		    //TODO: print out the code for the algorithm
		    System.out.println("--------------------");
		    //TODO: print out the total cost from the big set
		    System.out.println("Cost: " + 0.0);
		    
	    }
	}

}
