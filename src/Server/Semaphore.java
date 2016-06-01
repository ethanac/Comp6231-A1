package Server;

/**
 * Semaphore
 * @author Hao
 */
public class Semaphore {
	/**
	 * Current semaphore's value
	 */
	private int iValue;

	/*
	 * ------------
	 * Constructors
	 * ------------
	 */

	/**
	 * With value parameter.
	 *
	 * @param piValue Initial value of the semaphore to set.
	 */
	public Semaphore(int piValue)
	{
		this.iValue = piValue;
	}

	/**
	 * Default. Equivalent to Semaphore(0)
	 */
	public Semaphore()
	{
		this(0);
	}

	/**
	 * Returns true if locking condition is true.
	 */
	public synchronized boolean isLocked()
	{
		return (this.iValue <= 0);
	}

	/*
	 * -----------------------------
	 * Standard semaphore operations
	 * -----------------------------
	 */

	/**
	 * Puts thread asleep if semamphore's values is less than or equal to zero.
	 */
	public synchronized void Wait()
	{
		try
		{
			while(this.iValue <= 0)
			{
				wait();
			}

			this.iValue--;
		}
		catch(InterruptedException e)
		{
			System.out.println
			(
				"Semaphore::Wait() - caught InterruptedException: " +
				e.getMessage()
			);

			e.printStackTrace();
		}
	}

	/**
	 * Increments semaphore's value and notifies another (single) thread of the change.
	 */
	public synchronized void Signal()
	{
		++this.iValue;
		notify();
	}

	/**
	 * Proberen. An alias for Wait().
	 */
	public synchronized void P()
	{
		this.Wait();
	}

	/**
	 * Verhogen. An alias for Signal()
	 */
	public synchronized void V()
	{
		this.Signal();
	}
}
