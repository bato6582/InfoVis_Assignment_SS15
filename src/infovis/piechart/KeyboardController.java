package infovis.piechart;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class KeyboardController extends JFrame implements KeyListener{
	private boolean ctrl_pressed = false;
	
	
	
	public boolean getCtrl_pressed() {
		return ctrl_pressed;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// 17 == ctrl
		if (e.getKeyCode() == 17) {
			ctrl_pressed = true;
//			System.out.println("pressed KeyCode: " + e.getKeyCode());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// 17 == ctrl
		if (e.getKeyCode() == 17) {
			ctrl_pressed = false;
//			System.out.println("released KeyCode: " + e.getID());
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
//		System.out.println("typed KeyCode: " + e.getKeyCode());
		// TODO Auto-generated method stub
		
	}
}
