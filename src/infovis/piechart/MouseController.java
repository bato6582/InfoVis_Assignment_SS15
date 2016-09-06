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
	Shape currentShape = null;
	private double clicked_x = 0.0;
	private double clicked_y = 0.0;
	
	
	
	// click on segments and get selected segments if ctrl was pressed
	public void mouseClicked(MouseEvent e) {
		for (int i = 0; i <= view.level; i++) {
	
			if (view.segments.containsKey(i)) {
				for (Segment s : view.segments.get(i)) {
					if (s.poly.contains(e.getX(), e.getY())) {
						
						if (view.ctrl_pressed) {
							 if (i == view.level) {
								 if (view.selected_segments.contains(s.label)) {
									 view.selected_segments.remove(s.label);
								 } else {
									 view.selected_segments.add(s.label);
								 }
							 }
						} else {
							view.clicked(s.label, i);
							view.selected_segments.clear();
							view.selection_chosen = false;
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

	// move rectangle of timeline by dragging (mouseDragged)
	public void mousePressed(MouseEvent e) {
		if (view.timeline_rectangle.contains(e.getX(), e.getY())) {
			view.change_time = true;
			clicked_x = e.getX();
			clicked_y = e.getY();
		} else if (view.diagram_year_triangle.contains(e.getX(), e.getY())) {
			view.change_time_diagram = true;
			clicked_x = e.getX();
			clicked_y = e.getY();
		}
		
		view.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		view.change_time = false;
		view.change_time_diagram = false;
		view.repaint();
	}

	// move rectangle of timeline by dragging
	public void mouseDragged(MouseEvent e) {
		double distance_x = e.getX() - clicked_x;

		if(view.change_time){
			int x_pos = (int) (view.timeline_rectangle.getX() + distance_x);
			if (x_pos > view.timeline_x_start && x_pos < view.timeline_x_end - view.pixel_per_year) {
				view.timeline_rectangle.setRect(x_pos, view.timeline_y - 10, view.pixel_per_year, 20);
			}			
		} else if(view.change_time_diagram) {
			view.diagram_year_triangle.xpoints[0] += distance_x;
		}
		
		
		clicked_x = e.getX();
		clicked_y = e.getY();

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
