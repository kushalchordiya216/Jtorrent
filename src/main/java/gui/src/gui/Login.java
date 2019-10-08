package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Login{

	public JFrame LoginFrame;
	private JTextField userNametextField;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.LoginFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		LoginFrame = new JFrame();
		LoginFrame.setSize(800,600);
		LoginFrame.setLocationRelativeTo(null);
		LoginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		LoginFrame.getContentPane().setLayout(null);
		JLabel backgroundLabel = new JLabel("");
		backgroundLabel.setBackground(new Color(0, 0, 0));
		backgroundLabel.setIcon(null);
		backgroundLabel.setBounds(0, -29,788,587);
		LoginFrame.getContentPane().add(backgroundLabel);
		
		JLabel jTorrentLabel = new JLabel("JTorrent");
		jTorrentLabel.setBackground(new Color(0, 255, 0));
		jTorrentLabel.setForeground(Color.ORANGE);
		jTorrentLabel.setFont(new Font("FreeSerif", Font.BOLD, 32));
		jTorrentLabel.setBounds(300, 74, 148, 67);
		LoginFrame.getContentPane().add(jTorrentLabel);
		
		JLabel userNameLabel = new JLabel("Username");
		userNameLabel.setFont(new Font("FreeSerif", Font.BOLD, 22));
		userNameLabel.setBounds(197, 146, 122, 40);
		LoginFrame.getContentPane().add(userNameLabel);
		
		userNametextField = new JTextField();
		userNametextField.setBounds(403, 153, 164, 40);
		LoginFrame.getContentPane().add(userNametextField);
		userNametextField.setColumns(10);
		
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setFont(new Font("FreeSerif", Font.BOLD, 22));
		passwordLabel.setBounds(197, 272, 122, 40);
		LoginFrame.getContentPane().add(passwordLabel);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(400, 272, 164, 40);
		LoginFrame.getContentPane().add(passwordField);
		
				
		JLabel lblEnterUsername = new JLabel("Enter username");
		lblEnterUsername.setForeground(new Color(255, 0, 0));
		lblEnterUsername.setBounds(400, 195, 122, 25);
		lblEnterUsername.setVisible(false);
		LoginFrame.getContentPane().add(lblEnterUsername);
		
		JLabel lblEnterPassword = new JLabel("Enter Password");
		lblEnterPassword.setForeground(new Color(255, 0, 0));
		lblEnterPassword.setBounds(400, 320, 122, 25);
		lblEnterPassword.setVisible(false);
		LoginFrame.getContentPane().add(lblEnterPassword);
		
		JLabel RegisterLabel = new JLabel("New User ? Register.");
		RegisterLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LoginFrame.setVisible(false);
				Register register=new Register();
				register.registerFrame.setVisible(true);
			}
		});
		RegisterLabel.setFont(new Font("FreeSerif", Font.BOLD, 18));
		RegisterLabel.setBounds(553, 428, 191, 25);
		LoginFrame.getContentPane().add(RegisterLabel);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String uname,pass;
				uname=userNametextField.getText().toString();
				pass=passwordField.getText().toString();
				if(uname.equals(""))
				{
					lblEnterUsername.setVisible(true);
				}
				else if(pass.equals(""))
				{
					lblEnterPassword.setVisible(true);
				}
				else
				{
					DashBoard dashboard=new DashBoard();
					LoginFrame.setVisible(false);
					dashboard.DashBoardFrame.setVisible(true);
				}
			}
		});
		btnLogin.setBounds(316, 373, 114, 25);
		LoginFrame.getContentPane().add(btnLogin);
		
		JLabel ForgetPasswordLabel = new JLabel("Forget Password ?");
		ForgetPasswordLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LoginFrame.setVisible(false);
				ForgetPassword forgetPassword=new ForgetPassword();
				forgetPassword.ForgetPasswordFrame.setVisible(true);
			}
		});
		ForgetPasswordLabel.setFont(new Font("FreeSerif", Font.BOLD, 18));
		ForgetPasswordLabel.setBounds(144, 428, 175, 21);
		LoginFrame.getContentPane().add(ForgetPasswordLabel);
		
		userNametextField.addKeyListener(new KeyAdapter() {
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
	}

}
