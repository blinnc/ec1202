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
	private int[] terms;
	private int setNum;
	private double fixedCost;
	
	/**
	 * Constructor of the set.
	 * @param nTerms	the number of terms that this set contains
	 * @param tProduct	the total product of the selectivities that make up the set.
	 */
	public BasicTermSet(int setNumber, double[] selectivityArray, int[] termArray)
	{
		setNum = setNumber;
		selectivities = selectivityArray;
		terms = termArray;
		numTerms = selectivities.length;
		
		totalProduct = 1;
		for(double p : selectivities) {
			totalProduct *= p;
		}
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
		double totalCost = 0;
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
		
		if(totalCost < bestCost || bestCost == 0)
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
	public void calculateLogicalAnd(int arrayAccessCost, int logicalAnd, int ifTestCost, int branchMisp, int arrayWriteCost, int funcCost)
	{
		double totalCost = 0;
		//kr
		totalCost += numTerms*arrayAccessCost;
		//(k - 1)l
		totalCost += (numTerms - 1)*logicalAnd;
		//t
		totalCost += ifTestCost;
		double q = 1.0;
		//double selectivityProduct = 1.0;
		
		//mq
		for(int i = 0; i < numTerms; i++)
		{
			totalCost += funcCost;
			q *= selectivities[i];
		}
		
		if(q <= .5)
			totalCost += branchMisp * q;
		else
			totalCost += branchMisp * (1 - q);
		
		//p1 ... pk * a
		totalCost += arrayWriteCost * q;
		
		if(totalCost < bestCost || bestCost == 0)
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
	 * @return	 false if the right metric dominates and this plan is sub-optimal, 
	 * true if the left metric dominates and this plan is potentially optimal
	 */
	public boolean compareCMetric(BasicTermSet comp, int funcCost, int arrayAccessCost, int logicalAnd, int ifTestCost)
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
		
		//numerator of the metric: (p - 1)
		double temp = (totalProduct - 1);
		//denominator of the metric: fcost(E)
		double temp2 = numTerms*arrayAccessCost + (numTerms - 1) * logicalAnd + ifTestCost;
		for(int i = 0; i < numTerms; i++)
		{
			temp2 += funcCost;
		}
		
		setFixedCost(temp2);
		thisMetric[0] = temp / temp2;
		thisMetric[1] = totalProduct;
		
		//traverse down the left children until we reach the leftmost
		BasicTermSet tempSet = comp;
		while(tempSet.leftChild != null)
		{
			tempSet = tempSet.leftChild;
		}
		
		//numerator of the leftmost child metric: (p - 1)
		temp = tempSet.totalProduct - 1;
		//denominator of the leftmost child metric: fcost(E)
		temp2 = tempSet.numTerms*arrayAccessCost + (tempSet.numTerms - 1) * logicalAnd + ifTestCost;
		for(int i = 0; i < tempSet.numTerms; i++)
		{
			temp2 += funcCost;
		}
		
		compMetric[0] = temp / temp2;
		compMetric[1] = tempSet.totalProduct;
		
		//if the cmetric of the left child is dominated by the cmetric of the right child...
		if(thisMetric[0] >= compMetric[0] && thisMetric[1] > compMetric[1])
		{
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
	/**
	 * Compares the D metric of this set to the D metric of each and term that is not the leftmost child
	 * @param comp	the set to which this metric is being compared
	 * @param funcCost	the cost of applying a function
	 * @param arrayAccessCost
	 * @param logicalAnd
	 * @param ifTestCost
	 * @return false if the right metric dominates and this plan is suboptimal, 
	 * true if the left metric dominates and this plan is indeed optimal
	 */
	public boolean compareDMetric(BasicTermSet comp, int funcCost, int arrayAccessCost, int logicalAnd, 
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
		
		/*BasicTermSet tempSet = comp;
		while(tempSet.leftChild != null)
		{
			tempSet = tempSet.leftChild;
		}
		
		temp = tempSet.numTerms*arrayAccessCost + (tempSet.numTerms - 1) * logicalAnd + ifTestCost;
		for(int i = 0; i < tempSet.numTerms; i++)
		{
			temp += funcCost;
		}
		
		compMetric[0] = temp;
		compMetric[1] = tempSet.totalProduct;
		
		//if the cmetric of the left child is dominated by the cmetric of the right child...
		if(thisMetric[0] < compMetric[0] && thisMetric[1] < compMetric[1])
		{
			return false;
		}
		else
		{
			return true;
		}*/
		
		boolean proceed = traverseTreeMetric(totalProduct, thisMetric, comp, arrayAccessCost, logicalAnd, ifTestCost, funcCost, true);
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
	 * @return false if no node was found -- the plan is suboptimal; true if a node was found, meaning that the plan
	 * is optimal
	 */
	public boolean traverseTreeMetric(double product, double[] metric, BasicTermSet traversed, 
			int arrayAccessCost, int logicalAnd, int ifTestCost, int funcCost, boolean leftmostChild)
	{
		if(traversed.leftChild != null)
		{
			if(traverseTreeMetric(product, metric, traversed.leftChild, arrayAccessCost, logicalAnd, ifTestCost, funcCost, leftmostChild))
			{
				return true;
			}
		}
		
		if(traversed.rightChild != null)
		{
			if(traverseTreeMetric(product, metric, traversed.rightChild, arrayAccessCost, logicalAnd, ifTestCost, funcCost, false))
			{
				return true;
			}
		}
		
		/*if(leftmostChild && traversed.leftChild == null)
		{
			return false;
		}*/
		
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
		if(product <= 0.5 && metric[0] < compMetric[0] && metric[1] < compMetric[1])
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public double calculateCombinedCost(BasicTermSet rightChild, int bMispredict)
	{
		double result;
		double leftTotalProduct = getTotalProduct();
		double f = fixedCost;
		double mq = bMispredict * (leftTotalProduct <= .5 ? leftTotalProduct : 1 - leftTotalProduct);
		double pC = leftTotalProduct * rightChild.getCost();
		
		result = fixedCost + bMispredict * 
			(leftTotalProduct <= .5 ? leftTotalProduct : 1 - leftTotalProduct) +
			leftTotalProduct * rightChild.getCost();
		
		return result;
	}
	
	private void setFixedCost(double value)
	{
		
		fixedCost = value;
	}
	
	public int getSetNumber() {
		return setNum;
	}
	
	public boolean intersects(BasicTermSet set) {
		return (setNum & set.getSetNumber()) > 0;
	}
	
	public double getCost() {
		return bestCost;
	}
	
	public void setCost(double newCost) {
		bestCost = newCost;
	}
	
	public void setChildren(BasicTermSet leftChild, BasicTermSet rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		noBranch = false;
	}
	
	public double getTotalProduct()
	{
		return totalProduct;
	}
}
