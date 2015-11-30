package fr.hdelaunay.image.dialogs;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import fr.hdelaunay.image.Main;

/**
 * Dialogue d'attente.
 * 
 * @author Hugo Delaunay.
 */

public class WaitingDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	public WaitingDialog(final Component component) {
		this.setTitle("Chargement...");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource(Main.RES_PACKAGE + "icon_app.png")));
		this.setSize(400, 100);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(component);
		final JLabel lblChargement = new JLabel("Chargement...");
		lblChargement.setHorizontalAlignment(SwingConstants.CENTER);
		lblChargement.setFont(lblChargement.getFont().deriveFont(Font.ITALIC));
		getContentPane().add(lblChargement, BorderLayout.CENTER);
	}
	
	/**
	 * Ferme le dialogue (peut s'éxecuter depuis un autre thread).
	 */
	
	public final void close() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public final void run() {
				WaitingDialog.this.dispose();
			}
			
		});
	}

}