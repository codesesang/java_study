package Omok;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

class Omok extends Canvas{               														// 오목판을 구현하는 클래스
	Toolkit tk=Toolkit.getDefaultToolkit();														// 이미지 출력을 위한 Toolkit
	public static final int BLACK=1,WHITE=-1;     												// 흑과 백을 나타내는 상수
	private int[][]map;                            												// 오목판 배열
	private int size;              																// size는 격자의 가로 또는 세로 개수, 15로 정한다.
	private int cell;                          													// 격자의 크기(pixel)
	private String info="게임 중지";           														// 게임의 진행 상황을 나타내는 문자열
	private int color=BLACK;                 													// 사용자의 돌 색깔
	private boolean enable=false;																// true이면 사용자가 돌을 놓을 수 있는 상태
	private boolean running=false;       														// 게임이 진행 중인가를 나타내는 변수
	private PrintWriter writer;            														// 상대편에게 메시지를 전달하기 위한 스트림
	private Graphics gboard,gbuff;    															// 캔버스와 버퍼를 위한 그래픽스 객체
	private Image buff;                 														// 더블 버퍼링을 위한 버퍼
	private Image board;			
	private Image white;
	private Image black;
	
	Omok(int s, int c){           																// 오목판의 생성자(s=15, c=30)
		try{
			board= ImageIO.read(new File("image/badook_board.jpg"));  
			white= ImageIO.read(new File("image/WhiteStone.gif"));
			black= ImageIO.read(new File("image/BlackStone.gif"));
		}catch (IOException e) {
			e.printStackTrace();
		} 
		
		this.size=s; this.cell=c;
		map=new int[size+2][];            														// 맵의 크기를 정한다.

		for(int i=0;i<map.length;i++)
			map[i]=new int[size+2];

		setSize(size*(cell+1)+size, size*(cell+1)+size);    									// 오목판의 크기를 계산한다.

		addMouseListener(new MouseAdapter(){													// 오목판의 마우스 이벤트 처리
			public void mousePressed(MouseEvent me){     										// 마우스를 누르면
				if(!enable)	return;            													// 사용자가 누를 수 없는 상태이면 빠져 나온다.
            
				int x=(int)Math.round(me.getX()/(double)cell);									// 마우스의 좌표를 map 좌표로 계산한다.
				int y=(int)Math.round(me.getY()/(double)cell);		

				if(x==0 || y==0 || x==size+1 || y==size+1)	return;								// 돌이 놓일 수 있는 좌표가 아니면 빠져 나온다.
				if(map[x][y]==BLACK || map[x][y]==WHITE)	return;								// 해당 좌표에 다른 돌이 놓여져 있으면 빠져 나온다.
     
				writer.println("[STONE]"+x+" "+y);												// 상대편에게 놓은 돌의 좌표를 전송한다.
				map[x][y]=color;

				if(check(new Point(x, y), color)){												// 이겼는지 검사한다.
					info="이겼습니다.";
          
					writer.println("[WIN]");
				}
				else info="상대가 두기를 기다립니다.";
				
				repaint();                                   									// 오목판을 그린다.
				enable=false;																	// 사용자가 둘 수 없는 상태로 만든다.
			}
		});
	}

	public boolean isRunning(){           														// 게임의 진행 상태를 반환한다.
		return running;
	}

	public void startGame(String col){     														// 게임을 시작한다.
		running=true;

	    if(col.equals("BLACK")){              													// 흑이 선택되었을 때
	    	enable=true; color=BLACK;
	
	    	info="게임 시작... 두세요.";
	    }   
	    else{                                													// 백이 선택되었을 때
	    	enable=false; color=WHITE;
	
	    	info="게임 시작... 기다리세요.";
	    }
	}

	public void stopGame(){              														// 게임을 멈춘다.
		reset();                              													// 오목판을 초기화한다.

		writer.println("[STOPGAME]");        													// 상대편에게 메시지를 보낸다.

		enable=false;
		running=false;
	}

	public void putOpponent(int x, int y){       												// 상대편의 돌을 놓는다.
		map[x][y]=-color;

		info="상대가 두었습니다. 두세요.";

		repaint();
	}

 	public void setEnable(boolean enable){
 		this.enable=enable;
 	}

 	public void setWriter(PrintWriter writer){
 		this.writer=writer;
 	}

 	public void update(Graphics g){        														// repaint를 호출하면 자동으로 호출된다.
 		paint(g);                             													// paint를 호출한다.
 	}

 	public void paint(Graphics g){                												// 화면을 그린다.
 		if(gbuff==null){                             											// 버퍼가 없으면 버퍼를 만든다.
 			buff  = createImage(getWidth(),getHeight());
 			gbuff = buff.getGraphics();
 		}
 		drawBoard(g);    																		// 오목판을 그린다.
 	}

 	public void reset(){                         												// 오목판을 초기화시킨다.
 		for(int i=0;i<map.length;i++)
 			for(int j=0;j<map[i].length;j++)
 				map[i][j]=0;

 		info="게임 중지";

 		repaint();
 	}

 	private void drawLine(){                     												// 오목판에 선을 긋는다.
 		gbuff.drawImage(board, 0, 0, 494, 496, 0, 0, 494, 496, this);
 	}

 	private void drawBlack(int x, int y){         												// 흑 돌을 (x, y)에 그린다.

 		int xLocation=x*cell-cell/2;
 		int yLocation=y*cell-cell/2;
 		
 		if(10<x)
 			xLocation+=4;
 		if(10<y)
 			yLocation+=4;
 		
 		gbuff.drawImage(black, xLocation, yLocation, xLocation+cell, yLocation+cell, 0, 0, 23, 23, this);
 	}

 	private void drawWhite(int x, int y){         												// 백 돌을 (x, y)에 그린다.

 		int xLocation=x*cell-cell/2;
 		int yLocation=y*cell-cell/2;
 		
 		if(10<x)
 			xLocation+=4;
 		if(10<y)
 			yLocation+=4;
 		
 		gbuff.drawImage(white, xLocation, yLocation, xLocation+cell, yLocation+cell, 0, 0, 23, 23, this);
 	}

 	private void drawStones(){                  												// map 놓여진 돌들을 모두 그린다.
 		for(int x=1; x<=size;x++)
 			for(int y=1; y<=size;y++){
 				if(map[x][y]==BLACK)
 					drawBlack(x, y);

 				else if(map[x][y]==WHITE)
 					drawWhite(x, y);
 			}
 	}

 	synchronized private void drawBoard(Graphics g){      										// 오목판을 그린다.
	    //gbuff.clearRect(0, 0, getWidth(), getHeight());											// 버퍼에 먼저 그리고 버퍼의 이미지를 오목판에 그린다.
	
	    drawLine();

	    drawStones();
	    
	    gbuff.setColor(Color.red);
	    gbuff.drawString(info, 20, 15);
	
	    g.drawImage(buff, 0, 0, this);
 	}

 	private boolean check(Point p, int col){
	    if(count(p, 1, 0, col)  + count(p, -1, 0, col)==4)	return true;
	    if(count(p, 0, 1, col)  + count(p, 0, -1, col)==4)	return true;
	    if(count(p, -1, -1, col)+ count(p, 1, 1, col) ==4) 	return true;
	    if(count(p, 1, -1, col) + count(p, -1, 1, col)==4) 	return true;
	
	    return false;
 	}

 	private int count(Point p, int dx, int dy, int col){
	    int i=0;
	
	    for(; map[p.x+(i+1)*dx][p.y+(i+1)*dy]==col ;i++);
	
	    return i;
 	}
}   