package edu.gatech.grits.puppetctrl.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.gatech.grits.puppetctrl.comm.bioloid.*;
import edu.gatech.grits.puppetctrl.util.Conversion;

import javolution.util.FastList;

/**
 * Panel class which encapsulates controlling the puppet directly.
 * 
 * @author pmartin
 *
 */
public class DirectControlPanel extends JPanel {

	//TODO: eventually extend in order to allow different Dynamixel commands (i.e. led, torque)

	//motion command subpanel
	private JComboBox commandBox;
	private JComboBox availableMotorBox;
	private JTextArea selectedMotorName;
	private JTextField speedField;
	private JTextField goalPosField;
	private ButtonGroup convertSelectGroup;
	private JRadioButton degreeButton;
	private JRadioButton radianButton;

	private Conversion conversionSelected;
	private int selectedMotor;
	
	public DirectControlPanel(){
		buildContent();
		buildLayout();
	}
	
	protected final void buildContent() {
		
		String[] def = {"NONE"};
		selectedMotorName = new JTextArea("none");
		selectedMotorName.setColumns(4);

		//motor commands
		//TODO: implement commands
		commandBox = new JComboBox(def);

		availableMotorBox = new JComboBox(def);
		availableMotorBox.setEnabled(false);
		availableMotorBox.addItemListener(new ItemListener(){

			public void itemStateChanged(ItemEvent ie) {
				if(ie.getStateChange() == ItemEvent.SELECTED){
					selectedMotor = ((Integer)availableMotorBox.getSelectedItem()).intValue();
					selectedMotorName.setText(Integer.toString(selectedMotor));
				}
			}
			
		});
		
		speedField = new JTextField(4);
		speedField.setEnabled(false);
		speedField.setText("0");
		goalPosField = new JTextField(4);
		goalPosField.setEnabled(false);
		goalPosField.setText("0");
		
		//conversion selector
		degreeButton = new JRadioButton("Degrees");
		degreeButton.setEnabled(false);
		degreeButton.setSelected(true);
		conversionSelected = Conversion.DEG;
		degreeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				conversionSelected = Conversion.DEG;
			}
		});
		radianButton = new JRadioButton("Radians");
		radianButton.setEnabled(false);
		radianButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				conversionSelected = Conversion.RAD;
			}
		});
		convertSelectGroup = new ButtonGroup();
		convertSelectGroup.add(this.degreeButton);
		convertSelectGroup.add(this.radianButton);
		
	}

	protected final void buildLayout() {
		//motion subpanel
		JPanel motionSub = new JPanel();
		motionSub.setLayout(new GridLayout(4,1));
		
		JPanel motorSelSub = new JPanel();
		motorSelSub.add(new JLabel("Motor #"));
		motorSelSub.add(availableMotorBox);
		motorSelSub.add(new JLabel("Selected Motor: "));
		motorSelSub.add(selectedMotorName);
		motionSub.add(motorSelSub);
		
		JPanel paramSub = new JPanel();
		paramSub.add(new JLabel("Speed:"));
		paramSub.add(speedField);
		paramSub.add(new JLabel("Position:"));
		paramSub.add(goalPosField);
		motionSub.add(paramSub);
		
		JPanel selectSub = new JPanel();
		selectSub.add(this.degreeButton);
		selectSub.add(this.radianButton);
		motionSub.add(selectSub);
		
		JPanel buttonSub = new JPanel();
		motionSub.add(buttonSub);
		
		//layout for this panel
		JPanel mainSub = new JPanel();
		BoxLayout bl = new BoxLayout(mainSub, BoxLayout.PAGE_AXIS);
		mainSub.setLayout(bl);
		mainSub.add(Box.createRigidArea(new Dimension(0,5)));
		mainSub.add(motionSub);
		this.add(mainSub);
	}

	public int getSelectedMotor() {
		return selectedMotor;
	}

	public Float getRange() {
		return Float.valueOf(goalPosField.getText());
	}

	public Float getSpeed() {
		return Float.valueOf(speedField.getText());
	}

	public Conversion getConversionSelected() {
		return conversionSelected;
	}

	public final void enableMotion(){
		this.speedField.setEnabled(true);
		this.goalPosField.setEnabled(true);
		this.availableMotorBox.setEnabled(true);
		this.degreeButton.setEnabled(true);
		this.radianButton.setEnabled(true);
	}
	
	public final void setMotorList(FastList<Integer> motors){
		DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
		for(Integer i : motors){
			dcbm.addElement(i);
		}
		availableMotorBox.setModel(dcbm);
	}
	
	public final void disableMotion(){
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement("NONE");
		availableMotorBox.setModel(model);
		selectedMotorName.setText("none");
		
		this.speedField.setEnabled(false);
		this.goalPosField.setEnabled(false);
		this.availableMotorBox.setEnabled(false);
		this.degreeButton.setEnabled(false);
		this.radianButton.setEnabled(false);
	}
}
