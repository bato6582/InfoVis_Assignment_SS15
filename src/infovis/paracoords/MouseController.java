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
		
		for (String label : view.labelSelectionRectMap.keySet()) {
			Rectangle2D.Double rect = view.labelSelectionRectMap.get(label);
			if (rect.contains(e.getX(), e.getY())) {
//				view.selectionRectangles.remove(rect);
				view.selectedRectangle = rect;
				view.dragLabel = true;
				view.draggedLabel = label;
				clickedX = e.getX();
				view.oldXOffset = 0.0;
				return;
			}
		}
		
		//Iterator<Data> iter = model.iterator();
		view.getMarkerRectangle().setRect(e.getX(), e.getY(), 0, 0);
		view.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		view.dragLabel = false;
		view.oldXOffset = view.xOffset;
		view.xOffset = 0.0;
//		System.out.println("HIIIIII");
		if (!view.draggedLabel.equals("")) {
			view.checkAxes();
		}
		view.repaint();
	}

	public void mouseDragged(MouseEvent e) {
//		view.clearData();
		if (view.dragLabel) {
//			System.out.println("(" + e.getX() + " - " + clickedX +") - " + view.xOffset + " = " + ((e.getX() - clickedX) - view.xOffset));
			view.oldXOffset = view.xOffset;
			view.xOffset = (e.getX() - clickedX);
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
