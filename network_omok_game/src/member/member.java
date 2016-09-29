package member;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;

public class member {
	
	private Connection getConnection() throws Exception{
			
			String dbUrl = "jdbc:mysql://localhost:3306/2a";
			String dbId = "jspid";
			String dbPass = "jsppass";
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbUrl,dbId,dbPass);
			return conn;
			
		}

	public int MemberCheck(String id)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		try{
			conn = getConnection();
			
			String sql = "select * from member where uid=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				return 1;
			}
			
			
		}catch(Exception e)
		{
			System.out.println("Exception :" + e.getMessage());
		}
		
		return -1;
	}
	
	public void insertMember(String id, String pwd)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		
		try{
			conn = getConnection();
			
			String sql = "insert into member (uid,pwd,win,lose,score) values(?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pwd);
			pstmt.setInt(3, 0);
			pstmt.setInt(4, 0);
			pstmt.setInt(5, 0);
			pstmt.executeUpdate();
			
			
		}catch(Exception e)
		{
			System.out.println("Exception :" + e.getMessage());
		}
		
	}
	
	public int memberLoginCheck(String id, String pwd)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		try{
			conn = getConnection();
			
			String sql = "select * from member where uid=? and pwd=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pwd);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				return 1;
			}
			
			
		}catch(Exception e)
		{
			System.out.println("Exception :" + e.getMessage());
		}
		
		return -1;
	}
	
	public void Win(String id)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			conn = getConnection();
			
			String sql = "update member set win=win+1, score=score+10 where uid=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			
		}catch(Exception e)
		{
			System.out.println("Exception :" + e.getMessage());
		}
	}
	
	public void Lose(String id)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			conn = getConnection();
			
			String sql = "update member set lose=lose+1 where uid=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			
			sql = "update member set score=score-5 where uid=? and score >= 5";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			
			
		}catch(Exception e)
		{
			System.out.println("Exception :" + e.getMessage());
		}
	}
	
	public int WinCount(String id)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			conn = getConnection();
			
			String sql = "select win from member where uid=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return rs.getInt("win");
			}
			
		}catch(Exception e)
		{
			System.out.println("Exception :" + e.getMessage());
		}
		
		return 0;
	}
	
	public int LoseCount(String id)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			conn = getConnection();
			
			String sql = "select lose from member where uid=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return rs.getInt("lose");
			}
			
		}catch(Exception e)
		{
			System.out.println("Exception :" + e.getMessage());
		}
		
		return 0;
	}
	
	public String MyInformation(String id)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String MyInfo = null;
		
		try{
			conn = getConnection();
			
			String sql = "select * from member where uid=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				MyInfo = "아이디 : " + rs.getString("uid") + "\n" + "전적: " +rs.getInt("win") + "승" + rs.getInt("lose")+ "패" + "\n점수: " +rs.getString("score");
				
			}
			
		}catch(Exception e)
		{
			System.out.println("Exception :" + e.getMessage());
		}
		
		return MyInfo;
	}
	
	public String Ranking()
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String Rank = "";
		int i = 1;
		int temp = 0;
		
		try{
			conn = getConnection();
			
			String sql = "select * from member order by score desc limit 10";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				Rank += i + "등 : " + rs.getString("uid") + "("+rs.getInt("score")+")" + "\n";
				if(temp != rs.getInt("score"))
				{
					i++;
				}
				temp = rs.getInt("score");
				
			}
			
		}catch(Exception e)
		{
			System.out.println("Exception :" + e.getMessage());
		}
		
		return Rank;
	}
}
