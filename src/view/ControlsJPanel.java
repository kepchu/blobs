package view;

import java.awt.Component;
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import app.InputReceiver;
import data.Colour.ColourCategory;

@SuppressWarnings("serial")
class ControlsJPanel extends JPanel {
	
	private ControlButtonsJPanel buttons;
	ControlsJPanel() {
		buttons = new ControlButtonsJPanel();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(buttons);
		add(new ControlSlidersJPanel());
				
		makeAllChildrenUnfocusable(this);
	}

	void setInputReceiver (InputReceiver ir) {
		setAllInputReceivers(ir, this);
	}
	
	//traverse recurrently all children and set InputReceiver wherever possible
	private void setAllInputReceivers (InputReceiver ir, Container in) {
		for (Component c : in.getComponents()) {
			if (c instanceof InputProvider) {
				((InputProvider)c).setInputReceiver(ir);				
			}
			if (c instanceof Container) {
				setAllInputReceivers(ir, (Container)c);
			}

		}
	}
	
	//traverse recurrently all children and setFocusable(false) wherever possible
	private void makeAllChildrenUnfocusable(Container c) {
		for (Component child : c.getComponents()) {
			if (child instanceof Container) {
				child.setFocusable(false);
				makeAllChildrenUnfocusable((Container) child);
			} else if (child instanceof Component) {
				System.out.println(
				"In " + getClass().getSimpleName() +
				" A component but not a container: " + child.getClass());
				child.setFocusable(false);
			}		
		}
	}

	//this passes rgb selection to rgb buttons group
	public void setRGBState(ColourCategory colourCategory) {
		buttons.rgbButtons.setRGBState(colourCategory);		
	}
}
