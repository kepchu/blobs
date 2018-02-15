package view.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import app.DiscAccess;
import app.InputReceiver;
import view.Stage;
import view.State;

@SuppressWarnings("serial")
public class ThirdBox extends Box implements InputProvider, StageAccesser{

	private InputReceiver ir;
	private Stage stage;
	
	public ThirdBox(int axis) {
		super(axis);
		
		createButtons();
	}

	public ThirdBox () {
		this(BoxLayout.X_AXIS);
	}
	
	
	private void createButtons() {
		JButton saveB = new JButton("Save");
		
		saveB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("stage " + stage.toString() );
				//ir.save(stage.getFrame());
				System.out.println(State.getState().isEmpty());
				ir.save(State.getState());
			}
		});
		add(saveB);
		
		JButton loadB = new JButton("Load");
		loadB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ir.loadFrame();
			}
		});
		add(loadB);
		
		JButton darkB = new JButton("Dark/Bright");
		darkB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if((boolean)State.get("darkMode")) {
					State.put("darkMode", false);
				} else {
					State.put("darkMode", true);
				}
			}
		});
		add(darkB);
		
		JButton drawingB = new JButton("Drawing mode");
		drawingB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if((boolean)State.get("drawingMode")) {
					State.put("drawingMode", false);
				} else {
					State.put("drawingMode", true);
				}
			}
		});
		add(drawingB);
	}

	@Override
	public void setInputReceiver(InputReceiver ir) {
		this.ir = ir;
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;		
	}
}
