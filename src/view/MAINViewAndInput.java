package view;

import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import app.InputReceiver;
import data.BufferableFrames;
import data.Colour.ColourCategory;
import view.controls.InputProvider;
import view.controls.MAINControlsJPanel;
import data.FrameBuffer;
import data.StageDescription;
import data.Vec;

//switch to MouseInputAdapter?
public class MAINViewAndInput implements InputProvider,
MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener, Runnable {
	
	
	private JFrame frame;
	private Stage stage;
	private InputMap inputMap;
	private ActionMap actionMap;

	private InputReceiver inputReceiver;

	volatile static boolean r,g,b,n, ctrl, shift;

	//ControlRGBButtonsJPanel rgbButtons;
	MAINControlsJPanel controlsJPanel;
	
	public MAINViewAndInput (FrameBuffer frameBuffer) {
		System.out.println("ViewAndDataController constr.");
//		this.stage = new Stage(w.getBlobs(), w.getListOfCollisionPoints(), w.getCharges(), w.getGround(),
//				750);
		this.stage = new Stage(frameBuffer);
		initGraphics();
		initInput();	
	}
	
	
	//SETUP
	private void initGraphics() {
		
		frame = new JFrame("demko");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(1000, 800);
		frame.setLocationRelativeTo(null);//window appears in screens centre
		
		frame.getContentPane().add(stage, BorderLayout.CENTER);
		
		(controlsJPanel = new MAINControlsJPanel()).setStage(stage);
		
		frame.getContentPane().add(controlsJPanel, BorderLayout.PAGE_END);
		
		frame.setVisible(true);	
		frame.addComponentListener(this);
	}
	
	private void initInput() {
		stage.addMouseMotionListener(this);
		stage.addMouseWheelListener(this);
		stage.addMouseListener(this);
		inputMap = stage.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actionMap = stage.getActionMap();
		keyBinds();
		
		 KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
				 new KeyEventDispatcher() {
					
					@Override
					public boolean dispatchKeyEvent(KeyEvent e) {

						switch (e.getID()) {
						case KeyEvent.KEY_PRESSED:
							switch(e.getKeyCode()) {
							case KeyEvent.VK_R:
								r = true;
								controlsJPanel.setRGBState(ColourCategory.R);
								break;
							case KeyEvent.VK_G:
								g = true;
								controlsJPanel.setRGBState(ColourCategory.G);
								break;
							case KeyEvent.VK_B:
								b = true;
								controlsJPanel.setRGBState(ColourCategory.B);
								break;
							case KeyEvent.VK_N:
								n = true;
								controlsJPanel.setRGBState(ColourCategory.NEUTRAL);
								break;
								
							case KeyEvent.VK_CONTROL:
								ctrl = true;
								break;
							case KeyEvent.VK_SHIFT:
								shift = true;
								System.out.print(".");
								break;
							}										
						break;
							
						case (KeyEvent.KEY_RELEASED):
							switch(e.getKeyCode()) {
							case KeyEvent.VK_R:
								r = false;
								break;
							case KeyEvent.VK_G:
								g = false;
								break;
							case KeyEvent.VK_B:
								b = false;
								break;
								
							case KeyEvent.VK_CONTROL:
								ctrl = false;
								break;
							case KeyEvent.VK_SHIFT:
								shift = false;
								System.out.print("#");
								break;
							}
						case (KeyEvent.KEY_TYPED):
							switch (e.getKeyCode()) {
							case KeyEvent.VK_C:
								if (e.isShiftDown()) {
									System.out.println("c+shift");
									inputReceiver.switchChargeTypes();
								} else {
									System.out.println("c");
									inputReceiver.switchChargeColourCategories();
								}
								break;
							case KeyEvent.VK_Z:
								inputReceiver.gravityDown();//TODO
							}
						}
						
						return false;
					}
				});
		
		
	}
	public void setInputReceiver(InputReceiver uir) {
		this.inputReceiver = uir;
		controlsJPanel.setInputReceiver(uir);
		
		stage.initFinished();
		
	}
	
	public void run() {
				
		//TODO: updating here so Stage won't know anything about inputReceiver/World -a  makeshift solution...
		
//		inputReceiver.udateStageDimensions(
//				stage.descaledStageCentreX(),
//				stage.descaledStageCentreY()
//				);
		//TODO: 1 updating all this every frame is stupid - fix this. stage has appropriate listener already registered
		//TODO 
		Vec gravCentre = new Vec(stage.camera.descaledStageCentreX(),
				stage.camera.descaledStageCentreY());
		Vec minXY = stage.camera.descaledMinXY();
		Vec maxXY = stage.camera.descaledMaxXY();
		StageDescription sd = new StageDescription(gravCentre, minXY, maxXY);
		inputReceiver.udateStageDimensions(sd);
		stage.repaint();
	}
	
	//INPUT
	//MouseListener:
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		if (SwingUtilities.isLeftMouseButton(arg0)) {
			//System.out.println("mouseClicked, leftClick X: " + arg0.getX() + ", Y: " + arg0.getY());
			
			if (r) {
				System.out.println("click while r pressed");			
				inputReceiver.rLeftClick(
						stage.camera.descaleX(arg0.getX()),
						stage.camera.descaleY(arg0.getY()));
				return;
			}
			
			if (g) {
				System.out.println("click while g pressed");
				inputReceiver.gLeftClick(
						stage.camera.descaleX(arg0.getX()),
						stage.camera.descaleY(arg0.getY()));
				return;
			}
			if (b) {
				System.out.println("click while b pressed");
				inputReceiver.bLeftClick(
						stage.camera.descaleX(arg0.getX()),
						stage.camera.descaleY(arg0.getY()));
				return;
			}
			
			inputReceiver.leftClickAt(
					stage.camera.descaleX(arg0.getX()),
					stage.camera.descaleY(arg0.getY()));
			
		} else if (SwingUtilities.isRightMouseButton(arg0)) {
			//System.out.println("mouseClicked, rightClick X: " + arg0.getX() + ", Y: " + arg0.getY());
			inputReceiver.rightClickAt(
					stage.camera.descaleX(arg0.getX()),
					stage.camera.descaleY(arg0.getY()));
			
		} else if (SwingUtilities.isMiddleMouseButton(arg0)){
			
			inputReceiver.togglePointerRepulse();
			
		} else {
			System.out.println("mouseClicked, unknown mouse button - X: " + arg0.getX() + ", Y: " + arg0.getY());
			System.out.println("Switching collisions drawing");
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		inputReceiver.setMouseInside(true);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		inputReceiver.setMouseInside(false);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	
	// MouseMotionListener:
	@Override
	public void mouseDragged(MouseEvent arg0) {
		//System.out.println("mouseDragged X: " + arg0.getX() + ", Y: " + arg0.getY());
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		//System.out.println("View: mouseMoved: " + arg0.getX() + ", " + arg0.getY());
		stage.updatePointerPosition(arg0.getX(), arg0.getY());
		
		inputReceiver.updateMousePointerPosition(
				stage.camera.descaleX(arg0.getX()),
				stage.camera.descaleY(arg0.getY()));
	}
	
	// MouseWheelListener
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//TODO: smooth scrolling & zooming
		// if wheel rotated forward
		if (e.getWheelRotation() < 0) {		
			if (ctrl) stage.zoomIn();
			else stage.scrollDown();
		}
			
		// wheel rotated back
		if (e.getWheelRotation() > 0) {		
			if (ctrl) stage.zoomOut();
			else stage.scrollUp();
		}
	}
	
	
	
	private void keyBinds() {
		inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "rAction");
		inputMap.put(KeyStroke.getKeyStroke("LEFT"), "lAction");
		inputMap.put(KeyStroke.getKeyStroke("UP"), "uAction");
		inputMap.put(KeyStroke.getKeyStroke("DOWN"), "dAction");
		inputMap.put(KeyStroke.getKeyStroke("SPACE"), "sBarAction");
		inputMap.put(KeyStroke.getKeyStroke("PAGE_UP"), "pageUpAction");
		inputMap.put(KeyStroke.getKeyStroke("PAGE_DOWN"), "pageDownAction");
		inputMap.put(KeyStroke.getKeyStroke("HOME"), "homeAction");
		inputMap.put(KeyStroke.getKeyStroke("END"), "endAction");
		inputMap.put(KeyStroke.getKeyStroke("INSERT"), "insertAction");
		inputMap.put(KeyStroke.getKeyStroke("DELETE"), "deleteAction");
		inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "escAction");
		/*inputMap.put(KeyStroke.getKeyStroke("C"), "cAction");
		//inputMap.put(KeyStroke.getKeyStroke("VK_CONTROL"), "ctrlAction");
		inputMap.put(KeyStroke.getKeyStroke("P"), "pAction");
		inputMap.put(KeyStroke.getKeyStroke("R"), "rAction");
		inputMap.put(KeyStroke.getKeyStroke("G"), "gAction");
		inputMap.put(KeyStroke.getKeyStroke("B"), "bAction");*/
		
		actionMap.put("rAction", rArrowAction);
		actionMap.put("lAction", lArrowAction);
		actionMap.put("uAction", uArrowAction);
		actionMap.put("dAction", dArrowAction);
		actionMap.put("sBarAction", sBarAction);
		actionMap.put("pageUpAction", pageUpAction);
		actionMap.put("pageDownAction", pageDownAction);
		actionMap.put("homeAction", homeAction);
		actionMap.put("endAction", endAction);
		actionMap.put("insertAction", insertAction);
		actionMap.put("deleteAction", deleteAction);
		actionMap.put("escAction", escAction);
		/*actionMap.put("cAction", cAction);
		actionMap.put("ctrlAction", ctrlAction);
		actionMap.put("pAction", pAction);
		actionMap.put("rAction", rAction);
		actionMap.put("gAction", gAction);
		actionMap.put("bAction", bAction);*/
	}
	
	@SuppressWarnings("serial")
	Action rArrowAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("rAction");
		}
	};
	@SuppressWarnings("serial")
	Action lArrowAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("lAction");
		}
	};
	@SuppressWarnings("serial")
	Action uArrowAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("uAction");
		}
	};
	@SuppressWarnings("serial")
	Action dArrowAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("dAction");
		}
	};
	@SuppressWarnings("serial")
	Action sBarAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			inputReceiver.switchCollisions();
		}
	};
	@SuppressWarnings("serial")
	Action pageUpAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			inputReceiver.pageUpAction();
		}
	};
	@SuppressWarnings("serial")
	Action pageDownAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			inputReceiver.pageDownAction();
		}
	};
	@SuppressWarnings("serial")
	Action homeAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			inputReceiver.homeAction();
		}
	};
	@SuppressWarnings("serial")
	Action endAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			inputReceiver.endAction();
		}
	};
	@SuppressWarnings("serial")
	Action escAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("escAction");
		}
	};
	@SuppressWarnings("serial")
	Action insertAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			inputReceiver.insertAction();
		}
	};

	@SuppressWarnings("serial")
	Action deleteAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("deleteAction");
		}
	};
	@SuppressWarnings("serial")
	Action pAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("pAction");
		}
	};
	/*@SuppressWarnings("serial")
	Action cAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			if (shift) {
				System.out.println(" C + SHIFT");
				inputReceiver.cShiftAction();
			}
			else {
				System.out.println(" C ALONE");
				inputReceiver.cAction();
			}
		}
	};
	@SuppressWarnings("serial")
	Action ctrlAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("ctrlAction");
		}
	};
	@SuppressWarnings("serial")
	Action rAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("r");
		}
	};
	@SuppressWarnings("serial")
	Action gAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("g");
		}
	};
	@SuppressWarnings("serial")
	Action bAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("b");
		}
	};*/
	
	//ComponentListener:
		@Override
		public void componentHidden(ComponentEvent e) {
			// TODO Auto-generated method stub
			System.out.println("JFrame componentHidden");
		}
		@Override
		public void componentMoved(ComponentEvent e) {
			
		}
		@Override
		public void componentResized(ComponentEvent e) {
			System.out.println("JFrame componentResized");
			
		}
		@Override
		public void componentShown(ComponentEvent e) {
			// TODO Auto-generated method stub
			System.out.println("JFrame componentShown");
		}


		public void setFrameBuffer(BufferableFrames frameBuffer) {
			// TODO Auto-generated method stub
			
		}

		//END OF INPUT	
}
