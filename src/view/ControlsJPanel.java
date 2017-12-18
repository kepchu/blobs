package view;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JPanel;

import app.InputReceiver;

class ControlsJPanel extends JPanel {
	
	JPanel[] children;
	ControlsJPanel(JPanel... children) {
		this.children = children;
//		setLayout(new FlowLayout());
//		add(a);
//		add(b);
				
//		setLayout(new BorderLayout());
////		add(a, BorderLayout.WEST);
////		add(b, BorderLayout.EAST);
//		add(a, BorderLayout.NORTH);
//		add(b, BorderLayout.SOUTH);
		
		setLayout(new GridLayout(2, 2));
		for (JPanel jp : children) add(jp);
		
		makeAllChildrenUnfocusable(this);
	}

	void setInputReceiver (InputReceiver ir) {
		for (JPanel jp : children) {
			((InputProvider)jp).setInputReceiver(ir);
		}
	}
	
	void makeAllChildrenUnfocusable(Container c) {
		for (Component child : c.getComponents()) {
			if (child instanceof Container) {
				child.setFocusable(false);
				makeAllChildrenUnfocusable((Container) child);
			} else if (child instanceof Component) {
				System.out.println(
				"In " + getClass().getSimpleName() +
				" A component but not a container " + child.getClass());
				child.setFocusable(false);
			}		
		}
	}
}
