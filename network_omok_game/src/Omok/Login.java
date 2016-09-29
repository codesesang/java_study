package Omok;
import java.awt.BorderLayout;
import member.*;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
public class Login extends JFrame {

	private JPanel Panel;
	private JTextField textField_id;
	private JTextField textField_pwd;
	private JButton btnJoin;
	private JButton btnLogin;
	
	private Image image = new ImageIcon("/image/join.PNG").getImage(); 
	
	public void paint(Graphics g){ 
	    super.paint(g);       
	    g.drawImage(image, 0, 0, getWidth(), getHeight(), this); 
	}
	
	

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	
	
	public Login() {
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 552, 477);
		Panel = new JPanel();
		Panel.setBackground(Color.WHITE);
		Panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		Panel.setLayout(new BorderLayout(0, 0));
		setContentPane(Panel);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		Panel.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblId = new JLabel("");
		lblId.setFont(new Font("굴림", Font.PLAIN, 19));
		lblId.setHorizontalAlignment(SwingConstants.RIGHT);
		lblId.setBounds(141, 248, 91, 39);
		panel.add(lblId);
		
		ImageIcon LabelImage=new ImageIcon(Join.class.getResource("/image/id.png")); //이미지 변수에 이미지를 넣음 
		Image temp=LabelImage.getImage();
		Image tempafter=temp.getScaledInstance(80,25, java.awt.Image.SCALE_SMOOTH); //사이즈조정
		LabelImage=new ImageIcon(tempafter);
		lblId.setIcon(LabelImage);
		
		textField_id = new JTextField();
		textField_id.setBounds(255, 257, 116, 24);
		panel.add(textField_id);
		textField_id.setColumns(10);
		
		JLabel lblPwd = new JLabel("");
		lblPwd.setFont(new Font("굴림", Font.PLAIN, 19));
		lblPwd.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPwd.setBounds(127, 304, 105, 21);
		panel.add(lblPwd);
		
		ImageIcon Label_pwd=new ImageIcon(Join.class.getResource("/image/pwd.png")); //이미지 변수에 이미지를 넣음 
		Image temp_pwd=Label_pwd.getImage();
		Image tempafter_pwd=temp_pwd.getScaledInstance(80,25, java.awt.Image.SCALE_SMOOTH); //사이즈조정
		Label_pwd=new ImageIcon(tempafter_pwd);
		lblPwd.setIcon(Label_pwd);
		
		textField_pwd = new JTextField();
		textField_pwd.setBounds(255, 301, 116, 24);
		panel.add(textField_pwd);
		textField_pwd.setColumns(10);
		
		btnJoin = new JButton("");
		
		ImageIcon button_join=new ImageIcon(Join.class.getResource("/image/icon_join.png")); //이미지 변수에 이미지를 넣음 
		Image temp_join=button_join.getImage();
		Image tempafter_join=temp_join.getScaledInstance(105,27, java.awt.Image.SCALE_SMOOTH); //사이즈조정
		button_join=new ImageIcon(tempafter_join);
		btnJoin.setIcon(button_join);
		
		btnJoin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Join join=new Join();
				join.main(null);
			}
		});
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		btnJoin.setBounds(141, 356, 105, 27);
		panel.add(btnJoin);
	
		btnLogin = new JButton("");
		
		ImageIcon button_login=new ImageIcon(Join.class.getResource("/image/icon_login.png")); //이미지 변수에 이미지를 넣음 
		Image temp_login=button_login.getImage();
		Image tempafter_login=temp_login.getScaledInstance(105,27, java.awt.Image.SCALE_SMOOTH); //사이즈조정
		button_login=new ImageIcon(tempafter_login);
		btnLogin.setIcon(button_login);
		
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				OmokClient client =new OmokClient("네트워크 오목 게임");		
				member m = new member();
				String id = textField_id.getText();
				String pwd = textField_pwd.getText();
				if(1 == m.memberLoginCheck(id, pwd))
				{
					client.main(textField_id.getText());
				}
				else
				{
					JOptionPane.showMessageDialog(null, "아이디 및 비밀번호를 확인해주세요.");
				}
			}
		});
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				

			}
		});
		btnLogin.setBounds(282, 356, 105, 27);
		panel.add(btnLogin);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setIcon(new ImageIcon(Login.class.getResource("/image/back.PNG")));
		lblNewLabel.setBounds(14, 12, 496, 224);
		panel.add(lblNewLabel);
		
		
	}
}