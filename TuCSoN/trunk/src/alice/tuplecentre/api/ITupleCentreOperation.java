package alice.tuplecentre.api;

import alice.tuplecentre.core.OperationTimeOutException;


/**
 * Basic interface for tuple centre operations.
 * 
 * @author aricci
 */
public interface ITupleCentreOperation{

	boolean isOut();
	
	boolean isIn();

	boolean isRd();

	boolean isInp();

	boolean isRdp();
	
	boolean isNo();
	
	boolean isNop();
	
	boolean isSet_s();
	
	boolean isGet_s();
	
	boolean isGet();
	
	boolean isSet();		

//	my personal update
	
	boolean isOutAll();
	
	boolean isInAll();

	boolean isRdAll();
	
	boolean isUrd();
	
	boolean isUin();
	
	boolean isUinp();
	
	boolean isUrdp();
	
	/**
	 * Tests if the operation is completed
	 * 
	 * @return true if the operation is completed
	 */
	boolean isOperationCompleted();

	/**
	 * Wait for operation completion
	 * 
	 * Current execution flow is blocked until the operation is completed
	 */
	void waitForOperationCompletion();

	/**
	 * Wait for operation completion, with time out
	 * 
	 * Current execution flow is blocked until the operation is completed
	 * or a maximum waiting time is elapsed
	 * 
	 * @param ms maximum waiting time
	 * @throws OperationTimeOutException
	 */
	void waitForOperationCompletion(long ms) throws OperationTimeOutException;

	/**
	 * Get operation identifier
	 * 
	 * @return Operation identifier
	 */
	long getId();

}