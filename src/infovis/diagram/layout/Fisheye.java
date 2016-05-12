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
		
		System.out.println("HAAAALLLLLOOOO");
		for (Element element: model.getElements()){
			element.setX(fish(element.getX(), element.getX() + 0.5 * element.getWidth(), view.getScale(), view.getWidth()));
			element.setY(fish(element.getY(), element.getY() + 0.5 * element.getHeight(), view.getScale(), view.getHeight()));
			//			element.setHeight(3);
		}
		return null;
	}
	
	
	static double fish(double pNorm, double pBoundary, double d, int size){
		double pFocus = size * 0.5;
		double dMax = 0;
		

		if (pNorm > pFocus) {
			System.out.println("Fall 1");
			dMax = /*pBoundary*/ + pFocus;
		} else if ( pNorm < pFocus) {
			System.out.println("Fall 2");
			dMax = - pFocus;
		}  else {
			return pNorm;
		}
		
		double dNorm = pNorm - pFocus;
		double value = dNorm / dMax;
		double g = ((d + 1) * value) / (d * value + 1);
		double pFish = pFocus + g * dMax;
		
		return pFish;
	}
	
	
	
}
