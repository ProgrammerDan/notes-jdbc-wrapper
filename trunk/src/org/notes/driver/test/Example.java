package org.notes.driver.test;

import java.util.*;
import java.sql.*;
import java.io.*;


public class Example{

	static String driver="org.notes.driver.LNDriver";

//	static String url="jdbc:org:notes:172.17.45.138:63148?db=names.nsf";
//	static String uid="front_jboss";
//	static String pwd="front21";	
	
//	static String url="jdbc:org:notes:172.17.44.132?db=App\\SalPrj\\SalaryProject_awf.nsf";
//	static String url="jdbc:org:notes:172.17.44.132?db=App\\SalPrj\\SalPrj_Dictionary.nsf";
//	static String uid="Approver5";
//	static String pwd="RobinCrusoe";
	static String url="jdbc:org:notes:Salo:63148?db=dover.nsf";
	static String uid="front_jboss";
	static String pwd="front21";
	static long time = System.currentTimeMillis();	


	public static void main(String arg[])throws Exception{
		//Loads specified driver.
		//All drivers must support self registration by DriverManager.
		Class.forName(driver);
		
		//Connect to database
		Connection conn = DriverManager.getConnection(url,uid,pwd);
		
		

	    String query = "SELECT VIEWLIST";
		String [] param = {};
		execute(query, param, conn);
		
		
		//query = "SELECT VIEW (Hidden)\\Dic_OLFs";
		//execute(query, param, conn);
		
		conn.close();
		
		
	}


	private static void timeCheck(String i){
		    System.out.println("Duration " + i + " = " + (System.currentTimeMillis()-time));
		    time = System.currentTimeMillis();	
	}
	
	public static void execute(String query,String[] param, Connection conn)throws Exception{
		String s="";
		PreparedStatement st = conn.prepareStatement(query);
		
		timeCheck("start");
        System.out.println("Execute: "+query);
		
		for (int i=0;i<param.length;i++){
			System.out.println(param[i]);
			st.setString(i+1,param[i]);
		}
		
		ResultSet rs = st.executeQuery();		
                               
        ResultSetMetaData rsmd = rs.getMetaData();
        
        for (int i=0;i<rsmd.getColumnCount();i++){
        	s+=rsmd.getColumnName(i+1);
        	s+="\t";
        }
        
        s+="\n";
        System.out.println(s);

        // print the results
        while (rs.next()) {
        	s="";
            for(int i=0;i<rsmd.getColumnCount();i++){
            	s+="" + rs.getString(""+(i+1)) + "\t";
            }
            System.out.println(s);
            //s+="\n";
        }
        

		rs.close();
		timeCheck("end");
	}

}
