package edu.gatech.grits.puppetctrl.matlab;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import edu.gatech.grits.puppetctrl.mdl.action.*;
import edu.gatech.grits.puppetctrl.mdl.util.Mode;
import edu.gatech.grits.puppetctrl.mdl.util.ModeString;

import javolution.util.FastMap;

public class PuppetClient
{
	private Socket sock;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	private boolean debug = true;

	private boolean reqDone = false;

	public PuppetClient() throws Exception
	{
		sock = null;
		out  = null;
		in   = null;

		sock = new Socket( "localhost", MatlabControl.PORT );
		DataOutputStream dataOut = new DataOutputStream(sock.getOutputStream());
		out = new ObjectOutputStream(dataOut);

		//			in = new BufferedReader( new InputStreamReader( sock.getInputStream()) );
		DataInputStream dataIn = new DataInputStream(sock.getInputStream());
		in = new ObjectInputStream(dataIn);

		if(debug)
			System.out.println("PuppetClient created!");
	}

	public void finishJob() throws IOException
	{
		out.close();
		in.close();
		sock.close();
	}

	public void sendMessage( Object j ) throws IOException {
		Object job = j;
		// uncomment for debugging purposes
		//		 System.out.println("Send Matlab request" );

		out.writeObject(job);
		Object fromServer;
		reqDone = false;
		try {
			while(( fromServer = in.readObject() ) != null && !reqDone) 
			{
				// comment this out when not debugging 
				processData(fromServer);
				reqDone = true;
				if(reqDone)
					break;

			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private final void processData(Object dataIn){

		if(dataIn.equals(PuppetServer.ERROR)){
			System.err.println("Puppet server side error!");
		}
		else if(dataIn.equals(PuppetServer.DONE)){
			System.out.println("Play optimization complete!");
		}

	}

	public static void main(String[] args){
		PuppetClient pc;
		try {
			System.out.println("Start client.");
			pc = new PuppetClient();
			
//			ArrayList<Float> number = new ArrayList<Float>();
//			number.add(0, 1.2f);
//			number.add(1, 2.4f);
//			number.add(2, 3.6f);
//			number.add(3, 4.8f);
//			number.add(4, 5.1f);
//			number.add(5, 6.2f);
//			number.add(6, 7.4f);
//			pc.sendMessage(number);

			
			// Send a play
			//TODO: integrate with Java gui
			FastMap<String, ModeString> play = new FastMap<String, ModeString>();
			ModeString ms = new ModeString();
			Mode mode1 = new Mode(3000, "region1", 1.2f, new Walk());
			Mode mode2 = new Mode(2500, "region1", 0.9f, new Run());
			ms.addNewMode(mode1);
			ms.addNewMode(mode2);
			play.put("magnus", ms);
			pc.sendMessage(play);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Stop server!");
			pc.sendMessage(PuppetServer.STOP);

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
