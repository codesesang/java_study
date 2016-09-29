package Omok;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class OmokServer{
	private ServerSocket server;
	private BManager bMan;  			 						// �޽��� �����
	private Random rnd;      										// ��� ���� ���Ƿ� ���ϱ� ���� ����

	public OmokServer(){
		bMan = new BManager(); 
		rnd= new Random();  
	}

	void startServer(){                         								// ������ �����Ѵ�.
		try{
			server=new ServerSocket(7777);
			System.out.println("���������� �����Ǿ����ϴ�.");

			while(true){
		        Socket socket=server.accept();									// Ŭ���̾�Ʈ�� ����� �����带 ��´�. 
		        Omok_Thread ot=new Omok_Thread(socket);	 						// �����带 ����� �����Ų��.
		        
		        ot.start();    
		        bMan.add(ot);													// bMan�� �����带 �߰��Ѵ�.
		        
		        System.out.println("������ ��: "+bMan.size());
			}
	   	}catch(Exception e){
	   		System.out.println("Exception : "+e.getMessage());
	   	}
    }

    public static void main(String[] args){
    	OmokServer server=new OmokServer();

    	server.startServer();
    }

    class Omok_Thread extends Thread{											// Ŭ���̾�Ʈ�� ����ϴ� ������ Ŭ����
	    private int roomNumber=-1;        										// �� ��ȣ
	    private String userName=null;       									// ����� �̸�
	    private Socket socket;              									// ����
	    private boolean ready=false;											// ���� �غ� ����, true�̸� ���� �غ� �Ǿ����� �ǹ�
	    private BufferedReader reader;     										// �Է� ��Ʈ��
	    private PrintWriter writer;           									// ��� ��Ʈ��

	    Omok_Thread(Socket socket){     										// ������
	    	this.socket=socket;
	    }
	
	    Socket getSocket(){               										// ������ ��ȯ�Ѵ�.
	    	return socket;
	    }
	
	    int getRoomNumber(){             										// �� ��ȣ�� ��ȯ�Ѵ�.
	    	return roomNumber;
	    }
	
	    String getUserName(){             										// ����� �̸��� ��ȯ�Ѵ�.
	    	return userName;
	    }
	
	    boolean isReady(){                 										// �غ� ���¸� ��ȯ�Ѵ�.
	    	return ready;
	    }

	    public void run(){
	    	try{
	    		reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    		writer=new PrintWriter(socket.getOutputStream(), true);
	
	    		String msg;                     								// Ŭ���̾�Ʈ�� �޽���
		        while((msg=reader.readLine())!=null){
		        	
		        	System.out.println(msg);
		        	
		        	
		        	if(msg.startsWith("[NAME]")){								// msg�� "[NAME]"���� ���۵Ǵ� �޽����̸�
		        		userName=msg.substring(6);          					// userName�� ���Ѵ�.
		            }
		   
		        	else if(msg.startsWith("[ROOM]")){							// msg�� "[ROOM]"���� ���۵Ǹ� �� ��ȣ�� ���Ѵ�.
		        		int roomNum=Integer.parseInt(msg.substring(6));
		
		        		if( !bMan.isFull(roomNum)){             				// ���� �� ���°� �ƴϸ�   
		        			if(roomNumber!=-1)									// ���� ���� �ٸ� ��뿡�� ������� ������ �˸���.
		        				bMan.sendToOthers(this, "[EXIT]"+userName);
		              
		        			roomNumber=roomNum;									// ������� �� �� ��ȣ�� �����Ѵ�.
		        			writer.println(msg);								// ����ڿ��� �޽����� �״�� �����Ͽ� ������ �� ������ �˸���.
		
		        			writer.println(bMan.getNamesInRoom(roomNumber));	// ����ڿ��� �� �濡 �ִ� ����� �̸� ����Ʈ�� �����Ѵ�.    
		        			bMan.sendToOthers(this, "[ENTER]"+userName);		// �� �濡 �ִ� �ٸ� ����ڿ��� ������� ������ �˸���.
		        		}
		        		else 	writer.println("[FULL]");        				// ����ڿ� ���� á���� �˸���.
		        	}
		
		        	else if(roomNumber>=1 && msg.startsWith("[STONE]"))			// "[STONE]" �޽����� ������� �����Ѵ�.
		        		bMan.sendToOthers(this, msg);
		
		        	else if(msg.startsWith("[MSG]"))							// ��ȭ �޽����� �濡 �����Ѵ�.
		        		bMan.sendToRoom(roomNumber, "["+userName+"]: "+msg.substring(5));
		
		        	else if(msg.startsWith("[START]")){							// "[START]" �޽����̸�
		        		ready=true;   											// ������ ������ �غ� �Ǿ���.
		
			            if(bMan.isReady(roomNumber)){							// �ٸ� ����ڵ� ������ ������ �غ� �Ǿ�����
			            	int a=rnd.nextInt(2);								// ��� ���� ���ϰ� ����ڿ� ������� �����Ѵ�.
			
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
			        else if(msg.startsWith("[STOPGAME]"))						// ����ڰ� ������ �����ϴ� �޽����� ������
			        	ready=false;
			  
			        else if(msg.startsWith("[DROPGAME]")){						// ����ڰ� ������ ����ϴ� �޽����� ������
			            ready=false;
			            bMan.sendToOthers(this, "[DROPGAME]");					// ������� ������� ����� �˸���.
			        }
	
			        else if(msg.startsWith("[WIN]")){							// ����ڰ� �̰�ٴ� �޽����� ������
			            ready=false;
			            writer.println("[WIN]");								// ����ڿ��� �޽����� ������.
			            bMan.sendToOthers(this, "[LOSE]");						// ������� ������ �˸���.
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
		
			    	System.out.println(userName+"���� ������ �������ϴ�.");
			    	System.out.println("������ ��: "+bMan.size());
		          
			    	bMan.sendToRoom(roomNumber,"[DISCONNECT]"+userName);			// ����ڰ� ������ �������� ���� �濡 �˸���.
		    	}catch(Exception e){}
		    }
	    }
    }

    class BManager extends Vector{       										// �޽����� �����ϴ� Ŭ����
		BManager(){}
	
	    void add(Omok_Thread ot){           									// �����带 �߰��Ѵ�.
	    	super.add(ot);
	    }
	
	    void remove(Omok_Thread ot){        									// �����带 �����Ѵ�.
	    	super.remove(ot);
	    }
	
	    Omok_Thread getOT(int i){           	 								// i��° �����带 ��ȯ�Ѵ�.
	    	return (Omok_Thread)elementAt(i);
	    }
	
	    Socket getSocket(int i){              									// i��° �������� ������ ��ȯ�Ѵ�.
	    	return getOT(i).getSocket();
	    }
	
	    void sendTo(int i, String msg){											// i��° ������� ����� Ŭ���̾�Ʈ���� �޽����� �����Ѵ�.
	    	try{
	    		PrintWriter pw= new PrintWriter(getSocket(i).getOutputStream(), true);
	    		pw.println(msg);
	    	}catch(Exception e){}  
	    }
	
	    int getRoomNumber(int i){            									// i��° �������� �� ��ȣ�� ��ȯ�Ѵ�.
	    	return getOT(i).getRoomNumber();
	    }
	
	    synchronized boolean isFull(int roomNum){    							// ���� á���� �˾ƺ���.
	    	if(roomNum==0)return false;                 						// ������ ���� �ʴ´�.
	
	    	int count=0;														// �ٸ� ���� 2�� �̻� ������ �� ����.
	
	    	for(int i=0;i<size();i++)
	        if(roomNum==getRoomNumber(i))count++;
	
	    	if(count>=2)return true;
	
	    	return false;
	    }
	
	    void sendToRoom(int roomNum, String msg){								// roomNum �濡 msg�� �����Ѵ�.
	    	for(int i=0;i<size();i++)
	    		if(roomNum==getRoomNumber(i))
	    			sendTo(i, msg);
	    }
	
	    void sendToOthers(Omok_Thread ot, String msg){							// ot�� ���� �濡 �ִ� �ٸ� ����ڿ��� msg�� �����Ѵ�.
	    	for(int i=0;i<size();i++)
	    		if(getRoomNumber(i)==ot.getRoomNumber() && getOT(i)!=ot)
	    			sendTo(i, msg);
	    }
	    																		
	    synchronized boolean isReady(int roomNum){								// �� ���� ����� ��� �غ�� �����̸� true�� ��ȯ�Ѵ�.
	    	int count=0;
	
	    	for(int i=0;i<size();i++)
	    		if(roomNum==getRoomNumber(i) && getOT(i).isReady())
	    			count++;
	
	    	if(count==2)return true;
	      
	    	return false;
	    }
	
	    String getNamesInRoom(int roomNum){										// roomNum�濡 �ִ� ����ڵ��� �̸��� ��ȯ�Ѵ�.
	    	StringBuffer sb=new StringBuffer("[PLAYERS]");
	
	    	for(int i=0;i<size();i++)
	    		if(roomNum==getRoomNumber(i))
	    			sb.append(getOT(i).getUserName()+"\t");
	
	    	return sb.toString();
	    }
	}
}