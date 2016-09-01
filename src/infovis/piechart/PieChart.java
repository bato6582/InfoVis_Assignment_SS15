package infovis.piechart;

import java.io.IOException;

import infovis.gui.GUI;
import infovis.scatterplot.Model;

import javax.swing.SwingUtilities;

public class PieChart {
	private MouseController mouse_controller = null;
	private KeyboardController key_controller = null;
    private Model model = null;
    private static View view = null;
       
	public View getView() {
		if (view == null) generateDiagram();
		return view;
	}
	public void generateDiagram(){
	   model = new Model();
	   view = new View();
	   mouse_controller = new MouseController();
	   key_controller = new KeyboardController();
	   view.addMouseListener(mouse_controller);
	   view.addMouseMotionListener(mouse_controller);
	   view.addKeyListener(key_controller);
	   view.setFocusable(true);
	   
	   
	   mouse_controller.setModel(model);
	   mouse_controller.setView(view);
	   mouse_controller.setKeyController(key_controller);

	   key_controller.setView(view);
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
