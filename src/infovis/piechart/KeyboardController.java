package infovis.piechart;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class KeyboardController extends JFrame implements KeyListener{
	private boolean ctrl_pressed = false;
	private View view = null;

	public void setView(View view) {
		this.view = view;
	}
	
	public boolean getCtrl_pressed() {
		return ctrl_pressed;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// 17 == ctrl
		if (e.getKeyCode() == 17) {
			view.ctrl_pressed = true;
			view.selection_chosen = false;
			view.repaint();
			
		} else if (e.getKeyCode() == 16) {
			//16 == shift
			view.shift_pressed = true;
			view.repaint();
		} else if (e.getKeyCode() == 32) {
			//32 == space
			if (view.percent) {
				view.percent = false;
			} else {
				view.percent = true;				
			}
			view.repaint();
		}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// 17 == ctrl
		if (e.getKeyCode() == 17) {
			view.ctrl_pressed = false;
			view.selection_chosen = true;
			view.repaint();
//			System.out.println("released KeyCode: " + e.getID());
		} else if (e.getKeyCode() == 16) {
			//16 == shift
			view.shift_pressed = false;
			view.repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
//		System.out.println("typed KeyCode: " + e.getKeyCode());
		// TODO Auto-generated method stub
		
	}
}
