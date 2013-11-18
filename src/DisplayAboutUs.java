

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DisplayAboutUs {

	private JScrollPane scrollpane;
	private JFrame frame;
	private JPanel panel;
	private int WIDTH = 300;
	private int HEIGHT = 600;

	public DisplayAboutUs() {
		
		try {  
			frame = new JFrame();
			panel = new JPanel();
			
			JTextArea ta = new JTextArea();
			ta.setBackground(Color.DARK_GRAY);
			ta.setForeground(Color.WHITE);
			ta.setPreferredSize(new Dimension(WIDTH-10,HEIGHT-10));
			ta.read(new FileReader("./docs/AboutUs.txt"), null);
			ta.setEditable(false);  
			
			scrollpane = new JScrollPane(ta);
			scrollpane.setBackground(Color.DARK_GRAY);
			scrollpane.setForeground(Color.WHITE);
			
			panel.add(scrollpane);
			
			frame.add(panel);
			frame.setPreferredSize(new Dimension(WIDTH,HEIGHT));
			frame.pack();
			frame.setVisible(true);
			}  
			catch (IOException ioe) {  
			ioe.printStackTrace();  
			}  
			}
}



