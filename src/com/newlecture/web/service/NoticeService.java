package com.newlecture.web.service;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.newlecture.web.entity.Notice;
import com.newlecture.web.entity.NoticeView;

public class NoticeService {
	
	public int removeNoticeAll(int[] ids){ //
		return 0;
	}
	
	public int pubNoticeAll(int[] oids, int[] cids){
		
		List<String> oidsList = new ArrayList<>();
		for (int i = 0; i < oids.length; i++) {
			oidsList.add(String.valueOf(oids[i]));
		}

		List<String> cidsList = new ArrayList<>();
		for (int i = 0; i < cids.length; i++) {
			oidsList.add(String.valueOf(cids[i]));
		}
		
		return pubNoticeAll(oidsList,cidsList);
 	}
	
	public int pubNoticeAll(List<String> oids, List<String> cids){
		
		String oidsCSV = String.join(",", oids);
		String cidsCSV = String.join(",", cids);
		
		return pubNoticeAll(oidsCSV,cidsCSV);
	}
	// "20,30,43,56"
	public int pubNoticeAll(String oidsCSV, String cidsCSV){
		
		int result=0;
		String sqlOpen = String.format("UPDATE NOTICE SET PUB=1 WHERE ID IN (%s)",oidsCSV);
		String sqlClose = String.format("UPDATE NOTICE SET PUB=0 WHERE ID IN (%s)",cidsCSV);
		
		String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "system", "root1234");
			Statement stOpen = con.createStatement();
			result += stOpen.executeUpdate(sqlOpen);

			Statement stClose = con.createStatement();
			result += stClose.executeUpdate(sqlClose);
			
				stOpen.close();
				stClose.close();
				con.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int insertNotice(Notice notice){			// 실제 DB데이터에 입력함
		int result =0;
		
		String sql = "INSERT INTO NOTICE(TITLE, CONTENT, WRITER_ID, PUB, FILES) VALUES(?,?,?,?,?)";
		
		String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "system", "root1234");
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1,notice.getTitle()); 
			st.setString(2,notice.getContent());
			st.setString(3,notice.getWriterId());
			st.setBoolean(4,notice.getPub());
			st.setString(5,notice.getFiles());
			
			result = st.executeUpdate();
			
				st.close();
				con.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int deleteNotice(int id){
		return 0;
	}
	
	public int updateNotice(Notice notice){
		return 0;
	}
	
	List<Notice> getNoticeNewestList(){
		return null;
	}
	
	public List<NoticeView> getNoticeList() {
		return getNoticeList("title", "", 1);
	}

	public List<NoticeView> getNoticeList(int page) {
		return getNoticeList("title", "", page);
	}

	public List<NoticeView> getNoticeList(String field, String query, int page) {
		List<NoticeView> list = new ArrayList<>(); // list라는 배열객체를 만듦

		String sql = "SELECT * FROM (SELECT ROWNUM num, N.* FROM(select * from NOTICE_VIEW where " + field + " like ? ORDER BY regdate desc) N) where num BETWEEN ? AND ?";
		
		//1,11,21,31 -> 등차수열 an = 1+(page-1)*10
		// 10,20,30,40 -> page*10
		String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "system", "root1234");
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, "%"+query+"%");
			st.setInt(2, 1+(page-1)*10);
			st.setInt(3, page*10);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()){		// 돌면서 결과집합을 각 객체에 담음
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String writerId = rs.getString("writer_id");
				Date regdate = rs.getDate("regdate");
				String hit = rs.getString("hit");
				String files = rs.getString("files");
				//String content = rs.getString("content"); (오라클에서 CLOB이여서 뺐음)
				int cmtCount = rs.getInt("CMT_COUNT");
				boolean pub = rs.getBoolean("pub");
				
				NoticeView notice = new NoticeView(id, title, writerId, regdate, hit, files, pub /*content*/, cmtCount);		//notice에 담음
				list.add(notice);		//모든 결과값 담은 notice를 list라는 배열객체에 담음
		} 
				rs.close();
				st.close();
				con.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<NoticeView> getNoticePubList(String field, String query, int page) {
		List<NoticeView> list = new ArrayList<>(); // list라는 배열객체를 만듦

		String sql = "SELECT * FROM (SELECT ROWNUM num, N.* FROM(select * from NOTICE_VIEW where pub=1 and " + field + " like ? ORDER BY regdate desc) N) where num BETWEEN ? AND ?";
		
		//1,11,21,31 -> 등차수열 an = 1+(page-1)*10
		// 10,20,30,40 -> page*10
		String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "system", "root1234");
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, "%"+query+"%");
			st.setInt(2, 1+(page-1)*10);
			st.setInt(3, page*10);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()){		// 돌면서 결과집합을 각 객체에 담음
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String writerId = rs.getString("writer_id");
				Date regdate = rs.getDate("regdate");
				String hit = rs.getString("hit");
				String files = rs.getString("files");
				//String content = rs.getString("content"); (오라클에서 CLOB이여서 뺐음)
				int cmtCount = rs.getInt("CMT_COUNT");
				boolean pub = rs.getBoolean("pub");
				
				NoticeView notice = new NoticeView(id, title, writerId, regdate, hit, files, pub /*content*/, cmtCount);		//notice에 담음
				list.add(notice);		//모든 결과값 담은 notice를 list라는 배열객체에 담음
		} 
				rs.close();
				st.close();
				con.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	
	
	public int getNoticeCount() {

		return getNoticeCount("title","");
	}

	public int getNoticeCount(String field, String query) {
		
		int count=0;
		String sql = "SELECT COUNT(ID) COUNT FROM (SELECT ROWNUM num, N.* FROM(select * from notice where " +field+" like ? ORDER BY regdate desc) N )";
		
		String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "system", "root1234");
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, "%"+query+"%");
			ResultSet rs = st.executeQuery();
			
			if(rs.next()) 
			count = rs.getInt("count");
		 
				rs.close();
				st.close();
				con.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return count;
	}

	public Notice getNotice(int id) {
		Notice notice = null;
		
		String sql = "SELECT * FROM NOTICE WHERE ID=?";
		
		String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "system", "root1234");
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			
			if(rs.next()){		// 돌면서 결과집합을 각 객체에 담음
				int nid = rs.getInt("id");
				String title = rs.getString("title");
				String writerId = rs.getString("writer_id");
				Date regdate = rs.getDate("regdate");
				String hit = rs.getString("hit");
				String files = rs.getString("files");
				String content = rs.getString("content");
				boolean pub = rs.getBoolean("pub");
				
				notice = new Notice(nid, title, writerId, regdate, hit, files, content, pub);		//notice에 담음
		} 
				rs.close();
				st.close();
				con.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return notice;
	}

	public Notice getNextNotice(int id) {
		Notice notice = null;
		
		String sql ="SELECT * FROM NOTICE WHERE ID=( SELECT ID FROM NOTICE WHERE REGDATE > (SELECT REGDATE FROM NOTICE WHERE ID=?) AND ROWNUM =1)";
		
		String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "system", "root1234");
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			
			if(rs.next()){		// 돌면서 결과집합을 각 객체에 담음
				int nid = rs.getInt("id");
				String title = rs.getString("title");
				String writerId = rs.getString("writer_id");
				Date regdate = rs.getDate("regdate");
				String hit = rs.getString("hit");
				String files = rs.getString("files");
				String content = rs.getString("content");
				boolean pub = rs.getBoolean("pub");
				
				notice = new Notice(nid, title, writerId, regdate, hit, files, content, pub);		//notice에 담음
		} 
				rs.close();
				st.close();
				con.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return notice;
	}

	public Notice getPrevNotice(int id) {
		Notice notice = null;
		
		String sql = "SELECT ID FROM (SELECT * FROM NOTICE ORDER BY REGDATE DESC) WHERE REGDATE < (SELECT REGDATE FROM NOTICE WHERE ID=?) AND ROWNUM=1";
		
		String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "system", "root1234");
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			
			if(rs.next()){		// 돌면서 결과집합을 각 객체에 담음
				int nid = rs.getInt("id");
				String title = rs.getString("title");
				String writerId = rs.getString("writer_id");
				Date regdate = rs.getDate("regdate");
				String hit = rs.getString("hit");
				String files = rs.getString("files");
				String content = rs.getString("content");
				boolean pub = rs.getBoolean("pub");
				
				notice = new Notice(nid, title, writerId, regdate, hit, files, content, pub);	//notice에 담음
		} 
				rs.close();
				st.close();
				con.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return notice;
	}

	public int deleteNoticeAll(int[] ids) {
		int result =0;
		
		String params = "";
		
		for (int i = 0; i < ids.length; i++) {
			params += ids[i];
			
			if(i < ids.length-1)
				params += ",";
			
		}
		String sql = "DELETE NOTICE WHERE ID IN("+params+")";
		
		String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "system", "root1234");
			Statement st = con.createStatement();
			result = st.executeUpdate(sql);
			
				st.close();
				con.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}


}
