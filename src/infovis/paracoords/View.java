package infovis.paracoords;

import infovis.scatterplot.Data;
import infovis.scatterplot.Model;
import infovis.scatterplot.Range;
import sun.nio.ch.SelChImpl;

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
	private Rectangle2D cell = new Rectangle2D.Double(0, 0, 0, 0);
	private Rectangle2D dataPoint = new Rectangle2D.Double(0, 0, 0, 0);
	private Rectangle2D markerRectangle = new Rectangle2D.Double(0, 0, 0, 0);
	private boolean[] chosen;
	private Map<String, Rectangle2D.Double[]> axes = new HashMap<String, Rectangle2D.Double[]>();
	public Map<String, Rectangle2D.Double> labelSelectionRectMap = new HashMap<String, Rectangle2D.Double>();
	public Rectangle2D.Double selectedRectangle;

	private double width;
	private double height;

	private ArrayList<String> properties;
	private ArrayList<Range> ranges;
	private ArrayList<Data> list;

	public double xOffset = 0.0;
	public double oldXOffset = 0.0;
	public String draggedLabel = "";
	public boolean dragLabel = false;

	public void initialize() {
		if (properties == null) {
			properties = model.getLabels();
		}

		if (ranges == null) {
			ranges = model.getRanges();
		}

		if (list == null) {
			list = model.getList();
		}

		int upperBorder = 30;
		int leftBorder = 60;
		int numProperties = properties.size();

		int distance = (getWidth() - 2 * leftBorder) / (numProperties - 1);

		int xpos = leftBorder;
		int ypos = upperBorder;
		int iter = 0;
		for (String label : properties) {
			// labels
			Rectangle2D.Double selectionRectangle = new Rectangle2D.Double(xpos
					- label.length() * 2.5f, getHeight() - upperBorder + 5,
					2 * label.length() * 2.5f, 10);
			labelSelectionRectMap.put(label, selectionRectangle);

			double min = ranges.get(iter).getMin();
			double max = ranges.get(iter).getMax();

			// data
			Rectangle2D.Double[] points = new Rectangle2D.Double[list.size()];
			double yratio = (getHeight() - upperBorder * 2 - ypos)
					/ (max - min);

			int dataIter = 0;
			for (Data d : list) {
				double value = d.getValues()[iter];
				value = getHeight() - upperBorder * 2 - yratio * (value - min);

				points[dataIter] = new Rectangle2D.Double(xpos - 1, value, 3, 3);
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
			// data points
			for (Rectangle2D.Double rect : axes.get(draggedLabel)) {
				rect.setRect(rect.getX() + xOffset - oldXOffset, rect.getY(),
						rect.getWidth(), rect.getHeight());
			}
			// yellow rectangle
			labelSelectionRectMap.get(draggedLabel).setRect(
					selectedRectangle.getX() + xOffset - oldXOffset,
					selectedRectangle.getY(), selectedRectangle.getWidth(),
					selectedRectangle.getHeight());

			// sectionRectangles.remove()

		}

		if (width != getWidth() || height != getHeight()) {
			initialize();
		}

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.clearRect(0, 0, getWidth(), getHeight());

		g2D.setColor(Color.RED);
		g2D.fill(dataPoint);
		g2D.setColor(Color.BLACK);

		int upperBorder = 30;
		int leftBorder = 60;
		int numProperties = properties.size();

		int distance = (getWidth() - 2 * leftBorder) / (numProperties - 1);

		int xpos = leftBorder;
		int ypos = upperBorder;
		int iter = 0;
		String lastLabel = "";

		for (String label : properties) {
			Rectangle2D.Double selectionRect = labelSelectionRectMap.get(label);
			g2D.setColor(Color.YELLOW);
			g2D.fill(selectionRect);
			g2D.setColor(Color.BLACK);
			g2D.draw(selectionRect);

			// labels
			g2D.setColor(Color.BLACK);
			g2D.drawLine(xpos, ypos, xpos, getHeight() - upperBorder * 2);
			g2D.drawString(label, xpos - label.length() * 2.5f, getHeight()
					- upperBorder);

			// ranges
			double min = ranges.get(iter).getMin();
			double max = ranges.get(iter).getMax();
			String lowerLimit = "" + min;
			String upperLimit = "" + max;
			g2D.drawString(lowerLimit, xpos - lowerLimit.length() * 2.5f,
					getHeight() - upperBorder * 2 + 14);
			g2D.drawString(upperLimit, xpos - upperLimit.length() * 2.5f,
					ypos - 6);

			// data
			for (int dataIter = 0; dataIter < properties.size(); dataIter++) {
				g2D.fill(dataPoint);
				dataPoint.setRect(axes.get(label)[dataIter]);
				g2D.draw(dataPoint);
				g2D.setColor(Color.BLUE);

				// marked data
				if (markerRectangle.contains(axes.get(label)[dataIter])) {
					g2D.setColor(Color.RED);
				} else {
					// check if object has marked attributes
					for (int idx = 0; idx < axes.keySet().size(); idx++) {
						Rectangle2D.Double rect = axes.get(properties.get(idx))[dataIter];
						if (markerRectangle.contains(rect)) {
							g2D.setColor(Color.RED);
							break;
						}
					}
				}

				// connection lines
				if (iter > 0) {
					g2D.drawLine((int) axes.get(lastLabel)[dataIter].getX(),
							(int) axes.get(lastLabel)[dataIter].getY(),
							(int) axes.get(label)[dataIter].getX(),
							(int) axes.get(label)[dataIter].getY());
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

	public void checkAxes() {
		int leftBorder = 60;
		int numProperties = properties.size();

		int distance = (getWidth() - 2 * leftBorder) / (numProperties - 1);

		int xpos = leftBorder;
		int idx_draggedLabel = properties.indexOf(draggedLabel);
		for (String label : properties) {
			if (!label.equals(draggedLabel)) {
				if (oldXOffset < 0) {
					if (idx_draggedLabel * distance + xpos + oldXOffset <= properties.indexOf(label) * distance + xpos) {

						for (Data d : list) {
							double last = d.getValues()[idx_draggedLabel];
							double tmp = 0.0;

							for (int i = properties.indexOf(label); i <= idx_draggedLabel; i++) {
								tmp = d.getValues()[i];
								d.getValues()[i] = last;
								last = tmp;

							}

						}

						String lastLabel = draggedLabel;
						String tmpLabel = "";

						Range lastRange = ranges.get(idx_draggedLabel);
						Range tmpRange = ranges.get(idx_draggedLabel);

						for (int i = properties.indexOf(label); i <= idx_draggedLabel; i++) {
							tmpLabel = properties.get(i);
							properties.set(i, lastLabel);
							lastLabel = tmpLabel;

							tmpRange = ranges.get(i);
							ranges.set(i, lastRange);
							lastRange = tmpRange;

						}

						break;
					}
				} else {
					if ((idx_draggedLabel * distance + xpos + oldXOffset > properties.indexOf(label) * distance + xpos) && properties.indexOf(label) > idx_draggedLabel	) {
						for (int iter = list.size() - 1; iter >= 0; iter--) {
							Data d = list.get(iter);
							double last = d.getValues()[idx_draggedLabel];
							double tmp = 0.0;

							for (int i = properties.indexOf(label); i >= idx_draggedLabel; i--) {
								tmp = d.getValues()[i];
								d.getValues()[i] = last;
								last = tmp;
							}
						}

						String lastLabel = draggedLabel;
						String tmpLabel = "";

						Range lastRange = ranges.get(idx_draggedLabel);
						Range tmpRange = ranges.get(idx_draggedLabel);

						for (int i = properties.indexOf(label); i >= idx_draggedLabel; i--) {
							tmpLabel = properties.get(i);
							properties.set(i, lastLabel);
							lastLabel = tmpLabel;

							tmpRange = ranges.get(i);
							ranges.set(i, lastRange);
							lastRange = tmpRange;

						}

						break;
					}
				}

			}
		}
		System.out.println(properties);
		initialize();
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

	public Rectangle2D getMarkerRectangle() {
		return markerRectangle;
	}



}
