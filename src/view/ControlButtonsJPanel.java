package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import app.InputReceiver;
import data.ChargePoint.Charger;

@SuppressWarnings("serial")
class ControlButtonsJPanel extends JPanel implements InputProvider {

	
	RGBButtonsBox rgbButtons;
	private Charger ch;
	private InputReceiver ir;

	public ControlButtonsJPanel() {
		//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));	
		createButtons();
		
	}

	private void createButtons() {	
		Box effectsCompositionBox  = createEffectsBox();
		Box controlButtonsBox = createControlBox();
		//Box controlsBox = Box.createHorizontalBox();
		
		add(effectsCompositionBox);		
		add(controlButtonsBox);		
	}

	private Box createControlBox() {
		//create a box containing 2 horizontal boxes
		// box 1
		Box switchesBox = Box.createHorizontalBox();
		
		JButton wrapB = new JButton("Edges wrapped");
		wrapB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();
				if (b.getText() == "Edges: wrapped") {
					b.setText("Edges: bouncy");
					dispatchChange("unwrapEdges");
				} else {
					b.setText("Edges: wrapped");
					dispatchChange("wrapEdges");
				}
							
			}
		});
		switchesBox.add(wrapB);
		
		JButton gravityB = new JButton("Switch gravity");
		gravityB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("gravity");
				
			}
		});
		switchesBox.add(gravityB);
		
		JButton switchChargesB = new JButton("Swich actions");
		switchChargesB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("charges");
				
			}
		});
		switchesBox.add(switchChargesB);
		
		JButton switchColorsB = new JButton("Swich colors");
		switchColorsB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("charges");
				
			}
		});
		switchesBox.add(switchColorsB);
			
		JButton collisionsB = new JButton("Interaction");
		collisionsB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("collisions");
				
			}
		});
		switchesBox.add(collisionsB);
		add(switchesBox);
			
		//Second horizontal box ##########################################################
		Box controlBox = Box.createHorizontalBox();
		//controlBox.setAlignmentX(RIGHT_ALIGNMENT);
		
		JButton lockB = new JButton("Lock");
		lockB.addActionListener(new ActionListener() {
			//create confirmation dialog
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("lock");
			}
		});
		controlBox.add(lockB);
		
		JButton saveB = new JButton("Save");
		saveB.addActionListener(new ActionListener() {
			//create confirmation dialog
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("save");
			}
		});
		controlBox.add(saveB);
		
		JButton undoB = new JButton("Remove last");
		undoB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("undo");				
			}
		});
		controlBox.add(undoB);
		
		JButton resetB = new JButton("RESET");
		undoB.addActionListener(new ActionListener() {
			//create confirmation dialog
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("reset");
			}
		});
		controlBox.add(resetB);
		
		Box result = Box.createVerticalBox();
		result.add(switchesBox);
		result.add(controlBox);
		return result;
	}

	private Box createEffectsBox() {
		
		//RGB box is a field so it may be accessed via setInputReceiver & for keyboard access
		rgbButtons = new RGBButtonsBox();
		add(rgbButtons);
		
		//Effects box
		Box effectsBox = Box.createHorizontalBox();
			
		ButtonGroup group = new ButtonGroup();
		//this loop will automatically reflect changes to enum that stores all effects
		//this is why it is dependent directly on the enum
		for (int i = 0; i < Charger.values().length; i++) {
			//variable "fc" is final so it may be referenced in the anonymous listener class
			final Charger fc = Charger.values()[i];

			JToggleButton tb = new JToggleButton(fc.toString());
			group.add(tb);//add to ButtonGroup
			effectsBox.add(tb);//add to JPanel

			tb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispatchChange(fc);
				}
			});
		}
		
		Box result = Box.createVerticalBox();
		result.add(rgbButtons); result.add(effectsBox);
		return result;
	}

	protected void dispatchChange(Charger charger) {
		ir.chargeChanged(charger);
	}

	protected void dispatchChange(String in) {
		switch (in) {
		case "wrapEdges":
			ir.wrapEdges();
			break;
		case "unwrapEdges":
			ir.unwrapEdges();
		case "gravityDown":
			ir.gravityDown();
			break;
		case "gravityCentre":
			ir.gravityCentre();
			break;
		case "collisions":
			ir.switchCollisions();
		case "undo":
			ir.undo();
			break;
		case "reset":
			ir.reset();
			break;
		default:
			throw new IllegalArgumentException(getClass().getSimpleName() + " - unrecognised action");
		}

	}

	@Override
	public void setInputReceiver(InputReceiver ir) {
		this.ir = ir;
		rgbButtons.setInputReceiver(ir);
	}
}