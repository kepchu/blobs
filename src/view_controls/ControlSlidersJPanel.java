package view_controls;

import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import app.InputReceiver;

@SuppressWarnings("serial")
class ControlSlidersJPanel extends JPanel implements InputProvider{

	JSlider gravitySlider, heatSlider, forceSlider, speedSlider;
	InputReceiver ir;
	
	ControlSlidersJPanel() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Box labels = Box.createVerticalBox();
		add(labels);
		Box sliders = Box.createVerticalBox();
		add(sliders);
//		BoxLayout base = new BoxLayout(this, BoxLayout.X_AXIS);
//		BoxLayout sliders = new BoxLayout(this, BoxLayout.Y_AXIS);
		
		//this.getLayout().addLayoutComponent(arg0, arg1);
		
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		
		Box gravityBox = Box.createHorizontalBox();
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
		gravityBox.add(new JLabel("Gravity: "));
		gravityBox.add(gravitySlider);
		add(gravityBox);
		
		Box heatBox = Box.createHorizontalBox();
		heatSlider = new JSlider(0, 1000000000, 333333333);
		//heatSlider = new JSlider(-500000000, 500000000, 333333333);
		heatSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int rawReading = ((JSlider)e.getSource()).getValue();
				double result = rawReading/333333333.3333333;
				System.out.println(result);
				dispatchChange("temperature", result);
			}
		});
		heatBox.add(new JLabel("Energy: "));
		heatBox.add(heatSlider);	
		add(heatBox);
		
		Box forceBox = Box.createHorizontalBox();
		forceSlider = new JSlider(0, 1000000000, 0);
		forceBox.add(new JLabel("/Force:/ "));
		forceBox.add(forceSlider);	
		add(forceBox);
		
		Box speedBox = Box.createHorizontalBox();
		speedSlider = new JSlider(1, 8, 1);
		speedSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				dispatchChange("speed", ((JSlider)e.getSource()).getValue());				
			}
		});
		speedBox.add(new JLabel("Speed: "));
		speedBox.add(speedSlider);
		add(speedBox);
	}
	
	private void dispatchChange(String type, double value) {
		switch (type) {
		case "gravity":
			ir.setGravity(value);
			break;
		case "temperature":
			ir.setTemperature(value);
		case "speed":
			ir.videoFrameFactorChanged((int)value);
		}
	}
	
	public void setInputReceiver(InputReceiver ir) {
		this.ir = ir;
		
	}
}
