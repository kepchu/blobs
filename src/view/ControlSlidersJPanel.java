package view;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import app.InputReceiver;

@SuppressWarnings("serial")
class ControlSlidersJPanel extends JPanel implements InputProvider{

	JSlider gravitySlider, heatSlider, zoomSlider, speedSlider;
	InputReceiver ir;
	
	ControlSlidersJPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		gravitySlider = new JSlider(0, 1000000000, 40000000);
		gravitySlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int rawReading = ((JSlider)e.getSource()).getValue();
				double result = rawReading/200000000.0;
				System.out.println(result);
				dispatchChange("gravity", result);
				
			}
		});
		add(gravitySlider);
		
		heatSlider = new JSlider(0, 1000000000, 2000000);
		heatSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int rawReading = ((JSlider)e.getSource()).getValue();
				double result = rawReading/333333333.3333333;
				System.out.println(result);
				dispatchChange("temperature", result);
			}
		});
		add(heatSlider);
		
		zoomSlider = new JSlider(0, 1000000000, 2000000);
		add(zoomSlider);
		speedSlider = new JSlider(0, 1000000000, 2000000);
		add(speedSlider);

	}
	
	private void dispatchChange(String type, double value) {
		switch (type) {
		case "gravity":
			ir.setGravity(value);
			break;
		case "temperature":
			ir.setTemperature(value);
		}
	}
	
	public void setInputReceiver(InputReceiver ir) {
		this.ir = ir;
		
	}
}
