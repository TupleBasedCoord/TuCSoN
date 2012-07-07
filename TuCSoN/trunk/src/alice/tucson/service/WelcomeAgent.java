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

import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;

import java.io.IOException;
import java.net.*;

/**
 * 
 */
public class WelcomeAgent extends Thread{
	
	ACCProvider contextManager;
	TucsonNodeService node;
	int port;

	public WelcomeAgent(int port, TucsonNodeService node, ACCProvider cm){
		contextManager = cm;
		this.port = port;
		this.node = node;
		start();
	}

	private void log(String st){
		System.out.println("[WelcomeAgent]: " + st);
	}

	/**
	 * 
	 */
	public void run(){
					
		TucsonProtocol mainDialog = null;
		try{
			mainDialog = new TucsonProtocolTCP(new ServerSocket(port));
		}catch(IOException e1){
			System.err.println("[WelcomeAgent]: " + e1);
			e1.printStackTrace();
		}

		TucsonProtocol dialog = null;
		boolean exception = false;
		try{
			while(true){
				
				log("Listening on port " + port + " for incoming ACC requests...");
				dialog = mainDialog.acceptNewDialog();
				dialog.receiveFirstRequest();
				
				if(dialog.isEnterRequest()){
					dialog.receiveEnterRequest();
					ACCDescription desc = dialog.getContextDescription();
					log("Delegating ACCProvider received enter request...");
					contextManager.processContextRequest(desc, dialog);
				}else if(dialog.isTelnet()){
//					TO DO
					log("Welcome to the Tucson Service Node " + TucsonNodeService.getVersion());
				}
	
			}
		}catch(Exception e){
			exception = true;
			System.err.println("[WelcomeAgent]: " + e);
			e.printStackTrace();
		}
		
		if(exception){
			try{
				dialog.end();
			}catch(Exception e){
				System.err.println("[WelcomeAgent]: " + e);
				e.printStackTrace();
			}
		}
			
	}
	
}
