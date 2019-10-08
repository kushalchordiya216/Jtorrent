package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

public class Register {

	public JFrame registerFrame;
	private JTextField userNameTextField;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordfield;
	private JTextField nicknametextfield;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Register window = new Register();
					window.registerFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Register() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		registerFrame = new JFrame();
		registerFrame.setSize(800,600);
		registerFrame.setLocationRelativeTo(null);
		registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		registerFrame.getContentPane().setLayout(null);
		
		JLabel backgroundLabel = new JLabel("");
		backgroundLabel.setLocation(0, -30);
		backgroundLabel.setSize(800, 600);
		registerFrame.getContentPane().add(backgroundLabel);
		
		JLabel registerLabel = new JLabel("Register");
		registerLabel.setFont(new Font("FreeSerif", Font.BOLD, 24));
		registerLabel.setBounds(302, 22, 117, 37);
		registerFrame.getContentPane().add(registerLabel);
		
		JLabel userNameLabel = new JLabel("UserName");
		userNameLabel.setFont(new Font("FreeSerif", Font.BOLD, 18));
		userNameLabel.setBounds(170, 68, 100, 31);
		registerFrame.getContentPane().add(userNameLabel);
		
		userNameTextField = new JTextField();
		userNameTextField.setBounds(371, 69, 309, 31);
		registerFrame.getContentPane().add(userNameTextField);
		userNameTextField.setColumns(10);
		
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setFont(new Font("FreeSerif", Font.BOLD, 18));
		passwordLabel.setBounds(170, 158, 100, 31);
		registerFrame.getContentPane().add(passwordLabel);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(371, 159, 309, 31);
		registerFrame.getContentPane().add(passwordField);
		
		JLabel ConfirmPasswordLabel = new JLabel("Confirm Password");
		ConfirmPasswordLabel.setFont(new Font("FreeSerif", Font.BOLD, 18));
		ConfirmPasswordLabel.setBounds(170, 240, 169, 37);
		registerFrame.getContentPane().add(ConfirmPasswordLabel);
		
		confirmPasswordfield = new JPasswordField();
		confirmPasswordfield.setBounds(371, 244, 309, 31);
		registerFrame.getContentPane().add(confirmPasswordfield);
		
		JLabel checkPasswordLabel = new JLabel("Password not matches.");
		checkPasswordLabel.setForeground(new Color(255, 0, 0));
		checkPasswordLabel.setBounds(371, 287, 178, 25);
		checkPasswordLabel.setVisible(false);
		registerFrame.getContentPane().add(checkPasswordLabel);

		JLabel lblEnterUsername = new JLabel("Enter UserName");
		lblEnterUsername.setForeground(new Color(255, 0, 0));
		lblEnterUsername.setBounds(371, 105, 168, 31);
		lblEnterUsername.setVisible(false);
		registerFrame.getContentPane().add(lblEnterUsername);
		
		JLabel lblEnterPassword = new JLabel("Enter Password");
		lblEnterPassword.setForeground(new Color(255, 0, 0));
		lblEnterPassword.setBounds(371, 202, 168, 25);
		lblEnterPassword.setVisible(false);
		registerFrame.getContentPane().add(lblEnterPassword);
		
		
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Personal Details", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setBounds(165, 309, 520, 152);
		registerFrame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel PersonalDetailLabel = new JLabel("");
		PersonalDetailLabel.setBounds(5, 17, 510, 130);
		panel.add(PersonalDetailLabel);
		
		JLabel questionLabel = new JLabel("Enter your nick name");
		questionLabel.setFont(new Font("FreeSerif", Font.BOLD, 18));
		questionLabel.setBounds(48, 17, 230, 29);
		panel.add(questionLabel);
		
		nicknametextfield = new JTextField();
		nicknametextfield.setBounds(48, 69, 230, 39);
		panel.add(nicknametextfield);
		nicknametextfield.setColumns(10);
		
		JLabel checknicknamelabel = new JLabel("Enter nick name");
		checknicknamelabel.setForeground(new Color(255, 0, 0));
		checknicknamelabel.setBounds(48, 112, 140, 28);
		checknicknamelabel.setVisible(false);
		panel.add(checknicknamelabel);
		userNameTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				lblEnterUsername.setVisible(false);
			}
		});
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				lblEnterPassword.setVisible(false);
			}
		});
		confirmPasswordfield.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				checkPasswordLabel.setVisible(false);
			}
		});
		nicknametextfield.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				checknicknamelabel.setVisible(false);
			}
		});
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String pass,confirmpass,uname,nickname;
				nickname=nicknametextfield.getText().toString();
				uname=userNameTextField.getText().toString();
				pass=passwordField.getText().toString();
				confirmpass=confirmPasswordfield.getText().toString();
				if(uname.equals(""))
				{
					lblEnterUsername.setVisible(true);
				}
				else if(pass.equals(""))
				{
					lblEnterPassword.setVisible(true);
				}
				else if(!pass.equals(confirmpass))
				{
					checkPasswordLabel.setVisible(true);
				}
				else if(nickname.equals(""))
				{
					checknicknamelabel.setVisible(true);
				}
				else
				{
					registerFrame.setVisible(false);
					DashBoard dashboard=new DashBoard();
					dashboard.DashBoardFrame.setVisible(true);
				}
			}
		});
		btnSubmit.setBounds(334, 495, 114, 25);
		registerFrame.getContentPane().add(btnSubmit);
	}
}
