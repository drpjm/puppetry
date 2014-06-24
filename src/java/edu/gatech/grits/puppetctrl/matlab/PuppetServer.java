package edu.gatech.grits.puppetctrl.matlab;


import java.io.*;
import java.lang.reflect.TypeVariable;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.lang.Number;

import edu.gatech.grits.puppetctrl.mdl.util.Mode;
import edu.gatech.grits.puppetctrl.mdl.util.ModeString;
import com.mathworks.jmi.*;


import javolution.util.FastMap;

// to test server alone, start command prompt and type:
// %  telnet localhost 4444
// send it requests as if it were clientRequest
// alternatively, use MatClient.java and testClient.java 
public class PuppetServer 
{
	private MatlabControl mc;
	private Caller caller;
	private boolean listening = true;

	public final static String STOP = "stop";
	public final static String RESET = "reset";
	public final static String DONE = "done";
	public static final String ERROR = "error";

	public void start() throws IOException {
		mc = new MatlabControl();
		caller = new Caller();
		caller.start();
	}

	public class Caller extends Thread {
		public void run()
		{
			try 
			{
				body2();
			}
			catch( IOException e ) { System.err.println( e ); }
		}
		public void body2()  throws IOException
		{
			ServerSocket serverSocket = null;
			try
			{
				serverSocket = new ServerSocket( MatlabControl.PORT ); 
			}
			catch( IOException e ) { 
				System.err.println( e );
				listening = false;
			}

			while( listening )
			{
				new MatlabWorker( serverSocket.accept() ).run();
			}
			serverSocket.close();
			System.out.println("Server exit.");
		}
	}		

	public class MatlabWorker 
	{
		private Socket socket = null;

		public MatlabWorker( Socket socket ) { 
			this.socket = socket; 
		}

		public void run()
		{
			try
			{
				if(listening){
					DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
					ObjectOutputStream out = new ObjectOutputStream(dataOut);

					DataInputStream dataIn = new DataInputStream(socket.getInputStream());
					ObjectInputStream in = new ObjectInputStream(dataIn);

					Object input, output;
					try {
						while(( input = in.readObject()) != null) 
						{
							output = processInput( out, input );
							out.writeObject(output);
						}
					} catch (ClassNotFoundException e) {
						System.out.println("Error: class not found!");
					}

					out.close();
					in.close();
					socket.close();
				}
				else{
					System.out.println("Server is going down.");
					return;
				}
			}
			catch( IOException e ) { 
				System.err.println( e );
				listening = false;
				return;
			}

		}


		private Object processInput(ObjectOutputStream outStream, Object input) {

			System.out.println("Server processing...");
			
//			String str = "walk.m" ;
//			ArrayList<Float> num = new ArrayList<Float>();
//			num.add(0, 1.7f);
//			num.add(1, 2.2f);
//			num.add(2, 3.3f);
//			num.add(3, 4.4f);
//			num.add(4, 5.9f);
//			Object[] obj = new Object[2];
//			obj[0] = num.toArray();
//			obj[1] = str;
		
			
			//mc.feval("process", obj);
			

			// process string commands if necessary
			if(input instanceof String){

				processString( (String) input );
				return PuppetServer.DONE;

			}
			// otherwise, load the play
			else if(input instanceof FastMap){

				FastMap<String, ModeString> playMap = (FastMap<String,ModeString>) input;
				processPlay(playMap);
				return PuppetServer.DONE;
			}
//			else if(input instanceof ArrayList){
//				// UH OH!
//				ArrayList<Float> number = (ArrayList<Float>) input;
//				processArray(number);
//				return PuppetServer.DONE;
//			}
			// or, something happened!
			else{
				return PuppetServer.ERROR;
			}

		}

		private void processString(String inCmd){

			inCmd = inCmd.trim();
			if(inCmd.equals(PuppetServer.STOP)){
				listening = false;
			}
			// TODO: other commands

		}
		
//		private void processArray(ArrayList<Float> number){
//			int index = 0;
//			index = number.size();
//			index = index-1;
//			float adder = 1.5f;
//			number.add(index, adder);
//			System.out.println("output ArrayList is " + number);
//			
//			// TODO: convert into float[], pass into Matlab "process" function.
//			
//			
//		}

		/**
		 * This function inspects the play and creates a Matlab object representing the
		 * play in its entirety.
		 * @param play
		 */
		private final void processPlay(FastMap<String, ModeString> play){
			System.out.println("Play loaded with " + play.size() + " players.");
			Object[] playArray = new Object [play.size()];
			int i=0;
			
			for(FastMap.Entry<String, ModeString> curr = play.head(), end = play.tail(); (curr = curr.getNext()) != end;){
				
				Mode[] puppetMode = new Mode [curr.getValue().getLength()];
				int j = 0;
				
				ModeString currString = curr.getValue();								
				for(Mode currMode : currString.getModes()){
//					System.out.println(mode.getAction().getClass().getName());
					currMode.getAction().getClass().getSimpleName();
					Mode M = currString.getModes().get(j);
					puppetMode[j] = M;
					j++;
				}
				playArray[i] = puppetMode;
				i++;
			}
			
			
			
			
			mc.feval("build_play", playArray);
			
			System.out.println("length of playArray = " + playArray.length);

		}

		// the server currently handles 4 requests:
		// 1. help -- checks status of connection
		// 2. bye -- disconnects with client
		// 3. testEE -- matlab script with specific syntax
		// 4. testDM -- matlab script with specific syntax
		//		public String processInput( String req ) 
		//		{
		//			String rez = "";
		//			StringTokenizer st;
		//			if( req.startsWith( "help" ) ) { rez = "ok"; }
		//			else if( req.startsWith( "bye" ) ) { rez = "bye"; }
		//			else if( req.startsWith( "testEE" ) )
		//			{
		//				// parse request: "testEE Q B S O iter"
		//				st = new StringTokenizer( req, " " );
		//				String scriptname = "";
		//				if( st.hasMoreTokens() ) { scriptname = st.nextToken(); }
		//				Integer[] params = new Integer[5];
		//				String c;
		//				int i = 0;
		//				while( st.hasMoreTokens() )
		//				{
		//					c = st.nextToken();
		//					int n = Integer.parseInt( c );
		//					params[i] = new Integer( n );
		//					i++;
		//				}
		//				if( i < 5 ) { rez = "not sent"; }
		//				else
		//				{
		//					// send it to matlab
		//					Object[] args = new Object[5];
		//					for( i=0; i<5; i++ ) args[i] = params[i];
		//					if( scriptname.equals( "testEE" ) ) 
		//					{
		//						try
		//						{
		//							Object[] returnVals = new Object[1];
		//							returnVals[0] = mc.blockingFeval( scriptname, args ); 
		//						}
		//						catch( Exception e ) { System.err.println( e ); }
		//					}
		//					rez = "1";
		//				}
		//			}// end_if(testEE)
		//			else if( req.startsWith( "testDM" ) )
		//			{
		//				// parse request: "testDM Q iter"
		//				st = new StringTokenizer( req, " " );
		//				String scriptname = "";
		//				if( st.hasMoreTokens() ) { scriptname = st.nextToken(); }
		//				Integer[] params = new Integer[2];
		//				String c;
		//				int i = 0;
		//				while( st.hasMoreTokens() )
		//				{
		//					c = st.nextToken();
		//					int n = Integer.parseInt( c );
		//					params[i] = new Integer( n );
		//					i++;
		//				}
		//				if( i < 2 ) { rez = "not sent"; }
		//				else
		//				{
		//					// send it to matlab
		//					Object[] args = new Object[2];
		//					for( i=0; i<2; i++ ) args[i] = params[i];
		//					Object[] returnVals = new Object[1];
		//					if( scriptname.equals( "testDM" ) ) 
		//					{
		//						try
		//						{
		//							returnVals[0] = mc.blockingFeval( scriptname, args ); 
		//							//mc.feval(new String("disp"), returnVals);
		//						}
		//						catch( Exception e ) { System.err.println( e ); }
		//					}
		//					// parse output
		//					String filename = new String("cp.txt");
		//					BufferedReader br = null;
		//					try
		//					{
		//						br = new BufferedReader(new FileReader( filename )); 
		//						String text;
		//						while(( text = br.readLine() ) != null )
		//						{
		//							st = new StringTokenizer( text );
		//							while( st.hasMoreTokens() )
		//							{
		//								c = st.nextToken();
		//								rez = c;
		//							}
		//						}
		//					}
		//					catch( IOException ioe ) { System.err.println( ioe ); }
		//				}
		//			}
		//			else rez = "nothing";
		//			return rez;
		//		}
	}
}
