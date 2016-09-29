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

	private TextArea msgView=new TextArea("", 1,1,1);   										// 메시지를 보여주는 영역
	private TextField sendBox=new TextField("");         										// 보낼 메시지를 적는 상자
	public TextField nameBox=new TextField();          										// 사용자 이름 상자
	private TextField roomBox=new TextField("0");        										// 방 번호 상자
	
	private Label pInfo=new Label("대기실:  명");													// 방에 접속한 인원의 수를 보여주는 레이블
	private Label infoView=new Label("", 1);													// 각종 정보를 보여주는 레이블
	
	private java.awt.List pList=new java.awt.List();  											// 사용자 명단을 보여주는 리스트
	
	private JButton startButton = new JButton(new ImageIcon("image/start.png"));    										// 대국 시작 버튼
	private JButton stopButton  = new JButton(new ImageIcon("image/lose.png"));         										// 기권 버튼
	private JButton enterButton = new JButton(new ImageIcon("image/in.png"));    				// 입장하기 버튼
	private JButton exitButton  = new JButton(new ImageIcon("image/out.png"));      										// 대기실로 버튼
	private JButton rankButton  = new JButton("랭 킹");
	private JButton myRankButton= new JButton("내 정보");
	
	
	private Omok board=new Omok(19,24);      													// 오목판 객체

	private BufferedReader reader;                         										// 입력 스트림
	private PrintWriter writer;                               									// 출력 스트림
	private Socket socket;                                 										// 소켓
	private int roomNumber=-1;                           										// 방 번호
	private String userName=null;                          										// 사용자 이름
	
	public OmokClient(String title){                        									// 생성자
		super(title);
		
		nameBox.setEditable(false);																// 아이디 수정 불가하게.
		
		getContentPane().setLayout(null);                                										// 레이아웃을 사용하지 않는다.
   
	    msgView.setEditable(false);																// 각종 컴포넌트를 생성하고 배치한다.
	
	    infoView.setBounds(10,30,494,30);

	    infoView.setBackground(new Color(200,200,255));

	    board.setLocation(10,70);																// 오모판 위치를 지정한다.

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
	
	    sendBox.addActionListener(this);														// 이벤트 리스너를 등록한다.
	    enterButton.addActionListener(this);
	    exitButton.addActionListener(this);
	    startButton.addActionListener(this);
	    stopButton.addActionListener(this);

	    addWindowListener(new WindowAdapter(){													// 윈도우 닫기 처리
	    	public void windowClosing(WindowEvent we){
	    		System.exit(0);
	        }
	    });
	}
	
	public void actionPerformed(ActionEvent ae){												// 컴포넌트들의 액션 이벤트 처리
		if(ae.getSource()==sendBox){             												// 메시지 입력 상자이면

			String msg=sendBox.getText();

			if(msg.length()==0)		return;
			if(msg.length()>=30)	msg=msg.substring(0,30);

			try{  
				writer.println("[MSG]"+msg);

				sendBox.setText("");
			}catch(Exception ie){}
		}
		else if(ae.getSource()==enterButton){         											// 입장하기 버튼이면
			try{
		    	if(Integer.parseInt(roomBox.getText())<1){
		        	infoView.setText("방번호가 잘못되었습니다. 1이상");
		
		        	return;
		        }

		    	writer.println("[ROOM]"+Integer.parseInt(roomBox.getText()));

		    	msgView.setText("");
			}catch(Exception ie){
				infoView.setText("입력하신 사항에 오류가 았습니다.");
			}
		}
		else if(ae.getSource()==exitButton){           											// 대기실로 버튼이면
			try{
				goToWaitRoom();

				startButton.setEnabled(false);
				stopButton.setEnabled(false);
			}catch(Exception e){}
		}

		else if(ae.getSource()==startButton){          											// 대국 시작 버튼이면
			try{
				writer.println("[START]");

				infoView.setText("상대의 결정을 기다립니다.");

				startButton.setEnabled(false);
			}catch(Exception e){}
		}

		else if(ae.getSource()==stopButton){          											// 기권 버튼이면
			try{
				writer.println("[DROPGAME]");
				String id = nameBox.getText();
				member m = new member();
				m.Lose(id);
				endGame("기권하였습니다.");
			}catch(Exception e){}
		}
	}

	void goToWaitRoom(){                   														// 대기실로 버튼을 누르면 호출된다.
		if(userName==null){
			String name=nameBox.getText().trim();

			userName=name;

			writer.println("[NAME]"+userName);    

			nameBox.setText(userName);
			nameBox.setEditable(false);
		}  

		msgView.setText("");

		writer.println("[ROOM]0");

		infoView.setText("대기실에 입장하셨습니다.");

		roomBox.setText("0");

		enterButton.setEnabled(true);
		exitButton.setEnabled(false);
	}

	public void run(){
		String msg;                            		 											// 서버로부터의 메시지
		try{
			while((msg=reader.readLine())!=null){
				if(msg.startsWith("[STONE]")){     												// 상대편이 놓은 돌의 좌표
					String temp=msg.substring(7);
	
					int x=Integer.parseInt(temp.substring(0,temp.indexOf(" ")));
					int y=Integer.parseInt(temp.substring(temp.indexOf(" ")+1));
	
					board.putOpponent(x, y);     												// 상대편의 돌을 그린다.
					board.setEnable(true);        												// 사용자가 돌을 놓을 수 있도록 한다.
				}
	
				else if(msg.startsWith("[ROOM]")){    											// 방에 입장
					if(!msg.equals("[ROOM]0")){          										// 대기실이 아닌 방이면
						enterButton.setEnabled(false);
						exitButton.setEnabled(true);
	
						infoView.setText(msg.substring(6)+"번 방에 입장하셨습니다.");
					}
					else infoView.setText("대기실에 입장하셨습니다.");	 
	
					roomNumber=Integer.parseInt(msg.substring(6));     							// 방 번호 지정
	
					if(board.isRunning()){                    									// 게임이 진행중인 상태이면
						board.stopGame();                    									// 게임을 중지시킨다.
					}
				}
	
				else if(msg.startsWith("[FULL]"))     infoView.setText("방이 차서 입장할 수 없습니다.");  	// 방이 찬 상태이면
				else if(msg.startsWith("[PLAYERS]")){
					member m = new member();
					String id = nameBox.getText();
					msgView.append("[나의 승패는]" + m.WinCount(id) +"승" + m.LoseCount(id) + "패 입니다. \n");
					nameList(msg.substring(9));    			// 방에 있는 사용자 명단
				}
				else if(msg.startsWith("[ENTER]")){        										// 손님 입장
					member m = new member();
					String id = msg.substring(7);
					pList.add(msg.substring(7));
					
					playersInfo();
	
					msgView.append("["+ msg.substring(7)+"]님이 입장하였습니다.\n");
					
					msgView.append("["+ msg.substring(7)+"]님의 승패는" + m.WinCount(id) +"승 " + m.LoseCount(id) + "패 입니다. \n");
				}
	
				else if(msg.startsWith("[EXIT]")){          									// 손님 퇴장
					pList.remove(msg.substring(6));            									// 리스트에서 제거
	
					playersInfo();                       	 									// 인원수를 다시 계산하여 보여준다.
	
					msgView.append("["+msg.substring(6)+"]님이 다른 방으로 입장하였습니다.\n");
	
					if(roomNumber!=0)
						endGame("상대가 나갔습니다.");
				}
	
				else if(msg.startsWith("[DISCONNECT]")){     									// 손님 접속 종료
					pList.remove(msg.substring(12));
	
					playersInfo();
	
					msgView.append("["+msg.substring(12)+"]님이 접속을 끊었습니다.\n");
	
					if(roomNumber!=0)
						endGame("상대가 나갔습니다.");
				}

				else if(msg.startsWith("[COLOR]")){          									// 돌의 색을 부여받는다.
					String color=msg.substring(7);
	
					board.startGame(color);                      								// 게임을 시작한다.
	
					if(color.equals("BLACK")) 	infoView.setText("흑돌을 잡았습니다.");
					else 						infoView.setText("백돌을 잡았습니다.");
	
					stopButton.setEnabled(true);                 								// 기권 버튼 활성화
				}
				else if(msg.startsWith("[DROPGAME]")){
					String id = nameBox.getText();
					member m = new member();
					m.Win(id);
					endGame("상대가 기권하였습니다.");      			// 상대가 기권하면 
				}
				else if(msg.startsWith("[WIN]")){
					String id = nameBox.getText();
					member m = new member();
					m.Win(id);
					endGame("이겼습니다.");   									      				// 이겼으면
				}
				else if(msg.startsWith("[LOSE]")){
					String id = nameBox.getText();
					member m = new member();
					m.Lose(id);
					endGame("졌습니다.");           			// 졌으면	
				}
				else 									msgView.append(msg+"\n");				// 약속된 메시지가 아니면 메시지 영역에 보여준다.
			}
		}catch(IOException ie){
			msgView.append(ie+"\n");
		}
		msgView.append("접속이 끊겼습니다.");
	}

	private void endGame(String msg){                											// 게임의 종료시키는 메소드
		infoView.setText(msg);

		startButton.setEnabled(false);
		stopButton.setEnabled(false);

		try{ Thread.sleep(2000); }  catch(Exception e){}    									// 2초간 대기
		if(board.isRunning())		board.stopGame();
		if(pList.getItemCount()==2)	startButton.setEnabled(true);
	}

	private void playersInfo(){                 												// 방에 있는 접속자의 수를 보여준다.
		int count=pList.getItemCount();

		if(roomNumber==0) 				pInfo.setText("대기실: "+count+"명");
		else 			  				pInfo.setText(roomNumber+" 번 방: "+count+"명");

		if(count==2 && roomNumber!=0)	startButton.setEnabled(true);							// 대국 시작 버튼의 활성화 상태를 점검한다.
		else 							startButton.setEnabled(false);
	}
					
	private void nameList(String msg){															// 대국 시작 버튼의 활성화 상태를 점검한다.
		pList.removeAll();

		StringTokenizer st=new StringTokenizer(msg, "\t");

		while(st.hasMoreElements())
			pList.add(st.nextToken());

		playersInfo();
	}

	private void connect(String name){                    													// 연결
		try{
			msgView.append("서버에 연결을 요청합니다.\n");

			socket=new Socket("127.0.0.1", 7777);

			msgView.append("---연결 성공--.\n");
			msgView.append("아이디를 입력하고 대기실로 입장하세요.\n");

			reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer=new PrintWriter(socket.getOutputStream(), true);

			new Thread(this).start();

			board.setWriter(writer);
		}
		catch(Exception e){
			msgView.append(e+"\n\n연결 실패..\n");  
		}
		
		nameBox.setText(name);
	}

	public static void main(String args){
		OmokClient client=new OmokClient("네트워크 오목 게임");
		
		client.setSize(780,615);
		client.setVisible(true);
		client.connect(args);
	}
}
