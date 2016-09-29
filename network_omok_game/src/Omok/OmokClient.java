package Omok;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import member.member;

import java.awt.event.*;
import java.awt.geom.*;

public class OmokClient extends JFrame implements Runnable, ActionListener{

	private TextArea msgView=new TextArea("", 1,1,1);   										// �޽����� �����ִ� ����
	private TextField sendBox=new TextField("");         										// ���� �޽����� ���� ����
	public TextField nameBox=new TextField();          										// ����� �̸� ����
	private TextField roomBox=new TextField("0");        										// �� ��ȣ ����
	
	private Label pInfo=new Label("����:  ��");													// �濡 ������ �ο��� ���� �����ִ� ���̺�
	private Label infoView=new Label("", 1);													// ���� ������ �����ִ� ���̺�
	
	private java.awt.List pList=new java.awt.List();  											// ����� ����� �����ִ� ����Ʈ
	
	private JButton startButton = new JButton(new ImageIcon("image/start.png"));    										// �뱹 ���� ��ư
	private JButton stopButton  = new JButton(new ImageIcon("image/lose.png"));         										// ��� ��ư
	private JButton enterButton = new JButton(new ImageIcon("image/in.png"));    				// �����ϱ� ��ư
	private JButton exitButton  = new JButton(new ImageIcon("image/out.png"));      										// ���Ƿ� ��ư
	private JButton rankButton  = new JButton("�� ŷ");
	private JButton myRankButton= new JButton("�� ����");
	
	
	private Omok board=new Omok(19,24);      													// ������ ��ü

	private BufferedReader reader;                         										// �Է� ��Ʈ��
	private PrintWriter writer;                               									// ��� ��Ʈ��
	private Socket socket;                                 										// ����
	private int roomNumber=-1;                           										// �� ��ȣ
	private String userName=null;                          										// ����� �̸�
	
	public OmokClient(String title){                        									// ������
		super(title);
		
		nameBox.setEditable(false);																// ���̵� ���� �Ұ��ϰ�.
		
		getContentPane().setLayout(null);                                										// ���̾ƿ��� ������� �ʴ´�.
   
	    msgView.setEditable(false);																// ���� ������Ʈ�� �����ϰ� ��ġ�Ѵ�.
	
	    infoView.setBounds(10,30,494,30);

	    infoView.setBackground(new Color(200,200,255));

	    board.setLocation(10,70);																// ������ ��ġ�� �����Ѵ�.

	    getContentPane().add(infoView);
	    getContentPane().add(board);
	
	    Panel p=new Panel();
	
	    p.setBackground(new Color(200,255,255));
	    p.setLayout(new GridLayout(4,2));
	
	    p.add(new JLabel(new ImageIcon("image/id.png")));
	    p.add(nameBox);
	    p.add(new JLabel(new ImageIcon("image/roomnum.png"))); 
	    p.add(roomBox);
	    enterButton.setBackground(new Color(153, 153, 153));
	
	    p.add(enterButton); exitButton.setBackground(new Color(153, 153, 153));
 p.add(exitButton);
	    rankButton.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
	    		member m = new member();
	    		String id = nameBox.getText();
	    		JOptionPane.showMessageDialog(null, m.Ranking());
	    	}
	    });
	
	    p.add(rankButton); myRankButton.addMouseListener(new MouseAdapter() {
 	@Override
 	public void mouseClicked(MouseEvent arg0) {
 		member m = new member();
 		String id = nameBox.getText();
 		JOptionPane.showMessageDialog(null, m.MyInformation(id));
 	}
 });
 p.add(myRankButton);
	
	    p.setBounds(510,30, 250,100);
	    
	   

	    Panel p2=new Panel();
	
	    p2.setBackground(new Color(255,255,100));
	    p2.setLayout(new BorderLayout());
	
	    Panel p2_1=new Panel();
	
	    p2_1.add(startButton); p2_1.add(stopButton);
	
	    p2.add(pInfo,"North"); p2.add(pList,"Center"); p2.add(p2_1,"South");
	
	    startButton.setEnabled(false); stopButton.setEnabled(false);
	
	    p2.setBounds(510,135,250,155);
	
	    Panel p3=new Panel();
	
	    p3.setLayout(new BorderLayout());
	
	    p3.add(msgView,"Center");
	    p3.add(sendBox, "South");
	
	    p3.setBounds(510, 300, 250,265);
	
	    getContentPane().add(p); getContentPane().add(p2); getContentPane().add(p3);
	
	    sendBox.addActionListener(this);														// �̺�Ʈ �����ʸ� ����Ѵ�.
	    enterButton.addActionListener(this);
	    exitButton.addActionListener(this);
	    startButton.addActionListener(this);
	    stopButton.addActionListener(this);

	    addWindowListener(new WindowAdapter(){													// ������ �ݱ� ó��
	    	public void windowClosing(WindowEvent we){
	    		System.exit(0);
	        }
	    });
	}
	
	public void actionPerformed(ActionEvent ae){												// ������Ʈ���� �׼� �̺�Ʈ ó��
		if(ae.getSource()==sendBox){             												// �޽��� �Է� �����̸�

			String msg=sendBox.getText();

			if(msg.length()==0)		return;
			if(msg.length()>=30)	msg=msg.substring(0,30);

			try{  
				writer.println("[MSG]"+msg);

				sendBox.setText("");
			}catch(Exception ie){}
		}
		else if(ae.getSource()==enterButton){         											// �����ϱ� ��ư�̸�
			try{
		    	if(Integer.parseInt(roomBox.getText())<1){
		        	infoView.setText("���ȣ�� �߸��Ǿ����ϴ�. 1�̻�");
		
		        	return;
		        }

		    	writer.println("[ROOM]"+Integer.parseInt(roomBox.getText()));

		    	msgView.setText("");
			}catch(Exception ie){
				infoView.setText("�Է��Ͻ� ���׿� ������ �ҽ��ϴ�.");
			}
		}
		else if(ae.getSource()==exitButton){           											// ���Ƿ� ��ư�̸�
			try{
				goToWaitRoom();

				startButton.setEnabled(false);
				stopButton.setEnabled(false);
			}catch(Exception e){}
		}

		else if(ae.getSource()==startButton){          											// �뱹 ���� ��ư�̸�
			try{
				writer.println("[START]");

				infoView.setText("����� ������ ��ٸ��ϴ�.");

				startButton.setEnabled(false);
			}catch(Exception e){}
		}

		else if(ae.getSource()==stopButton){          											// ��� ��ư�̸�
			try{
				writer.println("[DROPGAME]");
				String id = nameBox.getText();
				member m = new member();
				m.Lose(id);
				endGame("����Ͽ����ϴ�.");
			}catch(Exception e){}
		}
	}

	void goToWaitRoom(){                   														// ���Ƿ� ��ư�� ������ ȣ��ȴ�.
		if(userName==null){
			String name=nameBox.getText().trim();

			userName=name;

			writer.println("[NAME]"+userName);    

			nameBox.setText(userName);
			nameBox.setEditable(false);
		}  

		msgView.setText("");

		writer.println("[ROOM]0");

		infoView.setText("���ǿ� �����ϼ̽��ϴ�.");

		roomBox.setText("0");

		enterButton.setEnabled(true);
		exitButton.setEnabled(false);
	}

	public void run(){
		String msg;                            		 											// �����κ����� �޽���
		try{
			while((msg=reader.readLine())!=null){
				if(msg.startsWith("[STONE]")){     												// ������� ���� ���� ��ǥ
					String temp=msg.substring(7);
	
					int x=Integer.parseInt(temp.substring(0,temp.indexOf(" ")));
					int y=Integer.parseInt(temp.substring(temp.indexOf(" ")+1));
	
					board.putOpponent(x, y);     												// ������� ���� �׸���.
					board.setEnable(true);        												// ����ڰ� ���� ���� �� �ֵ��� �Ѵ�.
				}
	
				else if(msg.startsWith("[ROOM]")){    											// �濡 ����
					if(!msg.equals("[ROOM]0")){          										// ������ �ƴ� ���̸�
						enterButton.setEnabled(false);
						exitButton.setEnabled(true);
	
						infoView.setText(msg.substring(6)+"�� �濡 �����ϼ̽��ϴ�.");
					}
					else infoView.setText("���ǿ� �����ϼ̽��ϴ�.");	 
	
					roomNumber=Integer.parseInt(msg.substring(6));     							// �� ��ȣ ����
	
					if(board.isRunning()){                    									// ������ �������� �����̸�
						board.stopGame();                    									// ������ ������Ų��.
					}
				}
	
				else if(msg.startsWith("[FULL]"))     infoView.setText("���� ���� ������ �� �����ϴ�.");  	// ���� �� �����̸�
				else if(msg.startsWith("[PLAYERS]")){
					member m = new member();
					String id = nameBox.getText();
					msgView.append("[���� ���д�]" + m.WinCount(id) +"��" + m.LoseCount(id) + "�� �Դϴ�. \n");
					nameList(msg.substring(9));    			// �濡 �ִ� ����� ���
				}
				else if(msg.startsWith("[ENTER]")){        										// �մ� ����
					member m = new member();
					String id = msg.substring(7);
					pList.add(msg.substring(7));
					
					playersInfo();
	
					msgView.append("["+ msg.substring(7)+"]���� �����Ͽ����ϴ�.\n");
					
					msgView.append("["+ msg.substring(7)+"]���� ���д�" + m.WinCount(id) +"�� " + m.LoseCount(id) + "�� �Դϴ�. \n");
				}
	
				else if(msg.startsWith("[EXIT]")){          									// �մ� ����
					pList.remove(msg.substring(6));            									// ����Ʈ���� ����
	
					playersInfo();                       	 									// �ο����� �ٽ� ����Ͽ� �����ش�.
	
					msgView.append("["+msg.substring(6)+"]���� �ٸ� ������ �����Ͽ����ϴ�.\n");
	
					if(roomNumber!=0)
						endGame("��밡 �������ϴ�.");
				}
	
				else if(msg.startsWith("[DISCONNECT]")){     									// �մ� ���� ����
					pList.remove(msg.substring(12));
	
					playersInfo();
	
					msgView.append("["+msg.substring(12)+"]���� ������ �������ϴ�.\n");
	
					if(roomNumber!=0)
						endGame("��밡 �������ϴ�.");
				}

				else if(msg.startsWith("[COLOR]")){          									// ���� ���� �ο��޴´�.
					String color=msg.substring(7);
	
					board.startGame(color);                      								// ������ �����Ѵ�.
	
					if(color.equals("BLACK")) 	infoView.setText("�浹�� ��ҽ��ϴ�.");
					else 						infoView.setText("�鵹�� ��ҽ��ϴ�.");
	
					stopButton.setEnabled(true);                 								// ��� ��ư Ȱ��ȭ
				}
				else if(msg.startsWith("[DROPGAME]")){
					String id = nameBox.getText();
					member m = new member();
					m.Win(id);
					endGame("��밡 ����Ͽ����ϴ�.");      			// ��밡 ����ϸ� 
				}
				else if(msg.startsWith("[WIN]")){
					String id = nameBox.getText();
					member m = new member();
					m.Win(id);
					endGame("�̰���ϴ�.");   									      				// �̰�����
				}
				else if(msg.startsWith("[LOSE]")){
					String id = nameBox.getText();
					member m = new member();
					m.Lose(id);
					endGame("�����ϴ�.");           			// ������	
				}
				else 									msgView.append(msg+"\n");				// ��ӵ� �޽����� �ƴϸ� �޽��� ������ �����ش�.
			}
		}catch(IOException ie){
			msgView.append(ie+"\n");
		}
		msgView.append("������ ������ϴ�.");
	}

	private void endGame(String msg){                											// ������ �����Ű�� �޼ҵ�
		infoView.setText(msg);

		startButton.setEnabled(false);
		stopButton.setEnabled(false);

		try{ Thread.sleep(2000); }  catch(Exception e){}    									// 2�ʰ� ���
		if(board.isRunning())		board.stopGame();
		if(pList.getItemCount()==2)	startButton.setEnabled(true);
	}

	private void playersInfo(){                 												// �濡 �ִ� �������� ���� �����ش�.
		int count=pList.getItemCount();

		if(roomNumber==0) 				pInfo.setText("����: "+count+"��");
		else 			  				pInfo.setText(roomNumber+" �� ��: "+count+"��");

		if(count==2 && roomNumber!=0)	startButton.setEnabled(true);							// �뱹 ���� ��ư�� Ȱ��ȭ ���¸� �����Ѵ�.
		else 							startButton.setEnabled(false);
	}
					
	private void nameList(String msg){															// �뱹 ���� ��ư�� Ȱ��ȭ ���¸� �����Ѵ�.
		pList.removeAll();

		StringTokenizer st=new StringTokenizer(msg, "\t");

		while(st.hasMoreElements())
			pList.add(st.nextToken());

		playersInfo();
	}

	private void connect(String name){                    													// ����
		try{
			msgView.append("������ ������ ��û�մϴ�.\n");

			socket=new Socket("127.0.0.1", 7777);

			msgView.append("---���� ����--.\n");
			msgView.append("���̵� �Է��ϰ� ���Ƿ� �����ϼ���.\n");

			reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer=new PrintWriter(socket.getOutputStream(), true);

			new Thread(this).start();

			board.setWriter(writer);
		}
		catch(Exception e){
			msgView.append(e+"\n\n���� ����..\n");  
		}
		
		nameBox.setText(name);
	}

	public static void main(String args){
		OmokClient client=new OmokClient("��Ʈ��ũ ���� ����");
		
		client.setSize(780,615);
		client.setVisible(true);
		client.connect(args);
	}
}
