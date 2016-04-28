package infovis.scatterplot;

import infovis.debug.Debug;
import infovis.diagram.elements.Element;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class View extends JPanel {
	     private Model model = null;
	     private Rectangle2D cell = new Rectangle2D.Double(0,0,0,0);
	     private Rectangle2D dataPoint = new Rectangle2D.Double(0,0,0,0); 
	     private Rectangle2D markerRectangle = new Rectangle2D.Double(0,0,0,0); 

		 public Rectangle2D getMarkerRectangle() {
			return markerRectangle;
		}
		 
		@Override
		public void paint(Graphics g) {
			
			Graphics2D g2D = (Graphics2D) g;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.clearRect(0, 0, getWidth(), getHeight());
	        
			
			g2D.setColor(Color.WHITE);
			g2D.fill(cell);
			g2D.setColor(Color.RED);
			g2D.fill(dataPoint);
			g2D.setColor(Color.BLACK);
			
			
			int border = 30;
			int numProperties = model.getLabels().size();
			double cellHeight = (getHeight() - border)/ numProperties;
			double innerCellHeight = cellHeight - 10;
			double cellWidth = (getWidth() - border)/ numProperties;
			double innerCellWidth = cellWidth - 10;
			for (int currentRow = 0; currentRow < numProperties; ++currentRow) {
				for (int currentCell = 0; currentCell < numProperties; ++ currentCell) {
					double xpos = currentCell * cellWidth + border;
					double ypos = currentRow * cellHeight + border;
					cell.setRect(xpos, ypos, cellWidth, cellHeight);
					g2D.draw(cell);
					
					//X - Axis
					double xmin = model.getRanges().get(currentCell).getMin();
					double xmax = model.getRanges().get(currentCell).getMax();
					double xratio = innerCellWidth / (xmax - xmin);
					
					//Y - Axis
					double ymin = model.getRanges().get(currentRow).getMin();
					double ymax = model.getRanges().get(currentRow).getMax();
					double yratio = innerCellHeight / (ymax - ymin);

					
					for (Data d : model.getList()) {
						double xValue = d.getValues()[currentCell] - xmin;
						double yValue = d.getValues()[currentRow] - ymin;
						xValue = xValue * xratio;
						yValue = yValue * yratio;
						//System.out.println(xValue + "				" + innerCellWidth);
						g2D.setColor(Color.RED);
						g2D.fill(dataPoint);
						dataPoint.setRect(xpos + xValue + 5, ypos + yValue + 5, 3, 3);
						g2D.setColor(Color.BLACK);
						g2D.draw(dataPoint);
					

					}

//					Debug.print(l);
//					Debug.print(",  ");
//					Debug.println("");
				}
			}
//			for (Range range : model.getRanges()) {
//				Debug.print(range.toString());
//				Debug.print(",  ");
//				Debug.println("");
//			}
//			for (Data d : model.getList()) {
//				Debug.print(d.toString());
//				Debug.println("");
//			}
//			
//				
	        
			
		}
		public void setModel(Model model) {
			this.model = model;
		}
}
