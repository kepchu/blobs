package view_controls;

import java.awt.Component;
import java.awt.Container;
import java.awt.im.InputMethodRequests;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import app.InputReceiver;
import data.Colour.ColourCategory;
import view.Stage;

@SuppressWarnings("serial")
public
class MAINControlsJPanel extends JPanel implements InputProvider, StageAccesser{
	
	private ControlButtonsJPanel buttons;
	
	public MAINControlsJPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		buttons = new ControlButtonsJPanel();
		add(buttons);
		add(new ControlSlidersJPanel());
				
		makeAllChildrenUnfocusable(this);
	}

	@Override
	public void setStage(Stage stage) {
		setAllStageAccessers(stage, this);
	}
	//traverse recurrently all children and set Stage wherever possible
		public static void setAllStageAccessers (Stage s, Container in) {
						
			for (Component c : in.getComponents()) {
				System.out.println(c.getClass().getName() + " - stage");
				if (c instanceof StageAccesser) {
					((StageAccesser)c).setStage(s);				
				}
				if (c instanceof Container) {
					setAllStageAccessers(s, (Container)c);
				}
			}
		}
	
	public void setInputReceiver (InputReceiver ir) {
		setAllInputReceivers(ir, this);
	}
	
	//traverse recurrently all children and set InputReceiver wherever possible
	public static void setAllInputReceivers (InputReceiver ir, Container in) {
				
		for (Component c : in.getComponents()) {
			System.out.println(c.getClass().getName() + " - ir");
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
