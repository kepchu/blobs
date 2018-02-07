package view_controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import app.InputReceiver;
import data.ChargePoint.Charger;
import view.Stage;

@SuppressWarnings("serial")
class ControlButtonsJPanel extends JPanel implements InputProvider, StageAccesser {

	
	RGBButtonsBox rgbButtons;
	private InputReceiver ir;
	private Stage stage;//used by only one button
	private ThirdBox thirdBox;

	public ControlButtonsJPanel() {
		//this.stage = stage;
		//WrapLayout: http://tips4java.wordpress.com/2008/11/06/wrap-layout/
		setLayout(new WrapLayout());
		createButtons();		
	}

	private void createButtons() {	
		Box effectsCompositionBox  = createEffectsBox();
		Box controlButtonsBox = createControlBox();
		thirdBox = new ThirdBox();
		
		add(effectsCompositionBox);		
		add(controlButtonsBox);
		add(thirdBox);
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
			final boolean [] flag = {true};
			@Override
			public void actionPerformed(ActionEvent e) {
				if (flag[0]) {
					dispatchChange("gravityCentre");
					flag[0] = false;
				} else {
					dispatchChange("gravityDown");
					flag[0] = true;
				}			
			}
		});
		switchesBox.add(gravityB);
		
		JButton switchChargesB = new JButton("Swich actions");
		switchChargesB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("chargeTypes");
				
			}
		});
		switchesBox.add(switchChargesB);
		
		JButton switchColoursB = new JButton("Swich colors");
		switchColoursB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("chargeColours");
				
			}
		});
		switchesBox.add(switchColoursB);
			
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
		
		JButton lockSidesB = new JButton("Auto-zoom to window size");
		lockSidesB.addActionListener(new ActionListener() {
			//create confirmation dialog
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lockSidesB.getText().equals("Auto-zoom to window size")) {
					dispatchChange("lockSides");
					lockSidesB.setText("Interact with window edges");
					lockSidesB.setToolTipText("Auto-zooming to match window.\nZooming or window resizing will not affect the action");
				} else {
					lockSidesB.setText("Auto-zoom to window size");				
					lockSidesB.setToolTipText("Window frame interacts with the content");
					dispatchChange("unlockSides");
				}
			}
		});
		controlBox.add(lockSidesB);
		
		JButton saveB = new JButton("Kill momentum");
		saveB.addActionListener(new ActionListener() {
			//create confirmation dialog
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("halt");
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
		
		JButton killAllChargesB = new JButton("Remove all");
		killAllChargesB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("killCharges");				
			}
		});
		controlBox.add(killAllChargesB);
		
		JButton resetB = new JButton("RESET");
		resetB.addActionListener(new ActionListener() {
			//create confirmation dialog
			@Override
			public void actionPerformed(ActionEvent e) {
				ir.reset();
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
			break;
		case "gravityDown":
			ir.gravityDown();
			break;
		case "gravityCentre":
			ir.gravityCentre();
			break;
		case "collisions":
			ir.switchCollisions();
			break;
		case "unlockSides":
			ir.lockStageSidesToWindow(true);
			stage.setAutoZoom(false);//########################################stage
			break;
		case "lockSides":
			ir.lockStageSidesToWindow(false);
			stage.setAutoZoom(true);//#########################################stage
			break;
		case "halt":
			ir.multiplyVelocitiesBy(0);
			break;
		case "undo":
			ir.removeLastCharge();
			break;
		case "killCharges":
			ir.removeAllCharges();
			break;
		case "chargeTypes":
			ir.switchChargeTypes();
			break;
		case "chargeColours":
			ir.switchChargeColourCategories();
			break;
		case "reset":
			ir.reset();
			break;
		default:
			throw new IllegalArgumentException(getClass().getSimpleName() + " " + in);
		}

	}

	@Override
	public void setInputReceiver(InputReceiver ir) {
		this.ir = ir;
		//rgbButtons.setInputReceiver(ir);
		MAINControlsJPanel.setAllInputReceivers(ir, this);
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
		//thirdBox.setStage(stage);
		MAINControlsJPanel.setAllStageAccessers(stage, this);
	}
	
}