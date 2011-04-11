import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Databases 4112 / Project 2 / Stage 2 Algorithm implementation
 * 
 * @author Sean Wang
 * @author Jeff Sinckler/jcs2137
 *
 */
public class stage2 {
	
	/**
	 * Generates a list of every possible set of terms in increasing order
	 * @param selectivityArray	an array containing the selectivities of each term in the query
	 * @return	an ArrayList of BasicTermSet objects, containing information about a set of terms
	 */
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
			ArrayList<Double> sltList = new ArrayList<Double>();
			ArrayList<Integer> termList = new ArrayList<Integer>();
			
			for(int i = 0; i < numTerms; i++) {
				if(bitmap[i][j]) {
					sltList.add(new Double(selectivityArray[i]));
					termList.add(new Integer(i + 1));
				}
			}
			
			double[] sltArray = new double[sltList.size()];
			int[] termArray = new int[termList.size()];
			
			for(int i = 0; i < sltList.size(); i++) {
				sltArray[i] = sltList.get(i);
				termArray[i] = termList.get(i);
			}
			
			setList.add(new BasicTermSet(j, sltArray, termArray));
		}
				
		return setList;
	}
	
	
	
	/**
	 * @param args - takes the location of the query file as the first argument and the location
	 * of the configuration file as the second query parameter
	 */
	public static void main(String[] args) {				
		/* Set up the properties file to store the values for the algorithm */
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(args[1]));
		} 
		catch (IOException e)
		{
			System.out.println("Error: File not found at specified location");
			System.exit(0);
		}
		
		/* Store the values from the configuration file */
		int arrayAccessCost = Integer.parseInt(properties.getProperty("r"));
		int ifTestCost = Integer.parseInt(properties.getProperty("t"));
		int logicalAndCost = Integer.parseInt(properties.getProperty("l"));
		int bMispredictCost = Integer.parseInt(properties.getProperty("m"));
		int arrayWriteCost = Integer.parseInt(properties.getProperty("a"));
		int funcApplyCost = Integer.parseInt(properties.getProperty("f"));
		
		/* Read the query file */
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
	    
	    /* Calculate the best plan for each query */
	    for(String query : queryList) {
		    
		    /* Write the selectivity values from the query into an array of doubles */
		    String[] selectivityStrings = query.split(" ");
		    double[] selectivities = new double[selectivityStrings.length];
		    for(int i = 0; i < selectivityStrings.length; i++) {
		    	selectivities[i] = Double.parseDouble(selectivityStrings[i]);
		    }
		    
		    /* Create the ordered list of sets */
		    ArrayList<BasicTermSet> setList = getSetList(selectivities);
		    
		    /* Calculate the Logical-AND and NOBRANCH costs for each set */
		    for(int i = 0; i < setList.size(); i++)
		    {
		    	setList.get(i).calculateLogicalAnd(arrayAccessCost, logicalAndCost, 
		    			ifTestCost, bMispredictCost, arrayWriteCost, funcApplyCost);
		    	setList.get(i).calculateNoBranch(arrayAccessCost, logicalAndCost, 
		    			arrayWriteCost, funcApplyCost);
		    }
		    
		    /* Find if Branching-AND plans are cheaper */
		    for(BasicTermSet rightChild : setList) {
		    	for(BasicTermSet leftChild : setList) {
		    		/* Check if the two sets share any elements */
		    		if(rightChild.intersects(leftChild)) {
		    			continue;
		    		}
		    		
		    		/* Compare metrics */
		    		if(!leftChild.compareCMetric(rightChild, funcApplyCost, arrayAccessCost, logicalAndCost, ifTestCost))
		    		{
		    			continue;
		    		}
		    		if(!leftChild.compareDMetric(rightChild, funcApplyCost, arrayAccessCost, logicalAndCost, ifTestCost))
		    		{
		    			continue;
		    		}
		    		
		    		/* Calculate the cost of a Branching-AND plan */
		    		double cost = leftChild.calculateCombinedCost(rightChild, bMispredictCost);
		    		
		    		BasicTermSet combinedSet = setList.get(rightChild.getSetNumber() + leftChild.getSetNumber() - 1);
		    		
		    		if(cost < combinedSet.getCost()) {
		    			combinedSet.setCost(cost);
		    			combinedSet.setChildren(leftChild, rightChild);
		    		}
		    	}
		    }
		    
		    
		    BasicTermSet top = setList.get(setList.size() - 1);
		    
		    //print out the values (we may have to write to a file)
		    System.out.println("======================================");
		    System.out.println(query);
		    System.out.println("--------------------------------------");
		    //TODO: print out the code for the algorithm
		    System.out.println("--------------------------------------");
		    //TODO: print out the total cost from the big set
		    System.out.println("Cost: " + top.getCost());
		    
	    }
	}

}
