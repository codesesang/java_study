package Omok;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class OmokServer{
	private ServerSocket server;
	private BManager bMan;  			 						// 메시지 방송자
	private Random rnd;      										// 흑과 백을 임의로 정하기 위한 변수

	public OmokServer(){
		bMan = new BManager(); 
		rnd= new Random();  
	}

	void startServer(){                         								// 서버를 실행한다.
		try{
			server=new ServerSocket(7777);
			System.out.println("서버소켓이 생성되었습니다.");

			while(true){
		        Socket socket=server.accept();									// 클라이언트와 연결된 스레드를 얻는다. 
		        Omok_Thread ot=new Omok_Thread(socket);	 						// 스레드를 만들고 실행시킨다.
		        
		        ot.start();    
		        bMan.add(ot);													// bMan에 스레드를 추가한다.
		        
		        System.out.println("접속자 수: "+bMan.size());
			}
	   	}catch(Exception e){
	   		System.out.println("Exception : "+e.getMessage());
	   	}
    }

    public static void main(String[] args){
    	OmokServer server=new OmokServer();

    	server.startServer();
    }

    class Omok_Thread extends Thread{											// 클라이언트와 통신하는 스레드 클래스
	    private int roomNumber=-1;        										// 방 번호
	    private String userName=null;       									// 사용자 이름
	    private Socket socket;              									// 소켓
	    private boolean ready=false;											// 게임 준비 여부, true이면 게임 준비가 되었음을 의미
	    private BufferedReader reader;     										// 입력 스트림
	    private PrintWriter writer;           									// 출력 스트림

	    Omok_Thread(Socket socket){     										// 생성자
	    	this.socket=socket;
	    }
	
	    Socket getSocket(){               										// 소켓을 반환한다.
	    	return socket;
	    }
	
	    int getRoomNumber(){             										// 방 번호를 반환한다.
	    	return roomNumber;
	    }
	
	    String getUserName(){             										// 사용자 이름을 반환한다.
	    	return userName;
	    }
	
	    boolean isReady(){                 										// 준비 상태를 반환한다.
	    	return ready;
	    }

	    public void run(){
	    	try{
	    		reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    		writer=new PrintWriter(socket.getOutputStream(), true);
	
	    		String msg;                     								// 클라이언트의 메시지
		        while((msg=reader.readLine())!=null){
		        	
		        	System.out.println(msg);
		        	
		        	
		        	if(msg.startsWith("[NAME]")){								// msg가 "[NAME]"으로 시작되는 메시지이면
		        		userName=msg.substring(6);          					// userName을 정한다.
		            }
		   
		        	else if(msg.startsWith("[ROOM]")){							// msg가 "[ROOM]"으로 시작되면 방 번호를 정한다.
		        		int roomNum=Integer.parseInt(msg.substring(6));
		
		        		if( !bMan.isFull(roomNum)){             				// 방이 찬 상태가 아니면   
		        			if(roomNumber!=-1)									// 현재 방의 다른 사용에게 사용자의 퇴장을 알린다.
		        				bMan.sendToOthers(this, "[EXIT]"+userName);
		              
		        			roomNumber=roomNum;									// 사용자의 새 방 번호를 지정한다.
		        			writer.println(msg);								// 사용자에게 메시지를 그대로 전송하여 입장할 수 있음을 알린다.
		
		        			writer.println(bMan.getNamesInRoom(roomNumber));	// 사용자에게 새 방에 있는 사용자 이름 리스트를 전송한다.    
		        			bMan.sendToOthers(this, "[ENTER]"+userName);		// 새 방에 있는 다른 사용자에게 사용자의 입장을 알린다.
		        		}
		        		else 	writer.println("[FULL]");        				// 사용자에 방이 찼음을 알린다.
		        	}
		
		        	else if(roomNumber>=1 && msg.startsWith("[STONE]"))			// "[STONE]" 메시지는 상대편에게 전송한다.
		        		bMan.sendToOthers(this, msg);
		
		        	else if(msg.startsWith("[MSG]"))							// 대화 메시지를 방에 전송한다.
		        		bMan.sendToRoom(roomNumber, "["+userName+"]: "+msg.substring(5));
		
		        	else if(msg.startsWith("[START]")){							// "[START]" 메시지이면
		        		ready=true;   											// 게임을 시작할 준비가 되었다.
		
			            if(bMan.isReady(roomNumber)){							// 다른 사용자도 게임을 시작한 준비가 되었으면
			            	int a=rnd.nextInt(2);								// 흑과 백을 정하고 사용자와 상대편에게 전송한다.
			
				            if(a==0){
				                writer.println("[COLOR]BLACK");
				                bMan.sendToOthers(this,"[COLOR]WHITE");
				            }
				            else{
				                writer.println("[COLOR]WHITE");
				                bMan.sendToOthers(this,"[COLOR]BLACK");
				            }
			            }
			        }												
			        else if(msg.startsWith("[STOPGAME]"))						// 사용자가 게임을 중지하는 메시지를 보내면
			        	ready=false;
			  
			        else if(msg.startsWith("[DROPGAME]")){						// 사용자가 게임을 기권하는 메시지를 보내면
			            ready=false;
			            bMan.sendToOthers(this, "[DROPGAME]");					// 상대편에게 사용자의 기권을 알린다.
			        }
	
			        else if(msg.startsWith("[WIN]")){							// 사용자가 이겼다는 메시지를 보내면
			            ready=false;
			            writer.println("[WIN]");								// 사용자에게 메시지를 보낸다.
			            bMan.sendToOthers(this, "[LOSE]");						// 상대편에는 졌음을 알린다.
			        }  
		        }
		    }catch(Exception e){
		    	System.out.println("Exception :"+e.getMessage());
		    }finally{
		    	try{
			    	bMan.remove(this);
		
			    	if(reader!=null) reader.close();
			    	if(writer!=null) writer.close();
			    	if(socket!=null) socket.close();
		
			    	reader=null; 
			    	writer=null; 
			    	socket=null;
		
			    	System.out.println(userName+"님이 접속을 끊었습니다.");
			    	System.out.println("접속자 수: "+bMan.size());
		          
			    	bMan.sendToRoom(roomNumber,"[DISCONNECT]"+userName);			// 사용자가 접속을 끊었음을 같은 방에 알린다.
		    	}catch(Exception e){}
		    }
	    }
    }

    class BManager extends Vector{       										// 메시지를 전달하는 클래스
		BManager(){}
	
	    void add(Omok_Thread ot){           									// 스레드를 추가한다.
	    	super.add(ot);
	    }
	
	    void remove(Omok_Thread ot){        									// 스레드를 제거한다.
	    	super.remove(ot);
	    }
	
	    Omok_Thread getOT(int i){           	 								// i번째 스레드를 반환한다.
	    	return (Omok_Thread)elementAt(i);
	    }
	
	    Socket getSocket(int i){              									// i번째 스레드의 소켓을 반환한다.
	    	return getOT(i).getSocket();
	    }
	
	    void sendTo(int i, String msg){											// i번째 스레드와 연결된 클라이언트에게 메시지를 전송한다.
	    	try{
	    		PrintWriter pw= new PrintWriter(getSocket(i).getOutputStream(), true);
	    		pw.println(msg);
	    	}catch(Exception e){}  
	    }
	
	    int getRoomNumber(int i){            									// i번째 스레드의 방 번호를 반환한다.
	    	return getOT(i).getRoomNumber();
	    }
	
	    synchronized boolean isFull(int roomNum){    							// 방이 찼는지 알아본다.
	    	if(roomNum==0)return false;                 						// 대기실은 차지 않는다.
	
	    	int count=0;														// 다른 방은 2명 이상 입장할 수 없다.
	
	    	for(int i=0;i<size();i++)
	        if(roomNum==getRoomNumber(i))count++;
	
	    	if(count>=2)return true;
	
	    	return false;
	    }
	
	    void sendToRoom(int roomNum, String msg){								// roomNum 방에 msg를 전송한다.
	    	for(int i=0;i<size();i++)
	    		if(roomNum==getRoomNumber(i))
	    			sendTo(i, msg);
	    }
	
	    void sendToOthers(Omok_Thread ot, String msg){							// ot와 같은 방에 있는 다른 사용자에게 msg를 전달한다.
	    	for(int i=0;i<size();i++)
	    		if(getRoomNumber(i)==ot.getRoomNumber() && getOT(i)!=ot)
	    			sendTo(i, msg);
	    }
	    																		
	    synchronized boolean isReady(int roomNum){								// 두 명의 사용자 모두 준비된 상태이면 true를 반환한다.
	    	int count=0;
	
	    	for(int i=0;i<size();i++)
	    		if(roomNum==getRoomNumber(i) && getOT(i).isReady())
	    			count++;
	
	    	if(count==2)return true;
	      
	    	return false;
	    }
	
	    String getNamesInRoom(int roomNum){										// roomNum방에 있는 사용자들의 이름을 반환한다.
	    	StringBuffer sb=new StringBuffer("[PLAYERS]");
	
	    	for(int i=0;i<size();i++)
	    		if(roomNum==getRoomNumber(i))
	    			sb.append(getOT(i).getUserName()+"\t");
	
	    	return sb.toString();
	    }
	}
}