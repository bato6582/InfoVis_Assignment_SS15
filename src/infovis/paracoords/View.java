package infovis.paracoords;

import infovis.scatterplot.Data;
import infovis.scatterplot.Model;
import infovis.scatterplot.Range;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

public class View extends JPanel {
	private Model model = null;
    private Rectangle2D cell = new Rectangle2D.Double(0,0,0,0);
    private Rectangle2D dataPoint = new Rectangle2D.Double(0,0,0,0); 
    private Rectangle2D markerRectangle = new Rectangle2D.Double(0,0,0,0); 
    private boolean [] chosen;
    private Map <String, Rectangle2D.Double []> axes = new HashMap<String, Rectangle2D.Double []>();
    private Map <Rectangle2D.Double, String> selectionRectangles = new HashMap<Rectangle2D.Double, String>();
    
    private double width;
    private double height;
    
    public double xOffset = 0.0;
    public String draggedLabel = "";
    public boolean dragLabel = false;
    
    public void initialize(){
    	int upperBorder = 30;
		int leftBorder = 60;
		int numProperties = model.getLabels().size();
		
		int distance = (getWidth() - 2 * leftBorder) / (numProperties - 1);
		System.out.println("distance=  (" + getWidth() + " - 120) / (" + numProperties + " -1)");
		
		
		int xpos = leftBorder;
		int ypos = upperBorder;
		int iter = 0;
		ArrayList<Range> ranges = model.getRanges();
		for (String label : model.getLabels()) {
			//labels
			Rectangle2D.Double selectionRectangle = new Rectangle2D.Double(xpos - label.length() * 2.5f, getHeight() - upperBorder + 5, 2 * label.length() * 2.5f, 10);
			selectionRectangles.put(selectionRectangle, label);
			Rectangle2D.Double [] points = new Rectangle2D.Double [model.getList().size()];
			
			double min = ranges.get(iter).getMin();
			double max = ranges.get(iter).getMax();
			
			//data
			double yratio = (getHeight() - upperBorder * 2 - ypos) / (max - min);
			
			int dataIter = 0;
			for (Data d : model.getList()) {
				double value = d.getValues()[iter];
				value = getHeight() - upperBorder * 2 - yratio * (value - min) + 5;
			
				points[dataIter] = new Rectangle2D.Double(xpos-1, value, 3, 3);
				dataIter++;
			}
			
			axes.put(label, points);
			xpos += distance;
			iter++;
			
		}
		width = getWidth();
		height = getHeight();
    }
    
    
    

	@Override
	public void paint(Graphics g) {
		
		if (dragLabel) {
			for (Rectangle2D.Double rect : axes.get(draggedLabel)) {
				rect.setRect(rect.getX() + xOffset, rect.getY(), rect.getWidth(), rect.getHeight());
			}
		}
		
		if (width != getWidth() || height != getHeight()) {
			initialize();
		}
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.clearRect(0, 0, getWidth(), getHeight());
        
		
		g2D.setColor(Color.WHITE);
		g2D.fill(cell);
		g2D.setColor(Color.RED);
		g2D.fill(dataPoint);
		g2D.setColor(Color.BLACK);
		
		
		int upperBorder = 30;
		int leftBorder = 60;
		int numProperties = model.getLabels().size();
		
		int distance = (getWidth() - 2 * leftBorder) / (numProperties - 1);
		
		int xpos = leftBorder;
		int ypos = upperBorder;
		int iter = 0;
		ArrayList<Range> ranges = model.getRanges();
		String lastLabel = "";
		for (String label : model.getLabels()) {
			//labels
			g2D.setColor(Color.BLACK);
			g2D.drawLine( xpos, ypos, xpos, getHeight() - upperBorder * 2);
			g2D.drawString(label, xpos - label.length() * 2.5f, getHeight() - upperBorder);
			Rectangle2D.Double selectionRectangle = new Rectangle2D.Double(xpos - label.length() * 2.5f, getHeight() - upperBorder + 5, 2 * label.length() * 2.5f, 10);
			g2D.setColor(Color.YELLOW);
			g2D.fill(selectionRectangle);
			g2D.setColor(Color.BLACK);
			g2D.draw(selectionRectangle);
			selectionRectangles.put(selectionRectangle, label);
			
			// ranges
			double min = ranges.get(iter).getMin();
			double max = ranges.get(iter).getMax();
			String lowerLimit = "" + min;
			String upperLimit = "" + max;
			g2D.drawString(lowerLimit, xpos - lowerLimit.length() * 2.5f, getHeight() - upperBorder * 2 + 14);
			g2D.drawString(upperLimit, xpos - upperLimit.length() * 2.5f, ypos - 6);
			

			
			//data
			for (int dataIter = 0; dataIter < model.getList().size(); dataIter++) {
				
				g2D.fill(dataPoint);
				dataPoint.setRect(axes.get(label)[dataIter]);
				g2D.draw(dataPoint);
				g2D.setColor(Color.BLUE);
				
				if (chosen != null) {
					if (markerRectangle.contains(axes.get(label)[dataIter])) {
						g2D.setColor(Color.RED);
					} else {
						for(String axis : axes.keySet()){
							Rectangle2D.Double rect = axes.get(axis)[dataIter];
							if (markerRectangle.contains(rect)) {
								g2D.setColor(Color.RED);
								break;
							}
						}
					}
				}
				
				if (iter > 0) {
					g2D.drawLine((int) axes.get(lastLabel)[dataIter].getX(), (int) axes.get(lastLabel)[dataIter].getY(), 
							(int) axes.get(label)[dataIter].getX(), (int) axes.get(label)[dataIter].getY());
					g2D.setColor(Color.BLACK);
				}
				

			}
			
			xpos += distance;
			iter++;
			lastLabel = label;
		}
		
		// marker rectangle
		g2D.setColor(Color.GREEN);
		g2D.draw(markerRectangle);
		
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
	public void clearData() {
		chosen = new boolean [model.getList().size()];
	}
	
	 public Rectangle2D getMarkerRectangle() {
		return markerRectangle;
	}
	 
	 public Map<Rectangle2D.Double, String> getSelectionRectangle() {
		 return selectionRectangles;
	 }
	 
	 
	 
	 
	
}
