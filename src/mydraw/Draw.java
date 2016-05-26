package mydraw;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Die Applikations Klasse.  Projeziert die von GUI gesendeten Befehle.
 *
 * @author Tom Kastek und Sabrina Buczko
 */
public class Draw {
	/**
	 * Startpunkt.  Kreirt eine Instanz der Apllikations Klasse
	 */

	public static void main(String[] args) throws ColorException, IOException {
		Draw x = new Draw();
	}

	/**
	 * Applikations Konstruktor:  Erstellt eine Instanz der GUI Klasse
	 */
	public Draw(){
		window = new DrawGUI(this);
		
	}

	// Die GUI Klasse wird der Applikation hinzugefügt
	protected DrawGUI   window;
	// Zum Speichern und Erhalten der Bilder wird die Hilfsklasse mybmbfile genutzt.
	protected MyBMPFile mybmpfile;

	/**
	 * API-Methode um die Breite des Bildes zu erhalten
	 *
	 * @return Gibt die Breite des Bildes
	 */
	public int getWidth() {
		return window.drawPanel.getWidth();
	}

	/**
	 * API-Methode um die Höhe des Bildes zu erhalten
	 *
	 * @return Gibt die Höhe des Bildes
	 */
	public int getHeight() {
		return window.drawPanel.getHeight();
	}

	/**
	 * API-Methode um die Breite des Bildes zu setzen
	 *
	 * @param width Wert den die Breite haben soll
	 */
	public void setWidth(int width) {
		window.drawPanel.setPreferredSize(new Dimension(width, getHeight()));
		window.drawPanel.setSize(new Dimension(width, getHeight()));
		window.pack();
	}

	/**
	 * API-Methode um die Höhe des Bildes zu setzen
	 *
	 * @param height Wert den die Höhe haben soll
	 */
	public void setHeight(int height) {
		window.drawPanel.setPreferredSize(new Dimension(getWidth(), height));
		window.drawPanel.setSize(new Dimension(getWidth(), height));
		window.pack();
	}

	/**
	 * API-Methode um die Farbe in der gemalt wird zu ändern
	 *
	 * @param new_color Farbe, in der gemalt werden soll
	 */
	public void setFGColor(String new_color) throws ColorException {
		if(window.colorMap.containsKey(new_color)){
			window.color = window.colorMap.get(new_color);
			window.colorName = new_color;
		} else {
			throw new ColorException();
		}
		
	}

	/**
	 * API-Methode um die Farbe in der aktuell gezeichnet wird zu erhalten
	 *
	 * @return die Farbe als String
	 */
	public String getFGColor() {
		return window.colorName;
	}

	/**
	 * API-Methode um die Hintergrundfarbe des Bildes zu setzen
	 *
	 * @param new_color die Farbe die gesetzt werden soll
	 */
	public void setBGColor(String new_color) throws ColorException {
		if (new_color.equals("Black")) {
			window.newBGColor = Color.black;
		} else if (new_color.equals("Green")) {
			window.newBGColor = Color.green;
		} else if (new_color.equals("Red")) {
			window.newBGColor = Color.red;
		} else if (new_color.equals("Blue")) {
			window.newBGColor = Color.blue;
		} else if (new_color.equals("White")) {
			window.newBGColor = Color.white;
		} else {
			throw new ColorException();
		}
	}

	/**
	 * API-Methode um die Hintergrundfarbe des Bildes zu erhalten
	 *
	 * @return die Farbe als String
	 */
	public String getBGColor() {
		return window.drawPanel.getBackground().toString();
	}

	/**
	 * API-Methode um ein Dreieck in das Bild zu malen
	 *
	 * @param upper_left  Startpunkt
	 * @param lower_right Endpunkt
	 */
	public void drawRectangle(Point upper_left, Point lower_right) {
		int x = (int) Math.min(upper_left.getX(), lower_right.getX());
		int y = (int) Math.min(upper_left.getY(), lower_right.getY());
		int w = (int) Math.abs(upper_left.getX() - lower_right.getX());
		int h = (int) Math.abs(upper_left.getY() - lower_right.getY());
		// draw rectangle

//		Graphics g = window.drawPanel.getGraphics();
		//		g.setColor(window.color);
		//		g.setPaintMode();
		//		g.drawRect(x, y, w, h);
		window.commandQueue.addRectangle(window.color, x, y, w, h);
	}

	/**
	 * API-Methode um ein Oval in das Bild zu malen
	 *
	 * @param upper_left  Startpunkt
	 * @param lower_right Endpunkt
	 */
	public void drawOval(Point upper_left, Point lower_right) {
		int x = (int) Math.min(upper_left.getX(), lower_right.getX());
		int y = (int) Math.min(upper_left.getY(), lower_right.getY());
		int w = (int) Math.abs(upper_left.getX() - lower_right.getX());
		int h = (int) Math.abs(upper_left.getY() - lower_right.getY());
		// draw oval instead of rectangle
//		Graphics g = window.drawPanel.getGraphics();
//		g.setColor(window.color);
//		g.setPaintMode();
//		g.drawOval(x, y, w, h);
		window.commandQueue.addOval(window.color, x, y, w, h);
	}

	/**
	 * API-Methode um ein Polygon in das Bild zu malen
	 *
	 * @param points Eine Liste der Punkte die gesetzt werden sollen
	 */
	public void drawPolyLine(java.util.List<Point> points) {
//		Graphics g = window.drawPanel.getGraphics();
//		g.setColor(window.color);
//		g.setPaintMode();
		Graphics2D g2D;
		for (int i = 1; i <= points.size(); i++) {
			if (i == points.size()) {
//				g.drawLine((int) points.get(i - 1).getX(), (int) points.get(i - 1).getY(), (int) points.get(0).getX(),
//						(int) points.get(0).getY());
				window.commandQueue.addLine(window.color, (int) points.get(i - 1).getX(), (int) points.get(i - 1).getY(), (int) points.get(0).getX(),
						(int) points.get(0).getY());
			} else {
//				g.drawLine((int) points.get(i - 1).getX(), (int) points.get(i - 1).getY(), (int) points.get(i).getX(),
//						(int) points.get(i).getY());
				window.commandQueue.addLine(window.color, (int) points.get(i - 1).getX(), (int) points.get(i - 1).getY(), (int) points.get(i).getX(),
												(int) points.get(i).getY());
			}
		}
	}

	/**
	 * API-Methode um das Bild zu erhalten
	 *
	 * @return das Bild als Image
	 */
	public Image getDrawing() {
		return window.getDrawing();
	}

	/**
	 * API-Methode um das Bild zu löschen
	 */
	public void clear() {
		window.drawPanel.updateUI();
		window.drawPanel.setBackground(Color.white);
	}

	/**
	 * API-Methode, die ein Bild von selbst malt
	 */
	public void autoDraw() throws ColorException {
		setWidth(500);
		setHeight(500);
		setBGColor("Blue");
		drawOval(new Point(10, 10), new Point(200, 200));
		setFGColor("Red");
		drawRectangle(new Point(5, 5), new Point(300, 300));
		setFGColor("Black");
		drawRectangle(new Point(3, 3), new Point(3, 3));
		drawOval(new Point(4, 4), new Point(4, 4));
		LinkedList points = new LinkedList<Point>();
		points.add(new Point(20, 50));
		points.add(new Point(480, 480));
		points.add(new Point(430, 333));
		points.add(new Point(222, 222));
		drawPolyLine(points);
		setFGColor("Green");
		drawOval(new Point(400, 400), new Point(22, 22));
	}

	/**
	 * API-Methode, die das Bild als bmp Datei abspeichert
	 */
	public void writeImage(Image img, String filename) throws IOException {
		mybmpfile.write(filename, img);
	}

	/**
	 * API-Methode, die ein Bild als bmb Datei einliest
	 */
	public Image readImage(String filename) throws IOException {
		return mybmpfile.read(filename);
	}
}

