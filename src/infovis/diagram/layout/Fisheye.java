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
	
	private double [] oldTranslateX;
	private double [] oldTranslateY;
	private double [] oldScaleX;
	private double [] oldScaleY;

	public void setMouseCoords(int x, int y, View view) {
		// TODO Auto-generated method stub
	}

	public Model transform(Model model, View view, int x, int y) {
		
//		System.out.println("HAAAALLLLLOOOO");
		for (Vertex vertex: model.getVertices()){
//			int tmp_x = (int) (x - vertex.getWidth() * 0.5 * view.getScale());
//			int tmp_y = (int) (y - vertex.getHeight() * 0.5 * view.getScale());
			
			int tmp_x = x;
			int tmp_y = y;
			
			vertex.setX(fishTranslate(vertex.getCenterX() , tmp_x / view.getScale(), view.getWidth(), view.getScale()) /*- vertex.getWidth() * 0.5 * view.getScale()*/);
			vertex.setY(fishTranslate(vertex.getCenterY() , tmp_y / view.getScale(), view.getHeight(), view.getScale()) /*- vertex.getHeight() * 0.5 * view.getScale()*/);
			vertex.setWidth(fishScale(vertex.getX() , vertex.getCenterX(), tmp_x/ view.getScale() , view.getWidth(), view.getScale()));
			vertex.setHeight(fishScale(vertex.getY(), vertex.getCenterY(), tmp_y/ view.getScale() , view.getHeight(), view.getScale()));
		}
		return model;
	}
	
	
	static double fishTranslate(double pNorm, double pFocus, double screenBoundary, double d){

		double dMax = 0;
		

		if (pNorm > pFocus) {
//			System.out.println("Fall 1");
			dMax = screenBoundary - pFocus;
		} else if ( pNorm < pFocus) {
//			System.out.println("Fall 2");
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
	
	
	static double fishScale(double qNorm, double pNorm, double pFocus, double screenBoundary, double d){
		double qFish = fishTranslate(qNorm, pFocus, screenBoundary, d); //TODO augment pNorm
		
		double sGeom = 2 * Math.min(Math.abs(qFish - fishTranslate(pNorm, pFocus, screenBoundary, d)), Math.abs(fishTranslate(pNorm, pFocus, screenBoundary, d) - qFish)); //unaugmented pNorm
//		System.out.println(sGeom);
		return sGeom;
	}

	@Override
	public Model transform(Model model, View view) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
