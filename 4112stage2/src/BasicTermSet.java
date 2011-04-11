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
	 * @param nTerms	the number of terms that this set contains
	 * @param tProduct	the total product of the selectivities that make up the set.
	 */
	public BasicTermSet(int setNum, double[] selectivities)
	{
		this.setNum = setNum;
		this.selectivities = selectivities;
	}
	
	/**
	 * Method that calculates the cost of using a no branch approach with the data in this set
	 * @param arrayAccessCost	the cost of accessing an array element
	 * @param logicalAnd	the cost of performing a logical and
	 * @param arrayWriteCost	the cost of writing an array
	 * @param funcCost	the cost of applying a function to data
	 */
	public void calculateNoBranch(int arrayAccessCost, int logicalAnd, int arrayWriteCost, int funcCost)
	{
		int totalCost = 0;
		//kr
		totalCost += numTerms*arrayAccessCost;
		//(k - 1) * l
		totalCost += (numTerms - 1)*logicalAnd;
		//a
		totalCost += arrayWriteCost;
		
		//f1 + ... + fk
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
		//kr
		totalCost += numTerms*arrayAccessCost;
		//(k - 1)l
		totalCost += (numTerms - 1)*logicalAnd;
		//t
		totalCost += ifTestCost;
		double q = 1.0;
		double selectivityProduct = 1.0;
		
		//mq
		for(int i = 0; i < numTerms; i++)
		{
			q *= selectivities[i];
		}
		if(q <= .5)
			totalCost += branchMisp * q;
		else
			totalCost += branchMisp * (1 - q);
		
		//p1 ... pk * a
		totalCost *= arrayWriteCost * q;
		
		if((double) totalCost < bestCost || bestCost == 0)
		{
			bestCost = (double) totalCost;
			noBranch = false;
		}
	}
	
	/**
	 * Compares the C metric of this set to the C metric of the leftmost child of comp
	 * @param comp	the set to which the metric is being compared
	 * @param funcCost	the cost to apply a function
	 * @param arrayAccessCost
	 * @param logicalAnd
	 * @param ifTestCost
	 * @return	 0 if the right metric dominates and this plan is sub-optimal, 
	 * 1 if the left metric dominates and this plan is potentially optimal
	 */
	public int compareCMetric(BasicTermSet comp, int funcCost, int arrayAccessCost, int logicalAnd, int ifTestCost)
	{
		/*double metricValue = (totalProduct - 1) / (double) funcCost;
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
		}*/
		
		//tuple to hold the values for this metric
		double[] thisMetric = new double[2];
		//tuple to hold the values for comp parameter metric
		double[] compMetric = new double[2];
		
		//numerator of the metric
		double temp = (totalProduct - 1);
		//denominator of the metric
		double temp2 = numTerms*arrayAccessCost + (numTerms - 1) * logicalAnd + ifTestCost;
		for(int i = 0; i < numTerms; i++)
		{
			temp2 += funcCost;
		}
		thisMetric[0] = temp / temp2;
		thisMetric[1] = totalProduct;
		
		BasicTermSet tempSet = comp;
		while(tempSet.leftChild != null)
		{
			tempSet = tempSet.leftChild;
		}
		//numerator of the comp metric
		temp = tempSet.totalProduct - 1;
		//denominator of the comp metric
		temp2 = tempSet.numTerms*arrayAccessCost + (tempSet.numTerms - 1) * logicalAnd + ifTestCost;
		for(int i = 0; i < tempSet.numTerms; i++)
		{
			temp2 += funcCost;
		}
		compMetric[0] = temp / temp2;
		compMetric[1] = totalProduct;
		
		//if the cmetric of the left child is dominated by the cmetric of the right child...
		if(thisMetric[0] < compMetric[0] && thisMetric[1] < compMetric[1])
		{
			return 0;
		}
		else
		{
			return 1;
		}
		
	}
	
	/**
	 * Compares the D metric of this set to the D metric of each and term that is not the leftmost child
	 * @param comp	the set to which this metric is being compared
	 * @param funcCost	the cost of applying a function
	 * @param arrayAccessCost
	 * @param logicalAnd
	 * @param ifTestCost
	 * @return 0 if the right metric dominates and this plan is suboptimal, 
	 * 1 if the left metric dominates and this plan is indeed optimal
	 */
	public int compareDMetric(BasicTermSet comp, int funcCost, int arrayAccessCost, int logicalAnd, 
			int ifTestCost)
	{
		/*double metricValue = (double) funcCost;
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
		}*/
		
		//tuple to hold the values for this metric
		double[] thisMetric = new double[2];
		//tuple to hold the values for comp parameter metric
		double[] compMetric = new double[2];
		
		double temp = numTerms*arrayAccessCost + (numTerms - 1) * logicalAnd + ifTestCost;
		for(int i = 0; i < numTerms; i++)
		{
			temp += funcCost;
		}
		thisMetric[0] = temp;
		thisMetric[1] = totalProduct;
		
		int proceed = traverseTreeMetric(totalProduct, thisMetric, comp, arrayAccessCost, logicalAnd, ifTestCost, funcCost, true);
		return proceed;
	}
	
	/**
	 * Recursive method used to traverse all the and terms of a set and check the metric
	 * @param product	the combined selectivity of the original left child set
	 * @param metric	the metric of the original left child set
	 * @param traversed	the set that is being traversed/checked by the method
	 * @param arrayAccessCost	the cost of accessing an element of the array
	 * @param logicalAnd	the cost of performing a logical AND
	 * @param ifTestCost	the cost of performing an if test
	 * @param funcCost	the cost of applying a function to its argument
	 * @param leftmostChild	a boolean representing whether the current node is potentially the leftmost child
	 * @return
	 */
	public int traverseTreeMetric(double product, double[] metric, BasicTermSet traversed, 
			int arrayAccessCost, int logicalAnd, int ifTestCost, int funcCost, boolean leftmostChild)
	{
		if(traversed.leftChild != null)
		{
			if(traverseTreeMetric(product, metric, traversed.leftChild, arrayAccessCost, logicalAnd, ifTestCost, funcCost, leftmostChild) == 1)
			{
				return 1;
			}
		}
		
		if(traversed.rightChild != null)
		{
			if(traverseTreeMetric(product, metric, traversed.rightChild, arrayAccessCost, logicalAnd, ifTestCost, funcCost, false) == 1)
			{
				return 1;
			}
		}
		
		if(leftmostChild && traversed.leftChild == null)
		{
			return 0;
		}
		
		double[] compMetric = new double[2];
		double temp = traversed.numTerms*arrayAccessCost + (traversed.numTerms - 1) * logicalAnd + ifTestCost;
		for(int i = 0; i < traversed.numTerms; i++)
		{
			temp += funcCost;
		}
		
		compMetric[0] = temp;
		compMetric[1] = traversed.totalProduct;
		
		//if the totalProduct is less than or equal to .5 and the dmetric of the left child is dominated
		//by the dmetric of this right child
		if(product <= .5 && metric[0] < compMetric[0] && metric[1] < compMetric[1])
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
	
	public int getSetNumber() {
		return setNum;
	}
	
	public boolean intersects(BasicTermSet set) {
		return (setNum & set.getSetNumber()) > 0;
	}
}
