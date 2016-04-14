package mydraw;
// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
// Copyright (c) 1997 by David Flanagan
// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
// You may study, use, modify, and distribute it for non-commercial purposes.
// For any commercial use, see http://www.davidflanagan.com/javaexamples

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
// OPFAAA
/** The application class.  Processes high-level commands sent by GUI */
public class Draw {
	/** main entry point.  Just create an instance of this application class */
	public static void main(String[] args) { new Draw(); }

	/** Application constructor:  create an instance of our GUI class */
	public Draw() { window = new DrawGUI(this); }

	protected JFrame window;

	/** This is the application method that processes commands sent by the GUI */
	public void doCommand(String command) {
		if (command.equals("clear")) {          // clear the GUI window
			// It would be more modular to include this functionality in the GUI
			// class itself.  But for demonstration purposes, we do it here.
			Graphics g = window.getGraphics();
			g.setColor(window.getBackground());
			g.fillRect(0, 0, window.getSize().width, window.getSize().height);
		} else if (command.equals("quit")) {      // quit the application
			window.dispose();                         // close the GUI
			System.exit(0);                           // and exit.
		}
	}
}

/** This class implements the GUI for our application */
class DrawGUI extends JFrame {
	Draw app;      // A reference to the application, to send commands to.
	Color color;

	/**
	 * The GUI constructor does all the work of creating the GUI and setting
	 * up event listeners.  Note the use of local and anonymous classes.
	 */
	public DrawGUI(Draw application) {
		super("Draw");        // Create the window
		app = application;    // Remember the application reference

		// selector for drawing modes
		JComboBox shape_chooser = new JComboBox();
		shape_chooser.addItem("Scribble");
		shape_chooser.addItem("Rectangle");
		shape_chooser.addItem("Oval");

		// selector for drawing colors
		JComboBox color_chooser = new JComboBox();
		color_chooser.addItem("Blue");
		color_chooser.addItem("Red");
		color_chooser.addItem("Green");
		color_chooser.addItem("Black");

		// Create two buttons
		JButton clear = new JButton("Clear");
		JButton quit = new JButton("Quit");

		// Set a LayoutManager, and add the choosers and buttons to the window.
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		// Setzt einen Panel, die Buttons in einer Leiste hat.
		NavigationPanel navigationPanel = new NavigationPanel(new FlowLayout());
		navigationPanel.add(new JLabel("Shape:"));
		navigationPanel.add(shape_chooser);
		navigationPanel.add(new JLabel("Color:"));
		navigationPanel.add(color_chooser);
		navigationPanel.add(quit);
		navigationPanel.add(clear);
		navigationPanel.setBackground(Color.magenta);

		// Setzt den Panel, auf dem gemalt wird
		final JPanel drawPanel = new JPanel();
		drawPanel.setBackground(Color.white);

		cp.add(navigationPanel, BorderLayout.NORTH);
		cp.add(drawPanel, BorderLayout.CENTER);


		// Here's a local class used for action listeners for the buttons
		class DrawActionListener implements ActionListener {
			private String command;

			public DrawActionListener(String cmd) {
				command = cmd;
			}

			public void actionPerformed(ActionEvent e) {
				app.doCommand(command);
			}
		}

		// Define action listener adapters that connect the  buttons to the app
		clear.addActionListener(new DrawActionListener("clear"));

		quit.addActionListener(new DrawActionListener("quit"));

		// this class determines how mouse events are to be interpreted,
		// depending on the shape mode currently set
		class ShapeManager implements ItemListener {
			DrawGUI gui;

			abstract class ShapeDrawer
					extends MouseAdapter implements MouseMotionListener
			{
				public void mouseMoved(MouseEvent e) { /* ignore */ }
			}

			// if this class is active, the mouse is interpreted as a pen
			class ScribbleDrawer extends ShapeDrawer {
				int lastx, lasty;

				public void mousePressed(MouseEvent e) {
					lastx = e.getX(); lasty = e.getY();
				}

				public void mouseDragged(MouseEvent e) {
					Graphics g = gui.getGraphics();
					int x = e.getX(), y = e.getY();
					g.setColor(gui.color);
					g.setPaintMode();
					g.drawLine(lastx, lasty, x, y);
					lastx = x; lasty = y;
				}
			}

			// if this class is active, rectangles are drawn
			class RectangleDrawer extends ShapeDrawer {
				int pressx, pressy;
				int lastx = -1, lasty = -1;

				// mouse pressed => fix first corner of rectangle
				public void mousePressed(MouseEvent e) {
					pressx = e.getX(); pressy = e.getY();
				}

				// mouse released => fix second corner of rectangle
				// and draw the resulting shape
				public void mouseReleased(MouseEvent e) {
					Graphics g = gui.getGraphics();
					if(lastx != -1)
					{
						// first undraw a rubber rect
						g.setXORMode(gui.color);
						g.setColor(gui.getBackground());
						doDraw(pressx, pressy, lastx, lasty, g);
						lastx = -1; lasty = -1;
					}
					// these commands finish the rubberband mode
					g.setPaintMode();
					g.setColor(gui.color);
					// draw the finel rectangle
					doDraw(pressx, pressy, e.getX(), e.getY(), g);
				}

				// mouse released => temporarily set second corner of rectangle
				// draw the resulting shape in "rubber-band mode"
				public void mouseDragged(MouseEvent e) {
					Graphics g = gui.getGraphics();
					// these commands set the rubberband mode
					g.setXORMode(gui.color);
					g.setColor(gui.getBackground());
					if(lastx != -1)
					{
						// first undraw previous rubber rect
						doDraw(pressx, pressy, lastx, lasty, g);

					}
					lastx = e.getX(); lasty = e.getY();
					// draw new rubber rect
					doDraw(pressx, pressy, lastx, lasty, g);
				}

				public void doDraw(int x0, int y0, int x1, int y1, Graphics g) {
					// calculate upperleft and width/height of rectangle
					int x = Math.min(x0, x1);
					int y = Math.min(y0, y1);
					int w = Math.abs(x1-x0);
					int h = Math.abs(y1-y0);
					// draw rectangle
					g.drawRect(x, y, w, h);
				}
			}

			// if this class is active, ovals are drawn
			class OvalDrawer extends RectangleDrawer {
				public void doDraw(int x0, int y0, int x1, int y1, Graphics g) {
					int x = Math.min(x0, x1);
					int y = Math.min(y0, y1);
					int w = Math.abs(x1-x0);
					int h = Math.abs(y1-y0);
					// draw oval instead of rectangle
					g.drawOval(x, y, w, h);
				}
			}

			ScribbleDrawer scribbleDrawer = new ScribbleDrawer();
			RectangleDrawer rectDrawer = new RectangleDrawer();
			OvalDrawer ovalDrawer = new OvalDrawer();
			ShapeDrawer currentDrawer;

			public ShapeManager(DrawGUI itsGui) {
				gui = itsGui;
				// default: scribble mode
				currentDrawer = scribbleDrawer;
				// activate scribble drawer
				gui.addMouseListener(currentDrawer);
				gui.addMouseMotionListener(currentDrawer);
			}

			// reset the shape drawer
			public void setCurrentDrawer(ShapeDrawer l) {
				if(currentDrawer == l)
					return;

				// deactivate previous drawer
				gui.removeMouseListener(currentDrawer);
				gui.removeMouseMotionListener(currentDrawer);
				// activate new drawer
				currentDrawer = l;
				gui.addMouseListener(currentDrawer);
				gui.addMouseMotionListener(currentDrawer);
			}

			// user selected new shape => reset the shape mode
			public void itemStateChanged(ItemEvent e) {
				if(e.getItem().equals("Scribble"))
				{
					setCurrentDrawer(scribbleDrawer);
				}
				else if(e.getItem().equals("Rectangle"))
				{
					setCurrentDrawer(rectDrawer);
				}
				else if(e.getItem().equals("Oval"))
				{
					setCurrentDrawer(ovalDrawer);
				}
			}
		}

		shape_chooser.addItemListener(new ShapeManager(this));

		class ColorItemListener implements ItemListener {

			// user selected new color => store new color in DrawGUI
			public void itemStateChanged(ItemEvent e) {
				if(e.getItem().equals("Black"))
				{
					color = Color.black;
				}
				else if(e.getItem().equals("Green"))
				{
					color = Color.green;
				}
				else if(e.getItem().equals("Red"))
				{
					color = Color.red;
				}
				else if(e.getItem().equals("Blue"))
				{
					color = Color.blue;
				}
			}
		}

		color_chooser.addItemListener(new ColorItemListener());

		// Handle the window close request similarly
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { app.doCommand("quit"); }
		});

		// Finally, set the size of the window, and pop it up
		this.setSize(500, 400);
		this.setBackground(Color.white);
		this.show();
	}


}

/**
 * @author Sabrina Buczko und Tom Kasatek (Gruppe 2)
 */
class NavigationPanel extends JPanel {
	public NavigationPanel(FlowLayout flowLayout) {
		super(flowLayout);
	}

	public void clearBackground(JPanel background){
		background.setBackground(Color.white);
	}
}
