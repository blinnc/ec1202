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
	/**
	 * @param args - takes the location of the query file as the first argument and the location
	 * of the configuration file as the second query parameter
	 */
	public static void main(String[] args) {
		
		//algorithm cost values
		int arrayAccessCost;
		int ifTestCost;
		int logicalAndCost;
		int bMispredictCost;
		int arrayWriteCost;
		int funcApplyCost;
		int numFunctions;
		double[] funcSelectivities = new double[10];
		
		//some helper data
		String queryData;
		ArrayList<BasicTermSet> basicTermSets = new ArrayList<BasicTermSet>();
		int lineCounter = 0;
		
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
		StringBuilder contents = new StringBuilder();
		try {
		      //use buffering, reading one line at a time
		      BufferedReader input =  new BufferedReader(new FileReader(args[0]));
		      try {
		        String line = null; //not declared within while loop
		        while (( line = input.readLine()) != null){
		          contents.append(line);
		          contents.append("/");
		          lineCounter++;
		        }
		      }
		      finally {
		        input.close();
		      }
    	}
	    catch (IOException ex){
	        ex.printStackTrace();
	    }
	    queryData = contents.toString();
	    
	    //get the first set of selectivities from the query file
	    while(lineCounter >= 1)
	    {
		    //read the next set of values from the total list, then chop that portion of the list
	    	//off of the string
	    	String firstSet = queryData.substring(0, contents.indexOf("/"));
		    queryData = queryData.substring(contents.indexOf("/") + 1, queryData.length());
		    
		    //write the selectivity values from the query into an array of doubles
		    StringTokenizer st = new StringTokenizer(firstSet);
		    int counter = 0;
		    while(st.hasMoreTokens())
		    {
		    	funcSelectivities[counter] = Double.parseDouble(st.nextToken(" "));
		    	counter++;
		    }
		    
		    //set the total number of selectivities found in the line of the query
		    numFunctions = counter;
		    
		    //loop that iterates over the selectivities and creates an extensive set. Needs work right now
		    //TODO: Make this loop work...
		    for(int i = 1; i <= numFunctions; i++)
		    {
		    	for(int j = i + 1; j <= numFunctions; j++)
		    	{
		    		basicTermSets.add(new BasicTermSet(i, funcSelectivities[j]));
		    	}
		    }
		    
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
		    System.out.println(firstSet);
		    System.out.println("--------------------");
		    //TODO: print out the code for the algorithm
		    System.out.println("--------------------");
		    //TODO: print out the total cost from the big set
		    System.out.println("Cost: " + 0.0);
		    
		    lineCounter--;
	    }
	}

}
