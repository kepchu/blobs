package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import app.InputReceiver;
import data.ChargePoint.Charger;

@SuppressWarnings("serial")
class ControlButtonsJPanel extends JPanel implements InputProvider {

	final static int ROWS = 2, COLUMNS = 5, NO_OF_BUTTONS = ROWS * COLUMNS;
	JToggleButton[] buttons = new JToggleButton[NO_OF_BUTTONS];

	Charger ch;
	InputReceiver ir;

	public ControlButtonsJPanel() {
		super(new GridLayout(ROWS, COLUMNS));
		setLayout(new FlowLayout());
		createButtons();
	}

	private void createButtons() {
		ButtonGroup group = new ButtonGroup();

		//Charger buttons
		for (int i = 0; i < Charger.values().length; i++) {
			final Charger fc = Charger.values()[i];

			JToggleButton tb = new JToggleButton(fc.toString());
			group.add(tb);
			add(tb);

			tb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispatchChange(fc);
				}
			});
		}

		//Other settings buttons
		JButton wrapB = new JButton("Wrap edges");
		wrapB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("wrap");			
			}
		});
		add(wrapB);
		
		JButton gravityB = new JButton("Gravity");
		gravityB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("gravity");
				
			}
		});
		add(gravityB);
		
		JButton collisionsB = new JButton("Interaction");
		gravityB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("collisions");
				
			}
		});
		add(collisionsB);
		
		JButton undoB = new JButton("UNDO");
		undoB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("undo");				
			}
		});
		add(undoB);
		
		JButton resetB = new JButton("RESET");
		undoB.addActionListener(new ActionListener() {
			//create confirmation dialog
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchChange("reset");				
			}
		});
		add(resetB);
		
	}

	protected void dispatchChange(Charger charger) {
		ir.chargeChanged(charger);
	}

	protected void dispatchChange(String in) {
		switch (in) {
		case "wrap":
			ir.switchStageWrap();
			break;
		case "gravity":
			ir.switchGravity();
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
	}
}