package infovis.scatterplot;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;


public class MouseController implements MouseListener, MouseMotionListener {

	private Model model = null;
	private View view = null;


	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent e) {
		view.clearData();
	
		//Iterator<Data> iter = model.iterator();
		view.getMarkerRectangle().setRect(e.getX(), e.getY(), 0, 0);
		view.repaint();
	}

	public void mouseReleased(MouseEvent arg0) {
		
	}

	public void mouseDragged(MouseEvent e) {
		Rectangle2D rect =  view.getMarkerRectangle();
		double width=  e.getX() - rect.getMinX();
		double height = e.getY() - rect.getMinY();
		
		
		//geht erstmal nur nach unten rechts
//		rect.setRect(rect.getMinX(), rect.getMinY(), e.getX() - rect.getMinX() , e.getY() - rect.getMinY());
		rect.setRect(rect.getMinX(), rect.getMinY(), e.getX() - rect.getMinX() , e.getY() - rect.getMinY());
		view.repaint();
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void setModel(Model model) {
		this.model  = model;	
	}

	public void setView(View view) {
		this.view  = view;
	}

}
