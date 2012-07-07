/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tucson.service;

import alice.logictuple.*;

import alice.tucson.api.*;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.api.TucsonTupleCentreId;

import alice.tucson.network.TucsonMsgReply;
import alice.tucson.network.TucsonMsgRequest;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;
import alice.tucson.service.TucsonOperation;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuprolog.Prolog;
import alice.tuprolog.lib.InvalidObjectIdException;

import java.io.*;

import java.util.*;

/**
 * 
 */
public class InterTupleCentreACCProxy implements InterTupleCentreACC{
	// aid il tuplecentre source
	private TucsonTupleCentreId aid;
	private ACCDescription profile;
	private LinkedList<TucsonOpCompletionEvent> events;
	private HashMap<String, ControllerSession> controllerSessions;
	private int opId;

	/**
	 * 
	 * @param id tuplecentre source
	 */
	public InterTupleCentreACCProxy(Object id){
		
		if(id.getClass().getName().equals("alice.tucson.api.TucsonTupleCentreId"))
			this.aid = (TucsonTupleCentreId) id;
		else{
			try{
				this.aid = new TucsonTupleCentreId(id);
			}catch(TucsonInvalidTupleCentreIdException e){
				System.err.println("[InterTupleCentreACCProxy]: " + e);
				e.printStackTrace();
			}
		}

		profile = new ACCDescription();
		opId = -1;
		events = new LinkedList<TucsonOpCompletionEvent>();
		controllerSessions = new HashMap<String, ControllerSession>();

	}

	/**
	 * tid il tuplecentre target
	 */
	public synchronized TucsonOpId doOperation(Object tid, int type, Object t) throws TucsonOperationNotPossibleException, UnreachableNodeException{

		TucsonTupleCentreId tcid = null;
		if(tid.getClass().getName().equals("alice.tucson.api.TucsonTupleCentreId"))
			tcid = (TucsonTupleCentreId) tid;
		else{
			try{
				tcid = new TucsonTupleCentreId(tid);
			}catch(TucsonInvalidTupleCentreIdException e){
				throw new TucsonOperationNotPossibleException();
			}
		}
		if(!tcid.checkSyntax() || !aid.checkSyntax())
			throw new TucsonOperationNotPossibleException();

		int nTry = 0;
		boolean exception;

		do{
			
			opId++;
			nTry++;
			exception = false;

			TucsonProtocol session = null;
			try{
				session = getSession(tcid);
			}catch(UnreachableNodeException ex2){
				exception = true;
				throw new UnreachableNodeException();
			}
			ObjectOutputStream outStream = session.getOutputStream();
			
			TucsonMsgRequest msg = new TucsonMsgRequest(opId, type, tcid.toString(), (LogicTuple) t);
			log("sending msg " + msg.getType() + ", " + msg.getTuple() + ", " + msg.getTid());
			try{
				TucsonMsgRequest.write(outStream, msg);
				outStream.flush();
			}catch(IOException ex){
				exception = true;
				System.err.println("[InterTupleCentreACCProxy]: " + ex);
				ex.printStackTrace();
			}
			
			if(!exception)
				return new TucsonOpId(opId);
			
		}while(nTry < 3);
		
		throw new UnreachableNodeException();
		
	}

	/**
	 * 
	 */
	public TucsonOpCompletionEvent waitForCompletion(TucsonOpId id, int timeout){
		
		try{
			long startTime = System.currentTimeMillis();
			synchronized(events){
				long dt = System.currentTimeMillis() - startTime;
				TucsonOpCompletionEvent ev = findEvent(id);
				while(ev == null && dt < timeout){
					events.wait(timeout - dt);
					ev = findEvent(id);
					dt = System.currentTimeMillis() - startTime;
				}
				return ev;
			}
		}catch(Exception ex){
			return null;
		}
		
	}

	/**
	 * 
	 */
	public TucsonOpCompletionEvent waitForCompletion(TucsonOpId id){
		
		try{
			synchronized(events){
				TucsonOpCompletionEvent ev = findEvent(id);
				while(ev == null){
					events.wait();
					ev = findEvent(id);
				}
				return ev;
			}
		}catch(Exception ex){
			return null;
		}
		
	}

	/**
	 * 
	 * @param tid
	 * @return
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 */
	private TucsonProtocol getSession(TucsonTupleCentreId tid) throws UnreachableNodeException{
		
		String opNode = alice.util.Tools.removeApices(tid.getNode());
		int port = tid.getPort();
//		log(opNode+":"+port);
		ControllerSession tc = controllerSessions.get(opNode+":"+port);
		if(tc != null)
			return tc.getSession();
		else{
			if(opNode.equals("localhost"))
				tc = controllerSessions.get("127.0.0.1:"+port);
			if(opNode.equals("127.0.0.1"))
				tc = controllerSessions.get("localhost:"+port);
			if(tc != null)
				return tc.getSession();
		}
//		log("New connection to setup toward node " + opNode+":"+port);	
		profile.setProperty("agent-identity", aid.toString());
		profile.setProperty("agent-role", "user");

		TucsonProtocol dialog = null;
		boolean isEnterReqAcpt = false;
		try{
			dialog = new TucsonProtocolTCP(opNode, port);
			dialog.sendEnterRequest(profile);
			dialog.receiveEnterRequestAnswer();
			if(dialog.isEnterRequestAccepted())
				isEnterReqAcpt = true;
		}catch(Exception ex){
			throw new UnreachableNodeException();
		}								

		if(isEnterReqAcpt){
			ObjectInputStream din = dialog.getInputStream();
			Controller contr = new Controller(din);
			ControllerSession cs = new ControllerSession(contr, dialog);
			controllerSessions.put(opNode+":"+port, cs);
			contr.start();
			return dialog;
		}
		
		return null;
		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private TucsonOpCompletionEvent findEvent(TucsonOpId id){
		Iterator<TucsonOpCompletionEvent> it = events.iterator();
		while(it.hasNext()){
			TucsonOpCompletionEvent ev = (TucsonOpCompletionEvent) it.next();
			if(ev.getOpId().equals(id)){
				it.remove();
				return ev;
			}
		}
		return null;
	}

	private void postEvent(TucsonOpCompletionEvent ev){
		synchronized(events){
			events.addLast(ev);
			events.notifyAll();
		}
	}

	private void log(String msg){
		System.out.println("[InterTupleCentreACCProxy]: " + msg);
	}

	/**
	 * 
	 */
	class Controller extends Thread{

		private boolean stop;
		private ObjectInputStream in;
		private final Prolog p = new Prolog();

		/**
		 * 
		 * @param in
		 */
		Controller(ObjectInputStream in){
			
			this.in = in;
			stop = false;
			this.setDaemon(true);
			
			alice.tuprolog.lib.JavaLibrary jlib = (alice.tuprolog.lib.JavaLibrary) p.getLibrary("alice.tuprolog.lib.JavaLibrary");
			try{
				jlib.register(new alice.tuprolog.Struct("config"), this);
			}catch(InvalidObjectIdException ex){
				System.err.println("[InterTupleCentreACCProxy] Controller: " + ex);
				ex.printStackTrace();
			}
			
		}

		@SuppressWarnings("unchecked")
		public void run(){
			
			TucsonOpCompletionEvent ev = null;
			while(!isStopped()){
				
				TucsonMsgReply msg = null;
				try{
					msg = TucsonMsgReply.read(in);
				}catch(EOFException e){
					log("TuCSoN node service unavailable, nothing I can do");
					setStop();
					break;
				}catch(Exception ex){
					setStop();
					System.err.println("[InterTupleCentreACCProxy] Controller: " + ex);
				}
				
				boolean ok = msg.isAllowed();
				if(ok){
					
					int type = msg.getType();
					if(type == TucsonOperation.noCode() || type == TucsonOperation.no_sCode()
							|| type == TucsonOperation.nopCode() || type == TucsonOperation.nop_sCode()
							|| type == TucsonOperation.inCode() || type == TucsonOperation.rdCode()
							|| type == TucsonOperation.inpCode() || type == TucsonOperation.rdpCode()
							|| type == TucsonOperation.uinCode() || type == TucsonOperation.urdCode()
							|| type == TucsonOperation.uinpCode() || type == TucsonOperation.urdpCode()
							|| type == TucsonOperation.in_allCode() || type == TucsonOperation.rd_allCode()
							|| type == TucsonOperation.in_sCode() || type == TucsonOperation.rd_sCode()
							|| type == TucsonOperation.inp_sCode() || type == TucsonOperation.rdp_sCode()){

						boolean succeeded = msg.isSuccess();
						if(succeeded){
							
							LogicTuple tupleReq = msg.getTupleRequested();
							LogicTuple tupleRes = (LogicTuple) msg.getTupleResult();
							LogicTuple res = unify(tupleReq, tupleRes);
							ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, true, res);
							
						}else{
							ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, false);
						}
						
					}else if(type == TucsonOperation.get_sCode()){
						
						LogicTuple tupleRes = (LogicTuple) msg.getTupleResult();
						ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, msg.isSuccess(), tupleRes);
					
					}else if(type == TucsonOperation.set_Code() || type == TucsonOperation.set_sCode()
							|| type == TucsonOperation.outCode() || type == TucsonOperation.out_sCode()){
						ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, msg.isSuccess());
					}else if(type == TucsonOperation.get_Code()){
						List<LogicTuple> tupleSetRes = (List<LogicTuple>) msg.getTupleResult();
						ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, msg.isSuccess(), tupleSetRes);
					}else if(type == TucsonOperation.exitCode()){
						setStop();
						break;
					}
					
				}else{
					ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), false, false);
				}
				
				/* What to do here? Is there no completion for remote operations due to this??
				if(op.isGet())
					op.setLogicTupleListResult((List<LogicTuple>) msg.getTupleResult());
				else{
					op.setTupleResult((LogicTuple) msg.getTupleResult());
//					log("msg.getTupleResult = " + msg.getTupleResult());
				}
				if(msg.isResultSuccess())
					op.setOpResult(Outcome.SUCCESS);
				else
					op.setOpResult(Outcome.FAILURE);
				op.notifyCompletion(ev.operationSucceeded(), msg.isAllowed());
				*/
				
				postEvent(ev);

			}
			
		}

		synchronized boolean isStopped(){
			return stop;
		}

		synchronized void setStop(){
			stop = true;
		}

		LogicTuple unify(TupleTemplate template, Tuple tuple){
			boolean res = template.propagate(p, tuple);
			if(res)
				return (LogicTuple) template;
			else
				return null;

		}
		
	}

	class ControllerSession{
		
		private Controller controller;
		private TucsonProtocol session;

		ControllerSession(Controller c, TucsonProtocol s){
			controller = c;
			session = s;
		}

		public Controller getController(){
			return controller;
		}

		public TucsonProtocol getSession(){
			return session;
		}
		
	}

}
