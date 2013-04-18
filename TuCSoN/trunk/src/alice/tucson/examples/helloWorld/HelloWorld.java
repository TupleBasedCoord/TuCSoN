package alice.tucson.examples.helloWorld;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Plain Java class exploiting TuCSoN library.
 * 
 * @author s.mariani@unibo.it
 */
public class HelloWorld {
	
	/**
	 * @param args the name of the TuCSoN coordinable (optional).
	 */
	public static void main(String[] args) {
		
		/*
		 * 1) Build a TuCSoN Agent identifier to contact the TuCSoN system.
		 */
		TucsonAgentId aid = null;
		try {
			if(args.length == 0)
				aid = new TucsonAgentId("helloWorldMain");
			else if(args.length == 1)
				aid = new TucsonAgentId(args[0]);
			/*
			 * 2) Get a TuCSoN ACC to enable interaction with the TuCSoN system.
			 */
			SynchACC acc = null;
			acc = TucsonMetaACC.getContext(aid);
			/*
			 * 3) Define the tuplecentre target of your coordination operations.
			 */
			TucsonTupleCentreId tid = new TucsonTupleCentreId("default", "localhost", "20504");
			/*
			 * 4) Build the tuple using the communication language.
			 */
			LogicTuple tuple = new LogicTuple("hello", new Value("world"));
			/*
			 * 5) Perform the coordination operation using the preferred coordination
			 * primitive.
			 */
			ITucsonOperation op = acc.out(tid, tuple, null);
			/*
			 * 6) Check requested operation success.
			 */
			LogicTuple res = null;
			if(op.isResultSuccess()){
				System.out.println("["+aid.getAgentName()+"]: Operation succeeded.");
				/*
				 * 7) Get requested operation result.
				 */
				res = op.getLogicTupleResult();
				System.out.println("["+aid.getAgentName()+"]: Operation result is "+res);
			}else{
				System.out.println("["+aid.getAgentName()+"]: Operation failed.");
			}
			/*
			 * Another success test to be sure.
			 */
			LogicTuple template = new LogicTuple("hello", new Var("Who"));
			op = acc.rdp(tid, template, null);
			if(op.isResultSuccess()){
				res = op.getLogicTupleResult();
				System.out.println("["+aid.getAgentName()+"]: Operation result is "+res);
			}else{
				System.out.println("["+aid.getAgentName()+"]: Operation failed.");
			}
			/*
			 * Automatically done by the TuCSoN Node, hence unnecessary.
			 */
			acc.exit();
		} catch (TucsonInvalidAgentIdException e) {
			/*
			 * The chosen TuCSoN Agent ID is not admissible.
			 */
			e.printStackTrace();
		} catch (TucsonInvalidTupleCentreIdException e) {
			/*
			 * The chosen target tuple centre is not admissible. 
			 */
			e.printStackTrace();
		} catch (TucsonOperationNotPossibleException e) {
			/*
			 * The requested TuCSoN operation cannot be performed.
			 */
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			/*
			 * The chosen target tuple centre is not reachable. 
			 */
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			/*
			 * Operation timeout expired.
			 */
			e.printStackTrace();
		}
		
	}

}