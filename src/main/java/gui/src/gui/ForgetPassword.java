package gui;

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
		ForgetPasswordFrame.setSize(800,600);
		ForgetPasswordFrame.setLocationRelativeTo(null);
		ForgetPasswordFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ForgetPasswordFrame.getContentPane().setLayout(null);
		
		JLabel backgroundLabel = new JLabel("");
		backgroundLabel.setBounds(0, -30,800, 600);
		ForgetPasswordFrame.getContentPane().add(backgroundLabel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Personal Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(60, 30, 696, 377);
		ForgetPasswordFrame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel borderLabel = new JLabel("");
		borderLabel.setBounds(5, 17, 691, 348);
		panel.add(borderLabel);
		
		JLabel UsernameLabel = new JLabel("UserName");
		UsernameLabel.setFont(new Font("FreeSerif", Font.BOLD, 24));
		UsernameLabel.setBounds(34, 61, 175, 53);
		panel.add(UsernameLabel);
		
		usernametextField = new JTextField();
		usernametextField.setBounds(250, 61, 308, 38);
		panel.add(usernametextField);
		usernametextField.setColumns(10);
		
		JLabel NicknameLabel = new JLabel("NickName");
		NicknameLabel.setFont(new Font("FreeSerif", Font.BOLD, 24));
		NicknameLabel.setBounds(34, 177, 168, 53);
		panel.add(NicknameLabel);
		
		nicknametextfield = new JTextField();
		nicknametextfield.setBounds(250, 187, 308, 38);
		panel.add(nicknametextfield);
		nicknametextfield.setColumns(10);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setBounds(211, 283, 114, 25);
		panel.add(btnSubmit);
		
		JLabel lblEnterUsername = new JLabel("Enter username");
		lblEnterUsername.setVisible(false);
		lblEnterUsername.setForeground(new Color(255, 0, 0));
		lblEnterUsername.setBounds(250, 111, 128, 38);
		panel.add(lblEnterUsername);
		
		JLabel lblEnterNickName = new JLabel("Enter Nick name");
		lblEnterNickName.setVisible(false);
		lblEnterNickName.setForeground(new Color(255, 0, 0));
		lblEnterNickName.setBounds(250, 236, 148, 25);
		panel.add(lblEnterNickName);
		
		
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				String uname,nickname;
				uname=usernametextField.getText().toString();
				nickname=nicknametextfield.getText().toString();
				if(uname.equals(""))
				{
					lblEnterUsername.setVisible(true);
				}
				else if(nickname.equals(""))
				{
					lblEnterNickName.setVisible(true);
				}
				else
				{
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
