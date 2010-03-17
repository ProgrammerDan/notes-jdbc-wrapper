/*
 * Author: TMS Team
 * Creation Date        : 02-28-2001
 * Modification Date    : 02-28-2001
 * List of Modifications:
 * Date       Version Author               Description/Purpose
 * ---------- ------- -------------------- ------------------------------------
 * 02-28-2001    1.00 TMS Team             initial release
 */
package org.notes.driver;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;

import java.util.Date;
import java.util.StringTokenizer;

/**
 * A LNDriver class provides all <code>java.sql.Driver</code> requirements.
 */
public class LNDriver implements Driver{

	private static final String myUrl="jdbc:org:notes:";

	private static LNDriver m_defaultDrvr=null;
	static{
		try{
			if(m_defaultDrvr==null){
				m_defaultDrvr=new LNDriver();
				DriverManager.registerDriver(m_defaultDrvr);
			}
		}catch(Exception e){
			System.err.println("org.notes.driver.LNDriver Exception:");
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Ñonstructs a new LNDriver.
	 * @throws SQLException if any sql exception occured
	 */
	public LNDriver() throws Exception{
	}


	/**
	 * Returns true if the driver thinks that it can open
	 * a connection to the given URL.
	 * @param url the database connection url to check.
	 * @throws SQLException if any sql exception occured
	 */
	public boolean acceptsURL(String url) throws SQLException{
		if(url==null)return false;
		return url.startsWith(myUrl);
	}

	private void getUrlProperties(String strURL,Properties prop){
		StringTokenizer st      =new StringTokenizer(strURL, "&");
		int             j       =0;
		String          strTemp="";
		while(st.hasMoreTokens()){
			strTemp=st.nextToken();
			j       =strTemp.indexOf("=");
			prop.setProperty(strTemp.substring(0, j), strTemp.substring(j+1));
		}
	}

	/**
	 * Attempts to make a database connection to the given URL
	 * @param url database connection url.
	 * @param info database connection parameters (user, password, etc.).
	 * @return a new database connection
	 * @throws SQLException if any sql exception occured
	 * @see java.sql.Driver
	 */
	public Connection connect(String url, Properties info) throws SQLException{
		if(!acceptsURL(url))return null;
		url=url.substring(myUrl.length());
		String host;
		int i=url.indexOf("?");
		if(i==-1){
			host=url;
		}else{
			host=url.substring(0,i);
			getUrlProperties(url.substring(i+1),info);
		}
		info.setProperty(LNConnection.HOST,host);
		//info.list(System.out);
		Connection con=new LNConnection(info);
		return con;
	}

	/**
	 * Gets the driver's major version number
	 * @return the driver's major version number
	 */
	public int getMajorVersion(){
		return 2;
	}

	/**
	 * Gets the driver's minor version number
	 * @return the driver's minor version number
	 */
	public int getMinorVersion(){
		return 0;
	}

	/**
	 * Gets information about the possible properties for this driver
	 * @param url database connection url.
	 * @param info database connection parameters.
	 * @see java.sql.Driver
	 */
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException{
		return new DriverPropertyInfo[0];
	}

	/**
	 * Reports whether this driver is a genuine JDBC COMPLIANT driver
	 * @see java.sql.Driver
	 */
	public boolean jdbcCompliant(){
		return true;
	}

}

