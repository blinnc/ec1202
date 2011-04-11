/**
 * Databases 4112 / Project 2 / Stage 2 Algorithm implementation
 * 
 * @author Jeff Sinckler/jcs2137
 * @author Sean Wang
 *
 */
public class BasicTermSet {
	
	//data that the set needs to maintain
	private int numTerms;
	private double totalProduct;
	private boolean noBranch = false;
	private double bestCost;
	private BasicTermSet leftChild = null;
	private BasicTermSet rightChild = null;
	private double[] selectivities;
	private int setNum;
	
	/**
	 * Constructor of the set.
	 * @param nTerms - the number of terms that this set contains
	 * @param tProduct - the total product of the selectivities that make up the set.
	 */
	public BasicTermSet(int setNum, double[] selectivities)
	{
		this.setNum = setNum;
		this.selectivities = selectivities;
	}
	
	/**
	 * Method that calculates the cost of using a no branch approach with the data in this set
	 * @param arrayAccessCost - the cost of accessing an array element
	 * @param logicalAnd - the cost of performing a logical and
	 * @param arrayWriteCost - the cost of writing an array
	 * @param funcCost - the cost of applying a function to data
	 */
	public void calculateNoBranch(int arrayAccessCost, int logicalAnd, int arrayWriteCost, int funcCost)
	{
		int totalCost = 0;
		totalCost += numTerms*arrayAccessCost;
		totalCost += (numTerms - 1)*logicalAnd;
		totalCost += arrayWriteCost;
		for(int i = 0; i < numTerms; i++)
		{
			totalCost += funcCost;
		}
		
		if((double) totalCost < bestCost || bestCost == 0)
		{
			bestCost = (double) totalCost;
			noBranch = true;
		}
	}
	
	/**
	 * Method that calculates the cost of using a logical and approach with the data in this set
	 * @param arrayAccessCost - the cost of accessing an array element
	 * @param logicalAnd - the cost of performing a logical and
	 * @param ifTestCost - the cost of performing and if test
	 * @param branchMisp - the penalty associated with a branch misprediction
	 * @param arrayWriteCost - the cost of writing data to the array
	 */
	public void calculateLogicalAnd(int arrayAccessCost, int logicalAnd, int ifTestCost, int branchMisp, int arrayWriteCost)
	{
		int totalCost = 0;
		totalCost += numTerms*arrayAccessCost;
		totalCost += (numTerms - 1)*logicalAnd;
		totalCost += ifTestCost;
		for(int i = 0; i < numTerms; i++)
		{
			if(selectivities[i] <= .5)
			{
				totalCost += selectivities[i] * branchMisp;
			}
			else
			{
				totalCost += 1 - selectivities[i] * branchMisp;
			}
			totalCost += selectivities[i]*arrayWriteCost;
		}
		
		if((double) totalCost < bestCost || bestCost == 0)
		{
			bestCost = (double) totalCost;
			noBranch = false;
		}
	}
	
	/**
	 * Compares the C metric of this set to the C metric of set comp
	 * @param comp - the set to which the metric is being compared
	 * @param funcCost - the cost to apply a function
	 * @return - 0 if the right metric dominates, 1 if the left metric dominates
	 */
	public int compareCMetric(BasicTermSet comp, int funcCost)
	{
		double metricValue = (totalProduct - 1) / (double) funcCost;
		double otherMetricValue = (comp.totalProduct - 1) / (double) funcCost;
		
		if(metricValue < otherMetricValue)
		{
			return 0;
		}
		else if(metricValue > otherMetricValue)
		{
			return 1;
		}
		else
		{
			if(totalProduct < comp.totalProduct)
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}
	}
	
	/**
	 * Compares the D metric of this set to the D metric of the set comp
	 * @param comp - the set to which this metric is being compared
	 * @param funcCost - the cost of applying a function
	 * @return 0 if the right metric dominates, 1 if the left metric dominates
	 */
	public int compareDMetric(BasicTermSet comp, int funcCost)
	{
		double metricValue = (double) funcCost;
		double otherMetricValue = (double) funcCost;
		
		if(totalProduct <= .5 && metricValue < otherMetricValue)
		{
			return 1;
		}
		else if(totalProduct <= .5 && metricValue == otherMetricValue)
		{
			if(totalProduct > comp.totalProduct)
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}
		else
		{
			return 0;
		}
	}
}
