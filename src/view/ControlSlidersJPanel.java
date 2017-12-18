package view;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;

import app.InputReceiver;

@SuppressWarnings("serial")
class ControlSlidersJPanel extends JPanel implements InputProvider{

	JSlider gravitySlider, heatSlider, zoomSlider, speedSlider;
	InputReceiver inputReceiver;
	
	ControlSlidersJPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		gravitySlider = new JSlider(0, 1000000000, 2000000);
		add(gravitySlider);
		heatSlider = new JSlider(0, 1000000000, 2000000);
		add(heatSlider);
		zoomSlider = new JSlider(0, 1000000000, 2000000);
		add(zoomSlider);
		speedSlider = new JSlider(0, 1000000000, 2000000);
		add(speedSlider);

	}
	
	public void setInputReceiver(InputReceiver ir) {
		this.inputReceiver = ir;
		
	}
}
