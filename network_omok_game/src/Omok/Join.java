package Omok;
import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPasswordField;
import java.awt.Color;
import member.*;

public class Join extends JFrame {

	private JPanel Panel_join;
	private JTextField textField_id1;
	private JPasswordField passwordField_one;
	private JPasswordField passwordField_two;
	

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Join frame = new Join();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setSize(552,477);
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
	
	
	
	public Join() {
		setBackground(Color.WHITE);
		
		Panel_join = new JPanel();
		Panel_join.setBackground(Color.WHITE);
		Panel_join.setBorder(new EmptyBorder(5, 5, 5, 5));
		Panel_join.setLayout(new BorderLayout(0, 0));
		Panel_join.setVisible(true);
		setContentPane(Panel_join);
		
		JPanel panel_join = new JPanel();
		panel_join.setBackground(Color.WHITE);
		Panel_join.add(panel_join, BorderLayout.CENTER);
		panel_join.setLayout(null);
		
		JLabel joinlabel = new JLabel("");
		joinlabel.setHorizontalAlignment(SwingConstants.CENTER);
		joinlabel.setBounds(30, 25, 450, 80);
		panel_join.add(joinlabel);
		
		joinlabel.setIcon(new ImageIcon(Join.class.getResource("/image/jointitle.png")));
		
		
		
		JLabel label_id = new JLabel("");
		label_id.setHorizontalAlignment(SwingConstants.RIGHT);
		label_id.setBounds(81, 141, 100, 25);
		panel_join.add(label_id);
		
		ImageIcon LabelImage=new ImageIcon(Join.class.getResource("/image/id.png")); //이미지 변수에 이미지를 넣음 
		Image temp=LabelImage.getImage();
		Image tempafter=temp.getScaledInstance(80,25, java.awt.Image.SCALE_SMOOTH); //사이즈조정
		LabelImage=new ImageIcon(tempafter);
		label_id.setIcon(LabelImage);
		
		textField_id1 = new JTextField();
		textField_id1.setColumns(10);
		textField_id1.setBounds(195, 141, 150, 25);
		panel_join.add(textField_id1);
		
		JLabel label_pwd = new JLabel("");
		label_pwd.setFont(new Font("굴림", Font.PLAIN, 19));
		label_pwd.setHorizontalAlignment(SwingConstants.RIGHT);
		label_pwd.setBounds(80, 193, 100, 25);
		panel_join.add(label_pwd);
		
		ImageIcon Label_pwd=new ImageIcon(Join.class.getResource("/image/pwd.png")); //이미지 변수에 이미지를 넣음 
		Image temp_pwd=Label_pwd.getImage();
		Image tempafter_pwd=temp_pwd.getScaledInstance(80,25, java.awt.Image.SCALE_SMOOTH); //사이즈조정
		Label_pwd=new ImageIcon(tempafter_pwd);
		label_pwd.setIcon(Label_pwd);
		
		JButton button_id = new JButton("");
		
		ImageIcon button_iconid=new ImageIcon(Join.class.getResource("/image/check.png")); //이미지 변수에 이미지를 넣음 
		Image temp_iconid=button_iconid.getImage();
		Image tempafter_iconid=temp_iconid.getScaledInstance(88,27, java.awt.Image.SCALE_SMOOTH); //사이즈조정
		button_iconid=new ImageIcon(tempafter_iconid);
		button_id.setIcon(button_iconid);
		
		
		button_id.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String id = textField_id1.getText();
				member m = new member();
				if(1 == m.MemberCheck(id))
				{
					JOptionPane.showMessageDialog(null, "아이디가 존재합니다.");
				}
				else if(id.equals(""))
				{
					JOptionPane.showMessageDialog(null, "아이디를 입력해주세요.");
				}
				else
				{
					JOptionPane.showMessageDialog(null, "사용가능한 아이디입니다");
				}

			}
		});
		button_id.setBounds(359, 142, 75, 20);
		panel_join.add(button_id);
		button_id.setVisible(true);
		
		JLabel label_pwdre = new JLabel("");
		label_pwdre.setFont(new Font("굴림", Font.PLAIN, 19));
		label_pwdre.setHorizontalAlignment(SwingConstants.RIGHT);
		label_pwdre.setBounds(30, 249, 153, 23);
		panel_join.add(label_pwdre);
		
		ImageIcon Label_pwdre=new ImageIcon(Join.class.getResource("/image/pwdcheck.png")); //이미지 변수에 이미지를 넣음 
		Image temp_pwdre=Label_pwdre.getImage();
		Image tempafter_pwdre=temp_pwdre.getScaledInstance(80,25, java.awt.Image.SCALE_SMOOTH); //사이즈조정
		Label_pwdre=new ImageIcon(tempafter_pwdre);
		label_pwdre.setIcon(Label_pwdre);
		
		
		passwordField_one = new JPasswordField();
		passwordField_one.setToolTipText("");
		passwordField_one.setBounds(194, 195, 150, 24);
		panel_join.add(passwordField_one);
		
		passwordField_two = new JPasswordField();
		passwordField_two.setBounds(194, 250, 150, 24);
		panel_join.add(passwordField_two);
		
		JButton canclebtn = new JButton("");
		
		canclebtn.setIcon(new ImageIcon(Join.class.getResource("/image/cancel.png")));
		
		
		canclebtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		canclebtn.setBounds(129, 319, 105, 31);
		panel_join.add(canclebtn);
		canclebtn.setVisible(true);
		
		JButton btnfinish = new JButton("");
		
		btnfinish.setIcon(new ImageIcon(Join.class.getResource("/image/finish.png")));
		
		btnfinish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String id = textField_id1.getText();
				String pwd = passwordField_one.getText();
				member m = new member();
				if(!passwordField_one.getText().equals(passwordField_two.getText()))
				{
					JOptionPane.showMessageDialog(null, "비밀번호가 다릅니다.");
				}
				else if(1 == m.MemberCheck(id))
				{
					JOptionPane.showMessageDialog(null, "아이디가 존재합니다.");
				}
				else if(id.equals(""))
				{
					JOptionPane.showMessageDialog(null, "아이디를 입력해주세요.");
				}
				else if(pwd.equals(""))
				{
					JOptionPane.showMessageDialog(null, "비밀번호를 입력해주세요.");
				}
				else
				{
					m.insertMember(id,pwd);
					JOptionPane.showMessageDialog(null, "회원가입을 축하드립니다!");
				}

			}
		});
		btnfinish.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Login login=new Login();
				Login.main(null);
			}
		});
		btnfinish.setBounds(281, 319, 105, 31);
		panel_join.add(btnfinish);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(Join.class.getResource("/image/bg.png")));
		lblNewLabel.setBounds(-12, -18, 540, 462);
		panel_join.add(lblNewLabel);
		btnfinish.setVisible(true);
		
		
	}
	
	
}
