package nl.hanze.project.moro.util;

/**
 * The <code>Countdown</code> should be used in parallel programming, but resembles
 * the <code>CyclicBarrier</code> which is used as a synchronization aid for
 * multiple threads in concurrent programming. 
 * 
 * The <code>Countdown</code> can be used to keep track of how many parties have
 * reached a common barrier point but unlike the <code>CyclicBarrier</code> it
 * does not support an optional <code>Runnable</code> command once the barrier point
 * has been reached.  
 * 
 * @author Chris Harris
 * @version 0.0.1
 * @since 0.0.1
 */
public class CountDown 
{
	/**
	 * The total number of parties.
	 */
	private final int parties;
	
	/**
	 * The number of parties still waiting.
	 */
	private int count;
	
	/**
	 * Creates a new <code>CountDown</code> that keeps track of how many parties
	 * have reached a common barrier point.
	 * 
	 * @param parties the number of parties that must invoke {@link #countDown()}
	 * 				  before the count has reached zero.
	 */
	public CountDown(int parties)
	{
		if (parties < 0) { 
			throw new IllegalArgumentException(String.format("the number of parties should be larger than 0, received %d", parties));
		}
		
		this.parties = parties;
		count = parties;
	}
	
	/**
	 * Decrease the number of parties by one.
	 */
	public void countDown()
	{
		if (count > 0) {
			count--;
		}
	}
	
	/**
	 * Returns the number of parties that still have to arrive.
	 * 
	 * @return remaining number of parties.
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Reset the <code>CountDown</code> to it's initial state.
	 */
	public void reset()
	{
		count = parties;
	}
}
