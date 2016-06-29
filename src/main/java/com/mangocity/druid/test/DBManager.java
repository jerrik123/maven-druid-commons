package com.mangocity.druid.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {

	private static Connection conn;

	private static PreparedStatement pstmt;

	private static ResultSet rs;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConn() throws SQLException {
		conn = DriverManager
				.getConnection(
						"jdbc:mysql:///max_conn_num?useUnicode=true&amp;characterEncoding=utf-8",
						"root", "root");
		return conn;
	}

	public static PreparedStatement getPreparedStatement(Connection connection,String sql)
			throws SQLException {
		return connection.prepareStatement(sql);
	}
	
	public static void printResultSet(PreparedStatement pstmt) throws SQLException{
		ResultSet rs = pstmt.executeQuery();
		while(null != rs && rs.next()){
			System.out.println("user_name: " + rs.getString("user_name"));
		}
		System.out.println("没有记录...");
	}
	
	public static void close(Connection conn) throws SQLException{
		if(conn != null){
			conn.close();
			conn = null;
		}
	}

}
