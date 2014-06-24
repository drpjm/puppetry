package edu.gatech.grits.puppetctrl.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import javolution.util.FastList;
import javolution.util.FastMap;

import edu.gatech.grits.puppetctrl.matlab.PuppetClient;
import edu.gatech.grits.puppetctrl.mdl.*;
import edu.gatech.grits.puppetctrl.mdl.util.*;
import edu.gatech.grits.puppetctrl.mdl.lexer.Lexer;
import edu.gatech.grits.puppetctrl.mdl.lexer.LexerException;
import edu.gatech.grits.puppetctrl.mdl.node.Start;
import edu.gatech.grits.puppetctrl.mdl.parser.Parser;
import edu.gatech.grits.puppetctrl.mdl.parser.ParserException;
import edu.gatech.grits.puppetctrl.opt.PlayOptimizer;

/**
 * Panel that has the top level components for initiating and displaying an MDLp compilation.
 * @author pmartin
 *
 */
public class MdlControlPanel extends MyPanel {
	
	private JButton chooseFile;
	private JButton compile;
	private JTextArea currentPlayName;
	
	private File playFile;
	private FastMap<String, ModeString> playMap;
	private PuppetClient puppetClient;
	private boolean isOptimizing = true;
	
	public MdlControlPanel(){
		this.observers = new FastList<PanelObservable>();

		buildContent();
		buildLayout();
		
		try {
			puppetClient = new PuppetClient();
		} catch (Exception e) {
			System.err.println("Error: no Matlab server detected. Optimization disabled.");
			isOptimizing = false;
		}


	}
	
	protected final void buildContent(){

		TitledBorder title;
		title = BorderFactory.createTitledBorder("MDL Loader");
		this.setBorder(title);
		
		currentPlayName = new JTextArea("none");
		currentPlayName.setEditable(false);
		chooseFile = new JButton();
		chooseFile.setText("Choose File...");
		//create the file choosing listener for loading the play file into the compiler
		chooseFile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent ae) {
				//open file chooser
				JFileChooser playChooser = new JFileChooser(System.getProperty("user.dir"));
				int state = playChooser.showDialog(null, "Select");
				playFile = playChooser.getSelectedFile();
				
				if(playFile != null && state == JFileChooser.APPROVE_OPTION){
					System.out.println("File selected for compiling!");
					compile.setEnabled(true);
					currentPlayName.setText(playFile.getName());
				}
				else if(playFile == null && state == JFileChooser.CANCEL_OPTION){
					compile.setEnabled(false);
				}
			}
			
		});

		compile = new JButton();
		compile.setText("Compile");
		compile.setEnabled(false);
		compile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {

				//load file and compile
				try {
					FileReader inFile = new FileReader(playFile);
					StringWriter outStr = new StringWriter();
					while(inFile.ready()){
						outStr.write(inFile.read());						
					}
					System.out.println("Parsing...");
					Parser p = new Parser(new Lexer(new PushbackReader(new StringReader(outStr.toString()))));
					Start s = p.parse();
					
					MDLp compiler = new MDLp();
					s.apply(compiler);
					System.out.println("Done!");
					playMap = compiler.getPlayerModeStrings();
					System.out.println(playMap);
					
					compile.setEnabled(false);
					if(isOptimizing){
						System.out.println("Compiling...");
						puppetClient.sendMessage(playMap);
						
						// TODO: spawn a wait window/progress bar
						JFrame waitFrame = new JFrame();
						waitFrame.setSize(new Dimension(250,100));
						WaitPanel wp = new WaitPanel();
						waitFrame.add(wp);
						waitFrame.setVisible(true);
						
					}

					
					
					// Initial conditions - joint angles only
//					FastList<double[]> X0 = new FastList<double[]>();
//					// last value is the 'time' variable
//					double[] p01 = new double[]{0,0,0,0,0,0,0};
//					double[] p02 = new double[]{0,0,0,0,0,0.0};
//					X0.add(p01);
//					X0.add(p02);
//					
//					// pass play to optimization routine!
//					PlayOptimizer playOpt = new PlayOptimizer(playMap, X0, 0.01, 50);
//					playOpt.optimize();
//					
//					//send message to observers
					for(PanelObservable po : observers){
						po.notifyChange(new ObserverPacket(MessageType.NEW_PLAY, playMap));
//						po.notifyChange(new ObserverPacket(MessageType.SOLUTION, playOpt.getTrajSolutions()));
					}
					
					System.out.println("Finished!");
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserException e) {
					e.printStackTrace();
				} catch (LexerException e) {
					e.printStackTrace();
				}
			}
			
		});
		
	}

	@Override
	protected final void buildLayout() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		chooseFile.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.add(chooseFile);
		
		JLabel play = new JLabel("Current Play:");
		play.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.add(play);
		currentPlayName.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.add(currentPlayName);
		
		compile.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.add(compile);

		this.add(buttonPanel);
	}
	public void notifyChange(ObserverPacket message) {
		MessageType mt = message.getMsgType();
		if(mt == MessageType.RUNNING){
			chooseFile.setEnabled(false);
			compile.setEnabled(false);
		}
		else if(mt == MessageType.STOPPED){
			chooseFile.setEnabled(true);
			currentPlayName.setText("none");
		}
	}
}

