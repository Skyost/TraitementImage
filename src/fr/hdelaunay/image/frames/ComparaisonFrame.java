package fr.hdelaunay.image.frames;

import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;

import javax.swing.JFrame;

import fr.hdelaunay.image.Main;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class ComparaisonFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public ComparaisonFrame(final Component parent) {
		this.setTitle("Comparaison de visages");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource(Main.RES_PACKAGE + "icon_app.png")));
		this.setSize(600, 300);
		this.setLocationRelativeTo(parent);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final Container content = this.getContentPane();
		final GroupLayout groupLayout = new GroupLayout(content);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGap(0, 584, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGap(0, 261, Short.MAX_VALUE)
		);
		content.setLayout(groupLayout);
	}
}
