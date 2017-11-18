package view;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
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
import data.FrameData;
import data.BufferableFrames;
import data.FrameBuffer;
import data.World;

//switch to MouseInputAdapter?
public class ViewAndInputController implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {
	
	
	private JFrame frame;
	private Stage stage;
	private InputMap inputMap;
	private ActionMap actionMap;

	private BufferableFrames frameBuffer;
	private InputReceiver inputReceiver;

	boolean r,g,b,ctrl, shift;
	
	
	
	int stageX = Integer.MIN_VALUE, stageY = Integer.MIN_VALUE;
	
	public ViewAndInputController (FrameBuffer frameBuffer) {
		System.out.println("ViewAndDataController constr.");
		this.frameBuffer = frameBuffer;
//		this.stage = new Stage(w.getBlobs(), w.getListOfCollisionPoints(), w.getCharges(), w.getGround(),
//				750);
		int initialCameraPosition = 750;
		this.stage = new Stage(initialCameraPosition, frameBuffer);
		initGraphics();
		initInput();	
	}
	
	
	//SETUP
	private void initGraphics() {
		frame = new JFrame("tytul okna");
		frame.add(stage);
		frame.setSize(800, 800);
		frame.setLocationRelativeTo(null);//window appears in screens centre
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
								break;
							case KeyEvent.VK_G:
								g = true;
								break;
							case KeyEvent.VK_B:
								b = true;
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
									inputReceiver.cShiftAction();
								} else {
									System.out.println("c");
									inputReceiver.cAction();
								}
								break;
							}
						}
						
						return false;
					}
				});
		
		
	}
	public void setUserInputReceiver(InputReceiver uir) {
		this.inputReceiver = uir;			
	}
	
	public void update() {
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
						stage.descaleX(arg0.getX()),
						stage.descaleY(arg0.getY()));
				return;
			}
			
			if (g) {
				System.out.println("click while g pressed");
				inputReceiver.gLeftClick(
						stage.descaleX(arg0.getX()),
						stage.descaleY(arg0.getY()));
				return;
			}
			if (b) {
				System.out.println("click while b pressed");
				inputReceiver.bLeftClick(
						stage.descaleX(arg0.getX()),
						stage.descaleY(arg0.getY()));
				return;
			}
			
			inputReceiver.leftClickAt(
					stage.descaleX(arg0.getX()),
					stage.descaleY(arg0.getY()));
			
		} else if (SwingUtilities.isRightMouseButton(arg0)) {
			//System.out.println("mouseClicked, rightClick X: " + arg0.getX() + ", Y: " + arg0.getY());
			inputReceiver.rightClickAt(
					stage.descaleX(arg0.getX()),
					stage.descaleY(arg0.getY()));
			
		} else if (SwingUtilities.isMiddleMouseButton(arg0)){
			System.out.println("mouseClicked, middleClick X: " + arg0.getX() + ", Y: " + arg0.getY());
		} else {
			System.out.println("mouseClicked, unknown mouse button - X: " + arg0.getX() + ", Y: " + arg0.getY());
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub	
	}
	
	// MouseMotionListener:
	@Override
	public void mouseDragged(MouseEvent arg0) {
		//System.out.println("mouseDragged X: " + arg0.getX() + ", Y: " + arg0.getY());
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// System.out.println("mouseMoved X: " + arg0.getX() + ", Y: " + arg0.getY());
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
			inputReceiver.sBarAction();
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
			// TODO Auto-generated method stub
			//System.out.println("JFrame componentMoved");
			int newStageX = e.getComponent().getX();
			int newStageY = e.getComponent().getY();
			//System.out.println("x: " + newStageX + ", y: " + newStageY);
			
			//in order to skip the first call at the time of creation of GUI
			if (stageX == Integer.MIN_VALUE) {
				//System.out.println("MIN_VALUE");
				stageX = newStageX;
				stageY = newStageY;
				return;
			}
			
			//componentMoved is called multiple times with the same values of X and Y
			//the below should filter out these repeated calls
			if (newStageX == stageX && newStageY == stageY) {
				//System.out.println("old and new stage deltas are the same");
				return;}
			
			//System.out.println("new stagex: " + (newStageX));
			//System.out.println("new stagey: " + (newStageY));
			inputReceiver.stageMoved (newStageX - stageX, newStageY - stageY);
			stageX = newStageX; stageY = newStageY;
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
