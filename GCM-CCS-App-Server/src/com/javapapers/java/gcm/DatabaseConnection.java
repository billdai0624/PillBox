package com.javapapers.java.gcm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
	
	final static String DRIVER = "com.mysql.jdbc.Driver";
    final static String URL = "jdbc:mysql://192.168.1.21:3306/client2client";
    private static String username = "jacky07081278" ;
    private static String password = "a123456789"; 
    Connection con ;
	
	public DatabaseConnection(){
		con = null ;
		try {
			Class.forName(DRIVER);
			con = DriverManager.getConnection(URL , username , password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}
		
	}
	
	public String[] selectString(String sql , String columnIndex)
	{
		String[] array = new String[100];
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int i = 0 ;
			while(rs.next())
			{
				array[i] = rs.getString(columnIndex);
			}
			rs.close();
            stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			return null;
		}
		
		return array;
	}
	
	public void storeGCM(String regId , String phoneNum , String userName){
		try {
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO UserInformation(PhoneNumber , UserName , RegId , Storage ) " + 
			"VALUES ( \'" + phoneNum + "\' , \'" + userName +  "\' , \'" + regId +  "\' , \'"  + " "  + "\'  )";
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getRegId(String phoneNum){
		String regId = "";
		try {
			Statement stmt = con.createStatement();
			String sql = "SELECT RegId FROM UserInformation WHERE PhoneNumber = \'" + phoneNum + "\' ";
			ResultSet rs = stmt.executeQuery(sql);
			System.out.println("select" + sql);
			rs.next();
			regId = rs.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return regId;
		
	}
	
	public void updateStorage(String phoneNum , String storage){
		try {
			Statement stmt = con.createStatement();
			String sql = "UPDATE UserInformation SET storage = \'" + storage + "\' WHERE PhoneNumber = \'" +  phoneNum + "\' ";
			System.out.println("update : " + sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getUserName(String regId){
		Statement stmt;
		try {
			stmt = con.createStatement();
			String sql = "SELECT UserName FROM UserInformation WHERE RegId = \'" + regId + "\' ";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				if(!rs.getString(1).isEmpty())
					return rs.getString(1) ;
			}
			else{
				return "" ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "" ;
	}
	
	public boolean checkIfExist(String phoneNum){
		Statement stmt;
		try {
			stmt = con.createStatement();
			String sql = "SELECT UserName FROM UserInformation WHERE PhoneNumber = \'" + phoneNum + "\' ";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				if(!rs.getString(1).isEmpty()){
					return true ;
				}
			}
			else{
				return false ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
		return false;
	}

	
}
