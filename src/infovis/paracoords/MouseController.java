package infovis.paracoords;

import infovis.scatterplot.Model;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class MouseController implements MouseListener, MouseMotionListener {
	private View view = null;
	private Model model = null;
	Shape currentShape = null;
	private double clickedX = 0.0;
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		view.clearData();
		
		Map<Rectangle2D.Double, String> map = view.getSelectionRectangle();
		for (Rectangle2D.Double rect : map.keySet()) {
			if (rect.contains(e.getX(), e.getY())) {
				view.dragLabel = true;
				view.draggedLabel = map.get(rect);
				clickedX = e.getX();
				return;
			}
		}
		
		//Iterator<Data> iter = model.iterator();
		view.getMarkerRectangle().setRect(e.getX(), e.getY(), 0, 0);
		view.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		view.dragLabel = false;
	}

	public void mouseDragged(MouseEvent e) {
//		view.clearData();
		if (view.dragLabel) {
			view.xOffset = e.getX() - clickedX;
			
		} else {		
			Rectangle2D rect = view.getMarkerRectangle();
			double width = e.getX() - rect.getMinX();
			double height = e.getY() - rect.getMinY();

			// geht erstmal nur nach unten rechts
			// rect.setRect(rect.getMinX(), rect.getMinY(), e.getX() -
			// rect.getMinX() , e.getY() - rect.getMinY());
			rect.setRect(rect.getMinX(), rect.getMinY(),
					e.getX() - rect.getMinX(), e.getY() - rect.getMinY());
		}
		view.repaint();
	}

	public void mouseMoved(MouseEvent e) {

	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
