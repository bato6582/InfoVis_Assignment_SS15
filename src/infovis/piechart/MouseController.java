package infovis.piechart;

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
	private boolean ctr_pressed = false;
	Shape currentShape = null;
	private double clicked_x = 0.0;
	private double clicked_y = 0.0;
	
	private KeyboardController key_controller = null;
	
	public void set_key_controller (KeyboardController k) {
		key_controller = k;
	}
	
	public void mouseClicked(MouseEvent e) {
		for (int i = 0; i <= view.level; i++) {
	
			
			if (view.segments.containsKey(i)) {
				for (Segment s : view.segments.get(i)) {
					if (s.poly.contains(e.getX(), e.getY())) {
						
						if (key_controller.getCtrl_pressed()) {
							 if (i == view.level) {
								 System.out.println("SegmentClicked");
								 if (view.selected_segments.contains(s.label)) {
									 view.selected_segments.remove(s.label);
								 }
								 view.selected_segments.add(s.label);
							 }
						} else {
							view.clicked(s.label, i);
							view.selected_segments.clear();
						}
						return;
					}
				}
				
			}
		}

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		

		if (view.timeline_rectangle.contains(e.getX(), e.getY())) {
			view.change_time = true;
			clicked_x = e.getX();
			clicked_y = e.getY();
		}
		

		//Iterator<Data> iter = model.iterator();
		//view.getMarkerRectangle().setRect(e.getX(), e.getY(), 0, 0);
		view.repaint();
//		System.out.println("pressed KeyCode: ");
	}

	public void mouseReleased(MouseEvent e) {
		view.change_time = false;
		view.repaint();
	}

	public void mouseDragged(MouseEvent e) {

		
		double distance_x = e.getX() - clicked_x;
		double distance_y = e.getY() - clicked_y;
		if(view.change_time){
			int x_pos = (int) (view.timeline_rectangle.getX() + distance_x);
			int y_pos = (int) (view.timeline_rectangle.getY() + distance_y);
			if (x_pos > view.timeline_x_start && x_pos < view.timeline_x_end - view.pixel_per_year) {
				view.timeline_rectangle.setRect(x_pos, view.timeline_y - view.pixel_per_year, view.pixel_per_year, view.pixel_per_year * 2);
			}			
		}
		
		clicked_x = e.getX();
		clicked_y = e.getY();
		
		
//		view.clearData();
				
//			Rectangle2D rect = view.getMarkerRectangle();
//			double width = e.getX() - rect.getMinX();
//			double height = e.getY() - rect.getMinY();
//
//			// geht erstmal nur nach unten rechts
//			// rect.setRect(rect.getMinX(), rect.getMinY(), e.getX() -
//			// rect.getMinX() , e.getY() - rect.getMinY());
//			rect.setRect(rect.getMinX(), rect.getMinY(),
//					e.getX() - rect.getMinX(), e.getY() - rect.getMinY());
		

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
