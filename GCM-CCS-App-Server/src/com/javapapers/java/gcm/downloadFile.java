package com.javapapers.java.gcm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/downloadFile")
public class downloadFile extends HttpServlet{
	String targetPath = "";
	final int BUFFER_SIZE = 1024 *1024 * 32 ;
	
	private static final long serialVersionUID = 1L;

	public downloadFile() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request , response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		targetPath = "C:/tempfolder/" + request.getHeader("filename");
		System.out.println("target file : " + targetPath );
		File file = new File(targetPath);
		
		System.out.println("===== Begin headers =====");
		Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String headerName = names.nextElement();
            System.out.println(headerName + " = " + request.getHeader(headerName));        
        }
        System.out.println("===== End headers =====\n");
		
		FileInputStream inputstream = new FileInputStream(file);
		DataOutputStream dataOutputStram = new DataOutputStream(response.getOutputStream());
		
		byte[] bytes = new byte[BUFFER_SIZE];
		int length = -1 ;
		
		while( (length = inputstream.read(bytes)) > 0){
			dataOutputStram.write(bytes , 0 , length);
		}
		System.out.println("File download finish");
		dataOutputStram.flush();
		dataOutputStram.close();
		inputstream.close();
		
		System.out.println("DOWNLOAD DONE");
	}
}
