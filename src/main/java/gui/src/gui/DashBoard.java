package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.border.TitledBorder;

public class DashBoard {

	public JFrame DashBoardFrame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DashBoard window = new DashBoard();
					window.DashBoardFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DashBoard() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		DashBoardFrame = new JFrame();
		DashBoardFrame.setSize(800, 600);
		DashBoardFrame.setLocationRelativeTo(null);
		DashBoardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DashBoardFrame.getContentPane().setLayout(null);
		
		JLabel BackgroundLabel = new JLabel("");
		BackgroundLabel.setForeground(new Color(248, 248, 255));
		BackgroundLabel.setBackground(new Color(255, 250, 250));
		BackgroundLabel.setBounds(0,-30,800,600);
		DashBoardFrame.getContentPane().add(BackgroundLabel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "User Credential", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(0, 0, 800, 200);
		DashBoardFrame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel credentialshowingLabel = new JLabel("");
		credentialshowingLabel.setBounds(5, 17, 800, 200);
		panel.add(credentialshowingLabel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(0, 194, 800,370);
		DashBoardFrame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(5, 17, 800, 400);
		panel_1.add(lblNewLabel);
	}
}
