package com.javapapers.java.gcm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class servlet_test
 */
@WebServlet("/uploadFile")
public class uploadFile extends HttpServlet {
	String filename = "";
	private static final long serialVersionUID = 1L;
	static final String SAVE_DIR= "C:/tempfolder/";
	static final int BUFFER_SIZE = 1024*1024*32 ; 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public uploadFile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request , response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	
		request.setCharacterEncoding("UTF-8");
	    
		filename = URLDecoder.decode(request.getHeader("filename") , "UTF-8");
		File saveFile = new File(SAVE_DIR + filename);
		
		System.out.println("===== Begin headers =====");
		Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String headerName = names.nextElement();
            System.out.println(headerName + " = " + request.getHeader(headerName));        
        }
        System.out.println("===== End headers =====\n");
        
        
        InputStream inputStream = request.getInputStream();
        
        FileOutputStream outputStream = new FileOutputStream(saveFile);
        
        byte[] buffer = new byte[BUFFER_SIZE];
        int byteRead = -1 ;
        
        System.out.println("receive.... ");
        while( ( byteRead = inputStream.read(buffer) ) != -1)
        {
        	outputStream.write(buffer , 0 , byteRead);
        }
        System.out.println("Data received.");
        
        outputStream.close();
        inputStream.close();
        
        System.out.println("File written to: " + saveFile.getAbsolutePath());
        
        
        response.getWriter().print("UPLOAD DONE");
        
	}

}

