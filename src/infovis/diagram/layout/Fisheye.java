package infovis.diagram.layout;

import infovis.debug.Debug;
import infovis.diagram.Model;
import infovis.diagram.View;
import infovis.diagram.elements.Edge;
import infovis.diagram.elements.Element;
import infovis.diagram.elements.Vertex;

import java.util.Iterator;

/*
 * 
 */

public class Fisheye implements Layout{

	public void setMouseCoords(int x, int y, View view) {
		// TODO Auto-generated method stub
	}

	public Model transform(Model model, View view) {
		
		
		
		
//		for (Element element: model.getElements()){
//			element.setHeight(3);
//		}
		return null;
	}
	
	
	static void fish(int x, int y, double d){
		
		double pNormX = 1;
		double pFocusX = 1;
		double pBoundaryX = 1;
		double dMaxX = 1;
		
		if (pNormX > pFocusX) {
			dMaxX = pBoundaryX - pFocusX;
		} else if ( pNormX < pFocusX) {
			dMaxX = - pFocusX;
		}
		double dNormX = pNormX - pFocusX;
		double valueX = dNormX / dMaxX;
		double gX = ((d + 1) * valueX) / (d * valueX + 1);
		double pFishX = pFocusX + gX * dMaxX;
	}
	
}
