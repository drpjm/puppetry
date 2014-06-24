package edu.gatech.grits.puppetctrl.app;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import javolution.util.FastMap;

import edu.gatech.grits.puppetctrl.gui.*;
import edu.gatech.grits.puppetctrl.mdl.util.ModeString;
import edu.gatech.grits.puppetctrl.util.PuppetDriver;

/**
 * Simple gui frame to let someone pick and run MDLp programs.
 * 
 * @author pmartin
 *
 */
public class PuppetDriveFrame extends JFrame implements PanelObservable {

	private static final boolean debug = true;
	
	private ConsolePanel console;
	private PuppetControlPanel puppetControlPanel;
	private MdlControlPanel mdlControlPanel;
	private SerialControlPanel serialControlPanel;
	private MdlDriverPanel mdlDriverPanel;
	
	private JSplitPane split;
//	private PuppetDriver puppetDriver;
	
	private FastMap<String, ModeString> currPlayMap;
	
	public PuppetDriveFrame(){
		currPlayMap = new FastMap<String, ModeString>();
		buildContent();
		buildLayout();
	}
	
	private final void buildContent(){
		this.setTitle("Puppet Controller");
		this.setEnabled(true);
		this.setVisible(true);
		this.setSize(new Dimension(400, 400));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		serialControlPanel = new SerialControlPanel();
		mdlDriverPanel = new MdlDriverPanel();
		mdlControlPanel = new MdlControlPanel();
		puppetControlPanel = new PuppetControlPanel();

		if(!debug){
			console = new ConsolePanel();
		}
		
		mdlControlPanel.addObserver(this);
		mdlControlPanel.addObserver(mdlDriverPanel);
		mdlControlPanel.addObserver(mdlControlPanel);
		serialControlPanel.addObserver(this);
		mdlDriverPanel.addObserver(this);
		mdlDriverPanel.addObserver(serialControlPanel);
		puppetControlPanel.addObserver(serialControlPanel);
		serialControlPanel.addObserver(puppetControlPanel);
	}

	private final void buildLayout(){

		JPanel top = new JPanel();
		top.add(serialControlPanel);
		top.add(mdlControlPanel);
		top.add(mdlDriverPanel);
		top.add(puppetControlPanel);
		if(!debug){
			split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, console);
		}
		else{
			split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, new JPanel());
		}
		this.add(split);
	}
	
	public void notifyChange(ObserverPacket message) {
		if(message.getMsgType() == MessageType.NEW_DATA){
			//pull out and assign new play map
			FastMap<String, ModeString> data = (FastMap<String, ModeString>) message.getData();
			this.currPlayMap = data;
		}
	}

	private static void createGui(){
		PuppetDriveFrame pdf = new PuppetDriveFrame();
	}

	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable(){

			public void run() {
				createGui();
			}
			
		});

	}

}
