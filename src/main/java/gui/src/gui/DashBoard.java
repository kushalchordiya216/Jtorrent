package gui.src.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;
import java.awt.Toolkit;
import javax.swing.JScrollPane;
import javax.swing.JList;

public class DashBoard {

	public JFrame DashBoardFrame;
	JProgressBar progressbar = null;

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
		DashBoardFrame.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(DashBoard.class.getResource("/gui/src/imgs/Profile_GroupFriend-RoundedBlack-512.png")));
		// DashBoardFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\91860\\Desktop\\Project\\gui\\src\\imgs\\Profile_GroupFriend-RoundedBlack-512.png"));
		DashBoardFrame.setSize(1227, 714);
		DashBoardFrame.setLocationRelativeTo(null);
		DashBoardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DashBoardFrame.getContentPane().setLayout(null);

		JPanel UpperPanel = new JPanel();
		UpperPanel.setBounds(0, 0, 1213, 237);
		UpperPanel.setBackground(Color.WHITE);
		DashBoardFrame.getContentPane().add(UpperPanel);
		UpperPanel.setLayout(null);

		JLabel lblIcon = new JLabel("");
		lblIcon.setIcon(new ImageIcon(
				DashBoard.class.getResource("/gui/src/imgs/images-removebg-preview-removebg-preview.png")));
		lblIcon.setBackground(Color.WHITE);
		lblIcon.setBounds(422, 10, 304, 170);
		UpperPanel.add(lblIcon);

		JLabel lblTagline = new JLabel("CONNECT | DOWNLOAD | SHARE");
		lblTagline.setForeground(new Color(0, 153, 153));
		lblTagline.setFont(new Font("FreeSerif", Font.BOLD, 24));
		lblTagline.setBounds(371, 164, 484, 37);
		UpperPanel.add(lblTagline);

		JLabel lblConnectMsg = new JLabel("You Are Connected!!");
		lblConnectMsg.setFont(new Font("FreeSerif", Font.BOLD | Font.ITALIC, 22));
		lblConnectMsg.setBackground(Color.WHITE);
		lblConnectMsg.setForeground(Color.BLACK);
		lblConnectMsg.setBounds(10, 10, 198, 37);
		UpperPanel.add(lblConnectMsg);

		JLabel lblHI = new JLabel("Hi ");
		lblHI.setForeground(Color.BLACK);
		lblHI.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 24));
		lblHI.setBackground(Color.WHITE);
		lblHI.setBounds(937, 10, 44, 37);
		UpperPanel.add(lblHI);

		JLabel lblUsername = new JLabel("\"Username\"");
		lblUsername.setForeground(Color.BLACK);
		lblUsername.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 24));
		lblUsername.setBackground(Color.WHITE);
		lblUsername.setBounds(967, 10, 246, 37);
		UpperPanel.add(lblUsername);

		JLabel lblLogout = new JLabel("LOGOUT??");
		lblLogout.setIcon(new ImageIcon(DashBoard.class.getResource("/gui/src/imgs/20712467.jpg")));
		lblLogout.setForeground(Color.BLACK);
		lblLogout.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
		lblLogout.setBackground(Color.WHITE);
		lblLogout.setBounds(1026, 44, 187, 98);
		UpperPanel.add(lblLogout);

		JPanel ButtonPanel = new JPanel();
		ButtonPanel.setBackground(new Color(255, 255, 255));
		ButtonPanel.setBounds(0, 236, 1213, 46);
		DashBoardFrame.getContentPane().add(ButtonPanel);
		ButtonPanel.setLayout(null);

		JButton btnShare = new JButton("SHARE");
		btnShare.setBorder(new LineBorder(new Color(0, 0, 0)));
		btnShare.setBackground(new Color(0, 153, 153));
		btnShare.setForeground(Color.BLACK);
		btnShare.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 28));
		btnShare.setBounds(0, 0, 399, 46);
		ButtonPanel.add(btnShare);

		JButton btnMyFiles = new JButton("MY FILES");
		btnMyFiles.setBorder(null);

		btnMyFiles.setBackground(Color.WHITE);
		btnMyFiles.setForeground(Color.BLACK);
		btnMyFiles.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 28));
		btnMyFiles.setBounds(401, 0, 432, 46);
		ButtonPanel.add(btnMyFiles);

		JButton btnProcess = new JButton("PROCESS");
		btnProcess.setBorder(null);
		btnProcess.setBackground(Color.WHITE);
		btnProcess.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 28));
		btnProcess.setBounds(837, 0, 376, 46);
		ButtonPanel.add(btnProcess);

		JPanel SharePanel = new JPanel();
		SharePanel.setBackground(new Color(0, 153, 153));
		SharePanel.setBounds(0, 279, 1213, 398);
		DashBoardFrame.getContentPane().add(SharePanel);
		SharePanel.setLayout(null);

		JPanel MyFilesPanel = new JPanel();
		// MyFilesPanel.setBackground(new Color(0, 153, 153));
		MyFilesPanel.setBounds(0, 279, 1213, 398);
		DashBoardFrame.getContentPane().add(MyFilesPanel);
		MyFilesPanel.setLayout(null);
		MyFilesPanel.setVisible(false);

		JPanel ProcessPanel = new JPanel();
		// ProcessPanel.setBackground(new Color(0, 153, 153));
		ProcessPanel.setBounds(0, 279, 1213, 398);
		DashBoardFrame.getContentPane().add(ProcessPanel);
		ProcessPanel.setLayout(null);
		progressbar = new JProgressBar();
		progressbar.setBounds(10, 20, 1200, 100);
		progressbar.setValue(0);
		progressbar.setStringPainted(true);
		ProcessPanel.add(progressbar);
		ProcessPanel.setVisible(false);

		JButton btnUploadMetadata = new JButton("UPLOAD METADATA");

		btnUploadMetadata.setBorder(new LineBorder(new Color(0, 0, 0), 3));
		btnUploadMetadata.setBackground(new Color(255, 255, 255));
		btnUploadMetadata.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 20));
		btnUploadMetadata.setBounds(130, 173, 255, 51);
		SharePanel.add(btnUploadMetadata);

		JPanel DownloadPanel = new JPanel();
		DownloadPanel.setBackground(new Color(255, 255, 255));
		DownloadPanel.setBounds(771, 24, 371, 337);
		SharePanel.add(DownloadPanel);
		DownloadPanel.setLayout(null);

		JButton btnStartDownload = new JButton("START DOWNLOAD");
		btnStartDownload.setEnabled(false);
		btnStartDownload.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 22));
		btnStartDownload.setBounds(69, 268, 235, 43);
		DownloadPanel.add(btnStartDownload);

		JLabel lblNoMetadataFile = new JLabel("NO METADATA FILE FOUND!!");
		lblNoMetadataFile.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblNoMetadataFile.setHorizontalAlignment(SwingConstants.CENTER);
		lblNoMetadataFile.setBounds(10, 10, 351, 43);
		DownloadPanel.add(lblNoMetadataFile);

		JLabel lblFileNameHead = new JLabel("File Name-");
		lblFileNameHead.setForeground(new Color(0, 153, 153));
		lblFileNameHead.setFont(new Font("FreeSerif", Font.BOLD, 20));
		lblFileNameHead.setBounds(10, 82, 114, 43);
		DownloadPanel.add(lblFileNameHead);

		JLabel lblFileName = new JLabel("GetFileName");
		lblFileName.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblFileName.setBounds(138, 82, 200, 43);
		DownloadPanel.add(lblFileName);

		JLabel lblFileSizeHead = new JLabel("File size-");
		lblFileSizeHead.setForeground(new Color(0, 139, 139));
		lblFileSizeHead.setFont(new Font("FreeSerif", Font.BOLD, 20));
		lblFileSizeHead.setBounds(10, 171, 114, 43);
		DownloadPanel.add(lblFileSizeHead);

		JLabel lblGetfilesize = new JLabel("GetFileSize\r\n");
		lblGetfilesize.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblGetfilesize.setBounds(138, 171, 114, 43);
		DownloadPanel.add(lblGetfilesize);

		btnShare.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnMyFiles.setBorder(null);
				btnProcess.setBorder(null);
				btnShare.setBorder(new LineBorder(new Color(0, 0, 0)));
				btnShare.setBackground(new Color(0, 153, 153));
				btnProcess.setBackground(new Color(255, 255, 255));
				btnMyFiles.setBackground(new Color(255, 255, 255));
				SharePanel.setVisible(true);
				ProcessPanel.setVisible(false);
				MyFilesPanel.setVisible(false);
			}
		});
		btnMyFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnShare.setBorder(null);
				btnShare.setBackground(new Color(255, 255, 255));
				btnProcess.setBackground(new Color(255, 255, 255));
				btnMyFiles.setBackground(new Color(0, 153, 153));
				btnProcess.setBorder(null);
				btnMyFiles.setBorder(new LineBorder(new Color(0, 0, 0)));
				SharePanel.setVisible(false);
				ProcessPanel.setVisible(false);

				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setBounds(0, 0, 1213, 398);
				MyFilesPanel.add(scrollPane);

				// DefaultListModel<String> week1=new DefaultListModel<String>();
				// week1.addElement("abc");
				String week[] = { "abc", "def", "ijk", "lmn",
						"opsdfsadfdafdsfsbshdfbhjsdbfbsdfbjdsfbhdsbfdsfbdsjfbdsjsbhjbsf vjsbvb sfjk vjkfvbfkgbjkfbjfbgbfvbfvnvmnvjfnvjfnvjfnjfvnfjvjnvjfnvnjfnvjfnvjfnvjfnvjfnvfjvnfjnvjfnvfjvnfjvnfjvnfjbjfbsjdbfjsdbfbdsbfhsdbfjbsdfbhdsbfbdsbfjdsbfjhsdbbdsfbdsjfbdbf hjdsbfhjdbsjfbdsjbfjsdbfjsbdfjb",
						"abc", "def", "ijk", "abc", "def", "ijk", "lmn", "opsdfsadfdafdsfs", "abc", "def", "ijk", "lmn",
						"opsdfsadfdafdsfs", "abc", "def", "ijk", "lmn", "opsdfsadfdafdsfs", "abc", "def", "ijk", "lmn",
						"opsdfsadfdafdsfs",
						"dssfnghb fsbgjsbgbfsbhjsbjvbsjbgjfsdbgfdsjbgjfsbjsjgbsjb gjsdbjbfsjgbjfsbjgbfsjbgjfsbgjfs" };
				JList list = new JList(week);
				scrollPane.setViewportView(list);

				MyFilesPanel.setVisible(true);
			}
		});
		btnProcess.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnMyFiles.setBorder(null);
				btnShare.setBackground(new Color(255, 255, 255));
				btnProcess.setBackground(new Color(0, 153, 153));
				btnMyFiles.setBackground(new Color(255, 255, 255));
				btnShare.setBorder(null);
				btnProcess.setBorder(new LineBorder(new Color(0, 0, 0)));
				SharePanel.setVisible(false);
				MyFilesPanel.setVisible(false);
				ProcessPanel.setVisible(true);

			}
		});
		btnUploadMetadata.addMouseListener(new MouseAdapter() {
			@Override

			public void mouseClicked(MouseEvent e) {
				JFileChooser metadataChoose = new JFileChooser("C:");
				int r = metadataChoose.showSaveDialog(null);
				if (r == JFileChooser.APPROVE_OPTION) {
					// set the label to the path of the selected file
					lblFileName.setText(metadataChoose.getSelectedFile().getName());
					btnStartDownload.setEnabled(true);
					lblNoMetadataFile.setFont(new Font("Times New Roman", Font.BOLD, 16));
					lblNoMetadataFile.setText("METADATA SUCCESSFULLY UPLOAD!");
					lblNoMetadataFile.setForeground(new Color(0, 153, 153));

				}
				// if the user cancelled the operation
				else {
					// lbl.setText("the user cancelled the operation");
				}

			}
		});
		btnStartDownload.addMouseListener(new MouseAdapter() {
			@Override

			public void mouseClicked(MouseEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						fill();
					}
				}).start();
			}
		});

	}

	public void fill() {
		int i = 0;
		try {
			while (i <= 100) {
				// fill the menu bar
				progressbar.setValue(i + 10);

				// delay the thread
				Thread.sleep(1000);
				i += 20;
			}
		} catch (Exception e) {
		}
	}
}