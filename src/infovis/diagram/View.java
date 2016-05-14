package infovis.diagram;

import infovis.diagram.elements.Element;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JPanel;



public class View extends JPanel{
	private Model model = null;
	private Color color = Color.BLUE;
	private double scale = 1;
	private double translateX= 0;
	private double translateY=0;

    private double old_translateX=0;
    private double old_translateY=0;

	private Rectangle2D marker = new Rectangle2D.Double();
    private Rectangle2D overviewRect = new Rectangle2D.Double();
    private Rectangle2D windowRect = new Rectangle2D.Double();
    
    public double border = 40;
    public double windowOffsetX;
    public double windowOffsetY;
    public double oldWindowOffsetX;
    public double oldWindowOffsetY;


	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	
	public void paint(Graphics g) {
		
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.clearRect(0, 0, getWidth(), getHeight());
		
		g2D.drawLine((int) (getWidth() * 0.5), 0, (int) (getWidth() * 0.5), (int)getHeight());
		g2D.drawLine(0,(int)( 0.5 * getHeight() ), getWidth(), (int) ( 0.5 * getHeight()));
		// data
		g2D.scale(scale,scale);
		g2D.translate(-old_translateX - translateX, -old_translateY -translateY);
		paintDiagram(g2D);	
		System.out.println(translateX + " , " + translateY);
		
		
		// window
		g2D.scale(0.25/scale, 0.25/scale);
		g2D.translate((old_translateX + translateX)*scale*4, 4*scale* (old_translateY + translateY));
		windowRect.setRect((windowOffsetX + oldWindowOffsetX) * 4, (windowOffsetY + oldWindowOffsetY) * 4, getWidth() + border * 2, getHeight() + border * 2);
		g2D.setColor(Color.BLACK);
		g2D.fill(windowRect);
		g2D.draw(windowRect);
		
		//overview
		overviewRect.setRect((windowOffsetX + oldWindowOffsetX) * 4 + border, (windowOffsetY + oldWindowOffsetY) * 4 + border, getWidth(), getHeight());
		g2D.setColor(Color.WHITE);
		g2D.fill(overviewRect);
		g2D.setColor(Color.BLACK);
		g2D.draw(overviewRect);
		
		//overview data
		g2D.translate((windowOffsetX + oldWindowOffsetX) * 4 + border, (windowOffsetY + oldWindowOffsetY) * 4 + border);
		paintDiagram(g2D);
		
		//marker
		//g2D.translate(translateX, translateY);
		g2D.scale(1 / scale, 1 / scale);
		g2D.translate(translateX * scale, translateY * scale);
        //marker.setRect(marker.getX(), marker.getY(), getWidth(), getHeight());
        //marker.setRect(marker.getMinX(), marker.getMinY(), getWidth(), getHeight());
        marker.setRect(old_translateX * scale, old_translateY * scale, getWidth(), getHeight());
//        System.out.println("min: " + marker.getX());
        g2D.setColor(Color.RED);
		g2D.draw(marker);

//		System.out.println(translateX);		


		
		
		
		
	}
	private void paintDiagram(Graphics2D g2D){
		for (Element element: model.getElements()){
			element.paint(g2D);
		}
	}
	
	public void setScale(double scale) {
		this.scale = scale;
	}
	public double getScale(){
		return scale;
	}
	public double getTranslateX() {
		return translateX;
	}
	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}
	public double getTranslateY() {
		return translateY;
	}
	public void setTranslateY(double translateY) {
		this.translateY = translateY;
	}
	public void updateTranslation(double x, double y){
		setTranslateX(x);
		setTranslateY(y);
	}	
	public void updateMarker(int x, int y){
		marker.setRect(x, y, 16, 10);
	}
	public Rectangle2D getMarker(){
		return marker;
	}
	public boolean markerContains(double x, double y){
		return marker.contains(x, y);
	}
	
	public boolean windowContains(double x, double y){
		return windowRect.contains(x, y);
	}
	
	public boolean overviewContains(double x, double y){
		return overviewRect.contains(x, y);
	}

    public void setOldTranslateX(double translateX) {
        this.old_translateX = translateX;
    }

    public void setOldTranslateY(double translateX) {
        this.old_translateY = translateX;
    }


    public Rectangle2D getOverviewRect(){
        return overviewRect;
    }

    public double getOldTranslateX(){
    	return old_translateX;
    }
    
    public double getOldTranslateY(){
    	return old_translateY;
    }
    



}
 