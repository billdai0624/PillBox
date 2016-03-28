package com.javapapers.java.gcm;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/Synchronize")
public class Synchronize extends HttpServlet{
	private DatabaseConnection dbconnect ;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Synchronize(){
		super();
	}
	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String output = "";
		PrintWriter printWriter = response.getWriter();
		System.out.println("===== Begin headers =====");
		Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String headerName = names.nextElement();
            System.out.println(headerName + " = " + request.getHeader(headerName));        
        }
        System.out.println("===== End headers =====\n");
		
		byte[] input = new byte[1024*1024];
		InputStream inputStream = request.getInputStream();
		int count ;
		while( ( count = inputStream.read(input) )!= -1){
			count++;
		}
		String get = new String(input);
		
		output = phoneNumCheck(get);
		
		System.out.println("output" + output);
		
		
		printWriter.write(output);
		printWriter.flush();
		printWriter.close();
	}
	
	private String phoneNumCheck(String input){
		dbconnect = new DatabaseConnection();
		String output = "";
		String[] array = input.split(" "); 
		for(String s : array){
			if( dbconnect.checkIfExist(s.trim()) ){
				output = output.concat(" " + s);
			}
		}
	
		return output ;
	}
	
}
