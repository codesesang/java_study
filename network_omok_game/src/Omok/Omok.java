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

class Omok extends Canvas{               														// �������� �����ϴ� Ŭ����
	Toolkit tk=Toolkit.getDefaultToolkit();														// �̹��� ����� ���� Toolkit
	public static final int BLACK=1,WHITE=-1;     												// ��� ���� ��Ÿ���� ���
	private int[][]map;                            												// ������ �迭
	private int size;              																// size�� ������ ���� �Ǵ� ���� ����, 15�� ���Ѵ�.
	private int cell;                          													// ������ ũ��(pixel)
	private String info="���� ����";           														// ������ ���� ��Ȳ�� ��Ÿ���� ���ڿ�
	private int color=BLACK;                 													// ������� �� ����
	private boolean enable=false;																// true�̸� ����ڰ� ���� ���� �� �ִ� ����
	private boolean running=false;       														// ������ ���� ���ΰ��� ��Ÿ���� ����
	private PrintWriter writer;            														// ������� �޽����� �����ϱ� ���� ��Ʈ��
	private Graphics gboard,gbuff;    															// ĵ������ ���۸� ���� �׷��Ƚ� ��ü
	private Image buff;                 														// ���� ���۸��� ���� ����
	private Image board;			
	private Image white;
	private Image black;
	
	Omok(int s, int c){           																// �������� ������(s=15, c=30)
		try{
			board= ImageIO.read(new File("image/badook_board.jpg"));  
			white= ImageIO.read(new File("image/WhiteStone.gif"));
			black= ImageIO.read(new File("image/BlackStone.gif"));
		}catch (IOException e) {
			e.printStackTrace();
		} 
		
		this.size=s; this.cell=c;
		map=new int[size+2][];            														// ���� ũ�⸦ ���Ѵ�.

		for(int i=0;i<map.length;i++)
			map[i]=new int[size+2];

		setSize(size*(cell+1)+size, size*(cell+1)+size);    									// �������� ũ�⸦ ����Ѵ�.

		addMouseListener(new MouseAdapter(){													// �������� ���콺 �̺�Ʈ ó��
			public void mousePressed(MouseEvent me){     										// ���콺�� ������
				if(!enable)	return;            													// ����ڰ� ���� �� ���� �����̸� ���� ���´�.
            
				int x=(int)Math.round(me.getX()/(double)cell);									// ���콺�� ��ǥ�� map ��ǥ�� ����Ѵ�.
				int y=(int)Math.round(me.getY()/(double)cell);		

				if(x==0 || y==0 || x==size+1 || y==size+1)	return;								// ���� ���� �� �ִ� ��ǥ�� �ƴϸ� ���� ���´�.
				if(map[x][y]==BLACK || map[x][y]==WHITE)	return;								// �ش� ��ǥ�� �ٸ� ���� ������ ������ ���� ���´�.
     
				writer.println("[STONE]"+x+" "+y);												// ������� ���� ���� ��ǥ�� �����Ѵ�.
				map[x][y]=color;

				if(check(new Point(x, y), color)){												// �̰���� �˻��Ѵ�.
					info="�̰���ϴ�.";
          
					writer.println("[WIN]");
				}
				else info="��밡 �α⸦ ��ٸ��ϴ�.";
				
				repaint();                                   									// �������� �׸���.
				enable=false;																	// ����ڰ� �� �� ���� ���·� �����.
			}
		});
	}

	public boolean isRunning(){           														// ������ ���� ���¸� ��ȯ�Ѵ�.
		return running;
	}

	public void startGame(String col){     														// ������ �����Ѵ�.
		running=true;

	    if(col.equals("BLACK")){              													// ���� ���õǾ��� ��
	    	enable=true; color=BLACK;
	
	    	info="���� ����... �μ���.";
	    }   
	    else{                                													// ���� ���õǾ��� ��
	    	enable=false; color=WHITE;
	
	    	info="���� ����... ��ٸ�����.";
	    }
	}

	public void stopGame(){              														// ������ �����.
		reset();                              													// �������� �ʱ�ȭ�Ѵ�.

		writer.println("[STOPGAME]");        													// ������� �޽����� ������.

		enable=false;
		running=false;
	}

	public void putOpponent(int x, int y){       												// ������� ���� ���´�.
		map[x][y]=-color;

		info="��밡 �ξ����ϴ�. �μ���.";

		repaint();
	}

 	public void setEnable(boolean enable){
 		this.enable=enable;
 	}

 	public void setWriter(PrintWriter writer){
 		this.writer=writer;
 	}

 	public void update(Graphics g){        														// repaint�� ȣ���ϸ� �ڵ����� ȣ��ȴ�.
 		paint(g);                             													// paint�� ȣ���Ѵ�.
 	}

 	public void paint(Graphics g){                												// ȭ���� �׸���.
 		if(gbuff==null){                             											// ���۰� ������ ���۸� �����.
 			buff  = createImage(getWidth(),getHeight());
 			gbuff = buff.getGraphics();
 		}
 		drawBoard(g);    																		// �������� �׸���.
 	}

 	public void reset(){                         												// �������� �ʱ�ȭ��Ų��.
 		for(int i=0;i<map.length;i++)
 			for(int j=0;j<map[i].length;j++)
 				map[i][j]=0;

 		info="���� ����";

 		repaint();
 	}

 	private void drawLine(){                     												// �����ǿ� ���� �ߴ´�.
 		gbuff.drawImage(board, 0, 0, 494, 496, 0, 0, 494, 496, this);
 	}

 	private void drawBlack(int x, int y){         												// �� ���� (x, y)�� �׸���.

 		int xLocation=x*cell-cell/2;
 		int yLocation=y*cell-cell/2;
 		
 		if(10<x)
 			xLocation+=4;
 		if(10<y)
 			yLocation+=4;
 		
 		gbuff.drawImage(black, xLocation, yLocation, xLocation+cell, yLocation+cell, 0, 0, 23, 23, this);
 	}

 	private void drawWhite(int x, int y){         												// �� ���� (x, y)�� �׸���.

 		int xLocation=x*cell-cell/2;
 		int yLocation=y*cell-cell/2;
 		
 		if(10<x)
 			xLocation+=4;
 		if(10<y)
 			yLocation+=4;
 		
 		gbuff.drawImage(white, xLocation, yLocation, xLocation+cell, yLocation+cell, 0, 0, 23, 23, this);
 	}

 	private void drawStones(){                  												// map ������ ������ ��� �׸���.
 		for(int x=1; x<=size;x++)
 			for(int y=1; y<=size;y++){
 				if(map[x][y]==BLACK)
 					drawBlack(x, y);

 				else if(map[x][y]==WHITE)
 					drawWhite(x, y);
 			}
 	}

 	synchronized private void drawBoard(Graphics g){      										// �������� �׸���.
	    //gbuff.clearRect(0, 0, getWidth(), getHeight());											// ���ۿ� ���� �׸��� ������ �̹����� �����ǿ� �׸���.
	
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