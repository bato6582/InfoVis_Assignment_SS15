package infovis.piechart;

import java.io.IOException;

import infovis.gui.GUI;
import infovis.scatterplot.Model;

import javax.swing.SwingUtilities;

public class PieChart {
	private MouseController controller = null;
    private Model model = null;
    private static View view = null;
       
	public View getView() {
		if (view == null) generateDiagram();
		return view;
	}
	public void generateDiagram(){
	   model = new Model();
	   view = new View();
	   controller = new MouseController();
	   view.addMouseListener(controller);
	   view.addMouseMotionListener(controller);
	   
	   controller.setModel(model);
	   controller.setView(view);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI application = new GUI();
				application.setView(new PieChart().getView());
				application.getJFrame().setVisible(true);
				try {
					view.initialize();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	
}
