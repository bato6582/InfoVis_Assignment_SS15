package infovis.scatterplot;

import infovis.debug.Debug;
import infovis.diagram.elements.Element;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
	        
//			g2D.setFont(new Font("TimesRoman", Font.PLAIN, 18)); 
			
			g2D.setColor(Color.WHITE);
			g2D.fill(cell);
			g2D.setColor(Color.RED);
			g2D.fill(dataPoint);
			g2D.setColor(Color.BLACK);
			
			
			int border_left = 125;
			int border_up = 60;
			int numProperties = model.getLabels().size();
			double cellHeight = (getHeight() - border_up)/ numProperties;
			double innerCellHeight = cellHeight - 10;
			double cellWidth = (getWidth() - border_left)/ numProperties;
			double innerCellWidth = cellWidth - 10;

			
			// content
			for (int currentRow = 0; currentRow < numProperties; ++currentRow) {
				for (int currentCell = 0; currentCell < numProperties; ++ currentCell) {
					double xpos = currentCell * cellWidth + border_left;
					double ypos = currentRow * cellHeight + border_up;
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
						if (markerRectangle.contains(new Rectangle2D.Double(xpos + xValue + 5, ypos + yValue + 5, 3, 3))) {
							d.chosen = true;
							g2D.setColor(Color.BLUE);
						} else {
							g2D.setColor(Color.RED);
						}
						
						if (d.chosen) {
							g2D.setColor(Color.BLUE);
						} else {
							g2D.setColor(Color.RED);
						}
						
						g2D.fill(dataPoint);
						dataPoint.setRect(xpos + xValue + 5, ypos + yValue + 5, 3, 3);
						g2D.setColor(Color.BLACK);
						g2D.draw(dataPoint);
					

					}
				}
			}

			// labels
			ArrayList<String> labels = model.getLabels();
			ArrayList<Range> ranges = model.getRanges();
			for (int currentCell = 0; currentCell < numProperties; ++ currentCell) {
				String label = labels.get(currentCell);
				double xpos = currentCell * cellWidth + border_left;
				g2D.drawString(label, (float) (xpos + cellWidth * 0.5 - label.length() * 2.5), border_up * 0.5f - 5);
				
				String lowerLimit = "" + ranges.get(currentCell).getMin();
				String upperLimit = "" + ranges.get(currentCell).getMax();
				g2D.drawString(lowerLimit, (float) (xpos + 7.5f), border_up - 5);
				g2D.drawString(upperLimit, (float) (xpos + cellWidth - upperLimit.length() * 7.5f), border_up - 5);
			}
			for (int currentRow = 0; currentRow < numProperties; ++currentRow) {
				String label = labels.get(currentRow);
				double ypos = currentRow * cellHeight + border_up;
				g2D.drawString(label, (float) 1, (float) (ypos + cellHeight * 0.5 + 5));
				
				String lowerLimit = "" + ranges.get(currentRow).getMin();
				String upperLimit = "" + ranges.get(currentRow).getMax();
				g2D.drawString(lowerLimit, (float) (border_left - lowerLimit.length() * 7.5f), (float) ypos + 10.5f);
				g2D.drawString(upperLimit, (float) (border_left - upperLimit.length() * 7.5f),  (float) (ypos + cellHeight - 7.5));
			}
			
			// marker rectangle
			g2D.setColor(Color.GREEN);
			g2D.draw(markerRectangle);
	        
			
		}
		public void setModel(Model model) {
			this.model = model;
		}
		
		public void clearData() {
			for (Data d : model.getList()) {
				d.chosen = false;
			}
		}
		
}
