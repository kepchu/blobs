package view.controls;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

import app.InputReceiver;
import data.Colour.ColourCategory;

@SuppressWarnings("serial")
class RGBButtonsBox extends Box implements InputProvider{
	
	//private static ColourCategory colourCategory;
	private InputReceiver inputReceiver;
	private ButtonGroup group;
	private JToggleButton redTB;
	private JToggleButton greenTB;
	private JToggleButton blueTB;
	private JToggleButton neutralTB;
	
	RGBButtonsBox() {
		super(BoxLayout.X_AXIS);
		createButtons();
	}

	public void setInputReceiver(InputReceiver ir) {
		this.inputReceiver = ir;		
	}
	
	private void createButtons() {
		
		group = new ButtonGroup();

		redTB = new JToggleButton("<html><u>R</u>ED");
		redTB.setMaximumSize(redTB.getPreferredSize());//a hack? - otherwise toggle buttons somehow resize despite being in a Box like all other buttons (all other buttons keep their size in their Boxes)
		redTB.setForeground(ColourCategory.R.getDefaultCategoryColor());
		redTB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
				dispatchChange(ColourCategory.R);
			}
		});

		greenTB = new JToggleButton("<html><u>G</u>REEN");
		greenTB.setMaximumSize(greenTB.getPreferredSize());
		greenTB.setForeground(ColourCategory.G.getDefaultCategoryColor());
		greenTB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {	
				if(e.getStateChange() == ItemEvent.SELECTED)
				dispatchChange(ColourCategory.G);
			}
		});

		blueTB = new JToggleButton("<html><u>B</u>LUE");
		blueTB.setMaximumSize(greenTB.getPreferredSize());
		blueTB.setForeground(ColourCategory.B.getDefaultCategoryColor());
		blueTB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
				dispatchChange(ColourCategory.B);
			}
		});

		neutralTB = new JToggleButton("<html><u>N</u>EUTRAL");
		neutralTB.setMaximumSize(neutralTB.getPreferredSize());
		neutralTB.setForeground(ColourCategory.NEUTRAL.getDefaultCategoryColor());
		neutralTB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
				dispatchChange(ColourCategory.NEUTRAL);
			}
		});

		group.add(redTB);
		add(redTB);
		group.add(greenTB);
		add(greenTB);
		group.add(blueTB);
		add(blueTB);
		group.add(neutralTB);
		add(neutralTB);
	}
	
	//propagate change of ButtonGroup's state
	private void dispatchChange (ColourCategory colourCategory) {
		inputReceiver.colourChanged(colourCategory);
	}
	
	//change state of button group from outside
	//(state of the ButtonGroup has to reflect keyboard input)
	void setRGBState (ColourCategory colourCategory) {
		//group.clearSelection();
		
		//setSelected() calls trigger itemListeners added to the buttons
		switch(colourCategory) {
		case R:
			group.setSelected(redTB.getModel(), true);
			break;
		case G:
			group.setSelected(greenTB.getModel(), true);
			break;
		case B:
			group.setSelected(blueTB.getModel(), true);
			break;
		case NEUTRAL:
			group.setSelected(neutralTB.getModel(), true);
			break;
		}
		
	}

}
