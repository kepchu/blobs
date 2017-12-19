package view;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToggleButton.ToggleButtonModel;

import app.InputReceiver;
import data.Colour.ColourCategory;

@SuppressWarnings("serial")
class ControlRGBButtonsJPanel extends JPanel implements InputProvider{

	final static int ROWS = 1, COLUMNS = 4, NO_OF_BUTTONS = ROWS * COLUMNS;
	JToggleButton[] tbs = new JToggleButton[NO_OF_BUTTONS];
	
	//private static ColourCategory colourCategory;
	private InputReceiver inputReceiver;
	ButtonGroup group;
	JToggleButton redTB;
	JToggleButton greenTB;
	JToggleButton blueTB;
	JToggleButton neutralTB;
	
	ControlRGBButtonsJPanel() {
		super(new GridLayout(ROWS, COLUMNS));
		createButtons();
	}

	public void setInputReceiver(InputReceiver ir) {
		this.inputReceiver = ir;		
	}
	
	private void createButtons() {
		group = new ButtonGroup();

		redTB = new JToggleButton("<html><u>R</u>ED");
		//redTB.setOpaque(true);
		//redTB.setBackground(ColourCategory.R.getDefaultCategoryColor());
		redTB.setForeground(ColourCategory.R.getDefaultCategoryColor());
		redTB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
				dispatchChange(ColourCategory.R);
			}
		});

		greenTB = new JToggleButton("<html><u>G</u>REEN");
		greenTB.setForeground(ColourCategory.G.getDefaultCategoryColor());
		greenTB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {	
				if(e.getStateChange() == ItemEvent.SELECTED)
				dispatchChange(ColourCategory.G);
			}
		});

		blueTB = new JToggleButton("<html><u>B</u>LUE");
		blueTB.setForeground(ColourCategory.B.getDefaultCategoryColor());
		blueTB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
				dispatchChange(ColourCategory.B);
			}
		});

		neutralTB = new JToggleButton("<html><u>N</u>EUTRAL");
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
	
	//propagate change of the group's state
	private void dispatchChange (ColourCategory colourCategory) {
		inputReceiver.colourChanged(colourCategory);
	}
	
	//change state of button group from outside (buttons have to reflect keyboard input)
	void setState (ColourCategory colourCategory) {
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
