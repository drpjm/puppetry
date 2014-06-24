package edu.gatech.grits.puppetctrl.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import be.abeel.graphics.eps.ColorMode;
import be.abeel.graphics.eps.EpsGraphics;

/**
 * Class that encapsulates the chart panels from JFreeChart.
 * @author pmartin
 *
 */
public class GraphFrame extends JFrame {

	private ChartPanel trajChartPanel;
	private ChartPanel costChartPanel;
	private JFreeChart trajChart;
	private JFreeChart costChart;
	
	private JButton exportTraj;
	private JButton exportCost;
	JPanel topPanel;
	JPanel bottomPanel;

	private int trajGraphCount = 0;
	private int costGraphCount = 0;
	private boolean isTrajReady = false;
	private boolean isCostReady = false;
	
	private final String PATH_ROOT = System.getProperty("user.dir");
	
	public GraphFrame(){
		buildContent();
		buildLayout();
	}

	private final void buildContent(){
		topPanel = new JPanel();
		bottomPanel = new JPanel();
		
		exportTraj = new JButton("To EPS");
		exportTraj.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				EpsGraphics epsg;
				try {
					epsg = new EpsGraphics("Traj Output", new FileOutputStream(PATH_ROOT + "/traj"+trajGraphCount+".eps"),0,0,600,400,ColorMode.COLOR_CMYK);
					trajChart.draw(epsg, new Rectangle2D.Double(0,0,600,400));
					epsg.close();
					System.out.println("Exported tajectory graph: " + PATH_ROOT + "/traj"+trajGraphCount+".eps");
					trajGraphCount++;
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
			
		});
		exportTraj.setEnabled(true);
		
		exportCost = new JButton("To EPS");
		exportCost.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				EpsGraphics epsg;
				try {
					epsg = new EpsGraphics("Cost Output", new FileOutputStream(PATH_ROOT + "/cost"+costGraphCount+".eps"),0,0,600,400,ColorMode.COLOR_CMYK);
					costChart.draw(epsg, new Rectangle2D.Double(0,0,600,400));
					epsg.close();
					System.out.println("Exported cost graph: " + PATH_ROOT + "/cost"+costGraphCount+".eps");
					costGraphCount++;
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
				
			}
			
		});
		exportCost.setEnabled(true);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(false);
		this.setMinimumSize(new Dimension(400,350));
		this.setSize(new Dimension(500,500));
		this.setLocation(new Point(640,0));
		this.setTitle("Compilation Output");
	}
	
	private final void buildLayout(){

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.setBackground(Color.white);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.setBackground(Color.white);
		
		mainPanel.add(topPanel);
		mainPanel.add(bottomPanel);
		this.add(mainPanel);
		
	}
	
	/**
	 * Displays the trajectory and cost graphs.
	 */
	private final void loadGraphs(){
		if(isTrajReady){
			topPanel.add(trajChartPanel);
			exportTraj.setAlignmentX(CENTER_ALIGNMENT);
			topPanel.add(exportTraj);			
		}
		
		if(isCostReady){
			bottomPanel.add(costChartPanel);
			exportCost.setAlignmentX(CENTER_ALIGNMENT);
			bottomPanel.add(exportCost);
		}
	}
	
	public final void displayGraphs(){
		loadGraphs();
		this.setVisible(true);
	}
	
	public final void hideGraphs(){
		this.setVisible(false);
	}
	
	/**
	 * This function accepts an XYSeriesCollection of the system trajectory solution
	 * that will then be added to a ChartPanel for display. 
	 * @param xyCollection
	 */
	public void setTrajectoryGraph(final XYSeriesCollection xyCollection){
		
		String title = "Puppet Angle Trajectories";
		trajChart = ChartFactory.createXYLineChart(title, "Time (s)", "Angle (rad)", 
				xyCollection, PlotOrientation.VERTICAL, false, false, false);
		trajChart.setBackgroundPaint(Color.white);
		trajChart.setBorderPaint(Color.black);
		this.trajChartPanel = new ChartPanel(trajChart);
		isTrajReady = true;
	}
	
	/**
	 * This function accepts an XYSeriesCollection of the cost calculation
	 * over the alogrithm iterations.
	 * @param xyCollection
	 */
	public void setCostGraph(final XYSeriesCollection xyCollection){
		
		String title = "Cost Functional vs. Iteration";
		costChart = ChartFactory.createXYLineChart(title, "Iteration", "Cost", 
				xyCollection, PlotOrientation.VERTICAL, false, false, false);
		costChart.setBackgroundPaint(Color.white);
		costChart.setBorderPaint(Color.black);
		this.costChartPanel = new ChartPanel(costChart);
		isCostReady = true;
		
	}
	
}
