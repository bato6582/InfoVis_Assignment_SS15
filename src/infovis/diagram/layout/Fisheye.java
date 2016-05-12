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
			element.setX(fish(element.getX(), view.getWidth(), view.getScale()));
			element.setY(fish(element.getY(), view.getHeight(), view.getScale()));
		}
		return null;
	}
	
	static double fish(double pNorm, double pBoundary, double d){
		//combines scaling and translation
		return fishTranslate(pNorm, pBoundary, d);
	}
	static double fishTranslate(double pNorm, double screenBoundary, double d){
		double pFocus = screenBoundary * 0.5;
		double dMax = 0;
		

		if (pNorm > pFocus) {
			System.out.println("Fall 1");
			dMax = screenBoundary - pFocus;
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
