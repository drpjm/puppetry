package edu.gatech.grits.puppetctrl.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.SpringLayout;

import edu.gatech.grits.puppetctrl.opt.DataConverter;
//import flanagan.math.Matrix;

import javolution.util.FastList;


/**
 * Contains the three main controlling panels: Serial, Direct, and MDL
 * @author pmartin
 *
 */
public class TopPanel extends MyPanel {

	private SerialControlPanel serialControlPanel;
	private PuppetControlPanel puppetControlPanel;
	private MdlControlPanel mdlControlPanel;
	private MdlDriverPanel mdlDriverPanel;
//	private GraphFrame graphFrame;
//	private JButton graphs;
//	private boolean areGraphsDisplayed;
	
	public TopPanel(){
		this.observers = new FastList<PanelObservable>();
		buildContent();
		buildLayout();
	}
	
	@Override
	protected final void buildContent() {
//		graphFrame = new GraphFrame();
//		graphs = new JButton("Show Graphs");
//		graphs.setEnabled(false);
//		areGraphsDisplayed = false;
//		graphs.addActionListener(new ActionListener(){
//
//			public void actionPerformed(ActionEvent e) {
//				if(!areGraphsDisplayed){
//					graphs.setText("Hide Graphs");
//					areGraphsDisplayed = true;
//					graphFrame.displayGraphs();
//				}
//				else{
//					graphs.setText("Show Graphs");
//					areGraphsDisplayed = false;
//					graphFrame.hideGraphs();
//				}
//			}
//			
//		});
		
		serialControlPanel = new SerialControlPanel();
		puppetControlPanel = new PuppetControlPanel();
		mdlControlPanel = new MdlControlPanel();
		mdlDriverPanel = new MdlDriverPanel();
		
		//this panel listens to ALL sub panels
		serialControlPanel.addObserver(this);
		puppetControlPanel.addObserver(this);
		mdlControlPanel.addObserver(this);
		mdlDriverPanel.addObserver(this);
		
		puppetControlPanel.addObserver(serialControlPanel);
		serialControlPanel.addObserver(puppetControlPanel);

		serialControlPanel.addObserver(mdlDriverPanel);
		mdlDriverPanel.addObserver(serialControlPanel);
		
		mdlDriverPanel.addObserver(mdlControlPanel);
		mdlControlPanel.addObserver(mdlDriverPanel);
	}

	@Override
	protected final void buildLayout() {
		GridLayout gl = new GridLayout(1,2);
		this.setLayout(gl);

		//assemble serial and control sub panels
		JPanel leftSub = new JPanel();
		leftSub.add(serialControlPanel);
		leftSub.add(puppetControlPanel);
		leftSub.add(mdlControlPanel);
//		leftSub.add(graphs);
		leftSub.add(mdlDriverPanel);
		
		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.NORTH, serialControlPanel, 
									0, SpringLayout.NORTH, leftSub);
		layout.putConstraint(SpringLayout.WEST, puppetControlPanel, 
				5, SpringLayout.EAST, serialControlPanel);
		layout.putConstraint(SpringLayout.WEST, mdlControlPanel, 
				5, SpringLayout.EAST, puppetControlPanel);
		
		// button constraints
//		layout.putConstraint(SpringLayout.NORTH, graphs, 
//				5, SpringLayout.SOUTH, mdlControlPanel);
//		layout.putConstraint(SpringLayout.WEST, graphs, 
//				18, SpringLayout.EAST, puppetControlPanel);
		
		// multiple constraints for puppet driver panel
//		layout.putConstraint(SpringLayout.NORTH, mdlDriverPanel, 
//				5, SpringLayout.SOUTH, graphs);
		layout.putConstraint(SpringLayout.WEST, mdlDriverPanel, 
				25, SpringLayout.EAST, puppetControlPanel);
		leftSub.setLayout(layout);
		
		this.add(leftSub);
		
		JPanel mainPanel = new JPanel();
		SpringLayout sl = new SpringLayout();
		mainPanel.setLayout(sl);
	}

	public MdlControlPanel getMdlControlPanel() {
		return mdlControlPanel;
	}

	public SerialControlPanel getSerialControlPanel() {
		return serialControlPanel;
	}

	public PuppetControlPanel getPuppetPanel() {
		return puppetControlPanel;
	}

	public void notifyChange(ObserverPacket message) {
		
		// TODO: listen for new data to graph!
		
		MessageType mt = message.getMsgType();
		if(mt == MessageType.PORT_OPEN){
			System.out.println("Port opened!");
			puppetControlPanel.enableControl();
		}
		else if(mt == MessageType.PORT_CLOSE){
			System.out.println("Port closed!");
			puppetControlPanel.disableControl();
		}
//		if(mt == MessageType.SOLUTION){
//			System.out.println("Optimization solution reached.");
//			graphs.setEnabled(true);
//			// check solutions...
//			if(message.getData() != null){
//				if(message.getData() instanceof FastList){
//					FastList<Matrix> solutions = (FastList<Matrix>) message.getData();
//					Matrix sol = solutions.get(0);
//					graphFrame.setTrajectoryGraph(DataConverter.matrixToXYSeries(sol, 0.01, 0));
//				}
//			}
//		}
	}

}
