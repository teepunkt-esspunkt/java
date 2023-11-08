package projekt;

import javax.swing.JFrame;


// Hier gibts nicht viel zu kommentieren, so wie all unsere Beispiele
@SuppressWarnings("serial")
public class ReisenMain extends JFrame {

	public static void main(String[] args) {
		new ReisenMain("Reisekostenabrechnung");

	}

	public ReisenMain(String titel) {
		super(titel);
		setBounds(100, 100, 800, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(new ReisenPanel());
		setVisible(true);

	}

}
