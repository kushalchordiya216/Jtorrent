package gui.src.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import java.awt.Toolkit;

public class ForgetPassword {

	public JFrame ForgetPasswordFrame;
	private JTextField usernametextField;
	private JTextField nicknametextfield;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ForgetPassword window = new ForgetPassword();
					window.ForgetPasswordFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ForgetPassword() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ForgetPasswordFrame = new JFrame();
		ForgetPasswordFrame.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(ForgetPassword.class.getResource("/gui/src/imgs/Profile_GroupFriend-RoundedBlack-512.png")));
		ForgetPasswordFrame.setSize(800, 600);
		ForgetPasswordFrame.setLocationRelativeTo(null);
		ForgetPasswordFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ForgetPasswordFrame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setBorder(new TitledBorder(null, "Personal Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(55, 100, 626, 377);
		ForgetPasswordFrame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel UsernameLabel = new JLabel("UserName");
		UsernameLabel.setForeground(new Color(255, 255, 255));
		UsernameLabel.setFont(new Font("Rockwell Condensed", Font.BOLD, 29));
		UsernameLabel.setBounds(101, 48, 175, 53);
		panel.add(UsernameLabel);

		usernametextField = new JTextField();
		usernametextField.setBounds(250, 61, 308, 38);
		panel.add(usernametextField);
		usernametextField.setColumns(10);

		JLabel NicknameLabel = new JLabel("NickName");
		NicknameLabel.setForeground(new Color(255, 255, 255));
		NicknameLabel.setFont(new Font("Rockwell Condensed", Font.BOLD, 26));
		NicknameLabel.setBounds(101, 174, 140, 53);
		panel.add(NicknameLabel);

		nicknametextfield = new JTextField();
		nicknametextfield.setBounds(250, 187, 308, 38);
		panel.add(nicknametextfield);
		nicknametextfield.setColumns(10);

		JButton btnSubmit = new JButton("Confirm");
		btnSubmit.setForeground(new Color(0, 102, 153));
		btnSubmit.setBackground(new Color(255, 255, 255));
		btnSubmit.setFont(new Font("Rockwell Condensed", Font.BOLD, 19));
		btnSubmit.setBounds(233, 292, 175, 38);
		panel.add(btnSubmit);

		JLabel lblEnterUsername = new JLabel("Enter username");
		lblEnterUsername.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblEnterUsername.setVisible(false);
		lblEnterUsername.setForeground(new Color(255, 0, 0));
		lblEnterUsername.setBounds(260, 97, 128, 38);
		panel.add(lblEnterUsername);

		JLabel lblEnterNickName = new JLabel("Enter Nick name");
		lblEnterNickName.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblEnterNickName.setVisible(false);
		lblEnterNickName.setForeground(new Color(255, 0, 0));
		lblEnterNickName.setBounds(260, 233, 148, 25);
		panel.add(lblEnterNickName);

		JLabel borderLabel = new JLabel("");
		borderLabel.setBounds(20, 19, 691, 348);
		panel.add(borderLabel);

		JLabel lblForgotPasswordDont = new JLabel("Forgot Password?? Don't Worry , We will fix it ...!");
		lblForgotPasswordDont.setForeground(Color.WHITE);
		lblForgotPasswordDont.setFont(new Font("Rockwell Condensed", Font.BOLD, 29));
		lblForgotPasswordDont.setBounds(82, 37, 618, 53);
		ForgetPasswordFrame.getContentPane().add(lblForgotPasswordDont);

		JLabel backgroundLabel = new JLabel("");
		backgroundLabel.setIcon(new ImageIcon(ForgetPassword.class.getResource("/gui/src/imgs/regbg.jpg")));
		backgroundLabel.setBounds(0, -30, 800, 600);
		ForgetPasswordFrame.getContentPane().add(backgroundLabel);

		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				String uname, nickname;
				uname = usernametextField.getText().toString();
				nickname = nicknametextfield.getText().toString();
				if (uname.equals("")) {
					lblEnterUsername.setVisible(true);
				} else if (nickname.equals("")) {
					lblEnterNickName.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(backgroundLabel, "Submited");
				}
			}
		});
		usernametextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				lblEnterUsername.setVisible(false);
			}
		});

		nicknametextfield.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				lblEnterNickName.setVisible(false);
			}
		});

	}
}
