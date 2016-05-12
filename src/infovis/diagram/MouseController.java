package infovis.diagram;

import infovis.debug.Debug;
import infovis.diagram.elements.DrawingEdge;
import infovis.diagram.elements.Edge;
import infovis.diagram.elements.Element;
import infovis.diagram.elements.GroupingRectangle;
import infovis.diagram.elements.None;
import infovis.diagram.elements.Vertex;
import infovis.diagram.layout.Fisheye;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MouseController implements MouseListener,MouseMotionListener {
	 private Model model;
	 private View view;
	 private Element selectedElement = new None(); // gescaled
	 private double mouseOffsetX; // nicht gescaled
	 private double mouseOffsetY; // nicht gescaled
	 private double xClicked = 0;
	 private double yClicked = 0;
	 private boolean edgeDrawMode = false;
	 private DrawingEdge drawingEdge = null;
	 private boolean fisheyeMode;
     private boolean markerMoving = false;
	 private GroupingRectangle groupRectangle;
	 private Fisheye fisheye = new Fisheye();
	/*
	 * Getter And Setter
	 */
	 public Element getSelectedElement(){
		 return selectedElement;
	 }
    public Model getModel() {
		return model;
	}
	public void setModel(Model diagramModel) {
		this.model = diagramModel;
	}
	public View getView() {
		return view;
	}
	public void setView(View diagramView) {
		this.view = diagramView;
	}
	/*
     * Implements MouseListener
     */


    //mouse is pressed and released without being moved
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		double scale = view.getScale();
//        System.out.println("CLICKED");




        if (e.getButton() == MouseEvent.BUTTON3){
			/*
			 * add grouped elements to the model
			 */
			Vertex groupVertex = (Vertex)getElementContainingPosition(x/scale,y/scale);
			for (Iterator<Vertex> iter = groupVertex.getGroupedElements().iteratorVertices();iter.hasNext();){
				model.addVertex(iter.next());
			}
			for (Iterator<Edge> iter = groupVertex.getGroupedElements().iteratorEdges();iter.hasNext();){
				model.addEdge(iter.next());
			}
			/*
			 * remove elements
			 */
			List<Edge> edgesToRemove = new ArrayList<Edge>();
			for (Iterator<Edge> iter = model.iteratorEdges(); iter.hasNext();){
				Edge edge = iter.next();
				if (edge.getSource() == groupVertex || edge.getTarget() == groupVertex){
					edgesToRemove.add(edge);
				}
			}
			model.removeEdges(edgesToRemove);
			model.removeElement(groupVertex);
			
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}
    //mouse is released
    public void mouseReleased(MouseEvent e){
        int x = e.getX();
        int y = e.getY();

        markerMoving = false;

        if (drawingEdge != null){
            Element to = getElementContainingPosition(x, y);
            model.addEdge(new Edge(drawingEdge.getFrom(),(Vertex)to));
            model.removeElement(drawingEdge);
            drawingEdge = null;
        }
        if (groupRectangle != null){
            Model groupedElements = new Model();
            for (Iterator<Vertex> iter = model.iteratorVertices(); iter.hasNext();) {
                Vertex vertex = iter.next();
                if (groupRectangle.contains(vertex.getShape().getBounds2D())){
                    Debug.p("Vertex found");
                    groupedElements.addVertex(vertex);
                }
            }
            if (!groupedElements.isEmpty()){
                model.removeVertices(groupedElements.getVertices());

                Vertex groupVertex = new Vertex(groupRectangle.getCenterX(),groupRectangle.getCenterX());
                groupVertex.setColor(Color.ORANGE);
                groupVertex.setGroupedElements(groupedElements);
                model.addVertex(groupVertex);

                List<Edge> newEdges = new ArrayList();
                for (Iterator<Edge> iter = model.iteratorEdges(); iter.hasNext();) {
                    Edge edge =  iter.next();
                    if (groupRectangle.contains(edge.getSource().getShape().getBounds2D())
                            && groupRectangle.contains(edge.getTarget().getShape().getBounds2D())){
                        groupVertex.getGroupedElements().addEdge(edge);
                        Debug.p("add Edge to groupedElements");
                        //iter.remove(); // Warum geht das nicht!
                    } else if (groupRectangle.contains(edge.getSource().getShape().getBounds2D())){
                        groupVertex.getGroupedElements().addEdge(edge);
                        newEdges.add(new Edge(groupVertex,edge.getTarget()));
                    } else if (groupRectangle.contains(edge.getTarget().getShape().getBounds2D())){
                        groupVertex.getGroupedElements().addEdge(edge);
                        newEdges.add(new Edge(edge.getSource(),groupVertex));
                    }
                }
                model.addEdges(newEdges);
                model.removeEdges(groupedElements.getEdges());
            }
            model.removeElement(groupRectangle);
            groupRectangle = null;
        }

        view.repaint();

        
        // last position of translation, in g2d
        view.setOldTranslateX(view.getOldTranslateX() + view.getTranslateX());
        view.setOldTranslateY(view.getOldTranslateY() + view.getTranslateY());

        
        view.setTranslateX(0.0);
        view.setTranslateY(0.0);


        //view.getMarker().setRect(view.getTranslateX() * view.getScale(), view.getTranslateY() * view.getScale(), view.getWidth(), view.getHeight());

    }

	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		xClicked= e.getX();
		yClicked = e.getY();
		double scale = view.getScale();

        //checks if clicked in marker, can not be called
        //in dragged() because mouse may move too fast
        if(view.markerContains(x * 4 * scale , y * 4 * scale) ) {
            markerMoving = true;
        }


	   if (edgeDrawMode){
			drawingEdge = new DrawingEdge((Vertex)getElementContainingPosition(x/scale,y/scale));
			model.addElement(drawingEdge);
		} else if (fisheyeMode){
			/*
			 * do handle interactions in fisheye mode
			 */
			
			model = fisheye.transform(model, view);
			view.repaint();
		} else {

			selectedElement = getElementContainingPosition(x/scale,y/scale); // klein gescaled
//			System.out.println("x: " + x/scale + " y: " + y/scale);
//			System.out.println("element x: " + selectedElement.getX() + " element y: " + selectedElement.getX());
			/*
			 * calculate offset
			 */
//			mouseOffsetX = x - selectedElement.getX() * scale;
//			mouseOffsetY = y - selectedElement.getY() * scale;

//			System.out.println("selected.elementd x : " + selectedElement.getX());



		}

	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		double scale = view.getScale();

		
		/*
		* Aufgabe 1.2
		*/
		// calculate offset

 
		double offsetx = x - xClicked; // nicht gescaled
		double offsety = y - yClicked; // nicht gescaled

        if(markerMoving && view.getOverviewRect().contains(x*4, y*4) ){
			// * 4 because overview window is set to 0.25 * detailwindow
			// TO DO: create variable in view containing the ration of detail/overview window and use it insted of 4
			view.setTranslateX(4 * offsetx);// nicht gescaled
			view.setTranslateY(4 * offsety);//
            //view.getMarker().setRect(view.getTranslateX() * view.getScale(), view.getTranslateY() * view.getScale(), view.getWidth(), view.getHeight());
//            view.getMarker().setRect(view.getTranslateX() * view.getScale(), view.getTranslateY() * view.getScale(), view.getWidth(), view.getHeight());

        }
//        System.out.println("dragging: " + view.getMarker().getX());

//		mouseOffsetX = x;
//		mouseOffsetY = y;

		if (fisheyeMode){
			/*
			 * handle fisheye mode interactions
			 */
			view.repaint();
		} else if (edgeDrawMode){
			drawingEdge.setX(e.getX());
			drawingEdge.setY(e.getY());
		}else if(selectedElement != null){
			selectedElement.updatePosition((e.getX()-mouseOffsetX)/scale, (e.getY()-mouseOffsetY) /scale);
		}
		
		view.repaint();
		
	}
	public void mouseMoved(MouseEvent e) {
	}
	public boolean isDrawingEdges() {
		return edgeDrawMode;
	}
	public void setDrawingEdges(boolean drawingEdges) {
		this.edgeDrawMode = drawingEdges;
	}
	
	public void setFisheyeMode(boolean b) {
		fisheyeMode = b;
		if (b){
			Debug.p("new Fisheye Layout");
			/*
			 * handle fish eye initial call
			 */
            view.repaint();
        } else {
			Debug.p("new Normal Layout");
			view.setModel(model);
			view.repaint();



		}
	}
	
	/*
	 * private Methods
	 */
	private Element getElementContainingPosition(double x,double y){
		Element currentElement = new None();
		Iterator<Element> iter = getModel().iterator();
		while (iter.hasNext()) {
		  Element element =  iter.next();
		  if (element.contains(x, y)) currentElement = element;  
		}
		return currentElement;
	}
	

}
