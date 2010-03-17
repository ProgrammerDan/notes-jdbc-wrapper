/*
 * Author: TMS Team
 * Creation Date        : 02-15-2001
 * Modification Date    : 02-15-2001
 * List of Modifications:
 * Date       Version Author               Description/Purpose
 * ---------- ------- -------------------- ------------------------------------
 * 02-15-2001    1.00 TMS Team             class created
 */
package org.notes.driver;

import java.sql.Connection;
import java.sql.Savepoint;
import java.sql.SQLException;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import lotus.domino.*;

/**
 * Implements standard <code>java.sql.Connection</code> .
 * It passes all requests to database <code>connection</code> specified in constructor.
 * There are some features of connection managing:
 * <ul>
 * <li><code>timeout</code> property to check connection idle time
 * <li><code>isOld()</code> method to check does time of maximum inactivity passed.
 * <li><code>stackTrace</code> property that used if connection is not released to trace caller.
 * </ul>
 */
public class LNConnection implements Connection {

    /** connection parameters */
    public static final String HOST = "host";
    public static final String USER = "user";
    public static final String PASS = "password";
    public static final String CHARSET = "charset";
    public static final String DATABASE = "db";
    
    private   String host;
    protected String user;
    protected String pass;
    private   String charset;
    private   String dbName;
    
    //lotus session
    private Session session = null;
    private Database database = null;

    /**
     * Creates <code>DBConnection</code>.
     * @param connection The real database connection.
     * @param pool connection pool this connection is related to.
     */
    protected LNConnection(Properties info) throws SQLException {
        host = info.getProperty(HOST);
        user = info.getProperty(USER);
        pass = info.getProperty(PASS);
        charset = info.getProperty(CHARSET);
        dbName = info.getProperty(DATABASE);
        if (charset == null || charset.length() == 0) {
            charset = java.nio.charset.Charset.defaultCharset().name();
        }
        getLNSession();
    }
    
    
    
    protected Database getLNDatabase() throws LNException{
    	if(database!=null){
    		try {
	    		if(database.isOpen())return database;
    		}catch(Exception e){
	    		System.out.println("LNConnection.getLNDatabase(): database.isOpen() exception: "+e);
    		}
    		//database is closed, let's free ressources
            try {
                database.recycle();
    		}catch(Exception e){
	    		System.out.println("LNConnection.getLNDatabase(): database.recycle() exception: "+e);
    		}
            database = null;
    	}
    	
    	//check if session is valid
    	getLNSession();
    	
   		//database is not open let's open database
    	if(database==null){
    		try {
	    		database=session.getDatabase("",dbName,false);
    		}catch(Exception e){
				throw new LNException("Database Exception: Can't open database \""+dbName+"\" on server \""+host+"\"",e);
    		}
			if(database==null){
				throw new LNException("Database Exception: Can't get database \""+dbName+"\" on server \""+host+"\"");
			}
    	}
    	return database;
    }

    private Session getLNSession() throws LNException{
    	if(session!=null){
    		try {
	    		if(session.isValid())return session;
    		}catch(Exception e){
	    		System.out.println("LNConnection.getLNSession(): session.isValid() exception: "+e);
    		}
    		//session closed, let's free ressources
            try {
                session.recycle();
    		}catch(Exception e){
	    		System.out.println("LNConnection.getLNSession(): session.recycle() exception: "+e);
    		}
            session = null;
    	}
    	
    	//check if session is valid
    	
    	
   		//database is not open let's open database
    	if(session==null){
    		try {
	    		session=NotesFactory.createSession(host, user, pass);
    		}catch(Exception e){
	            throw new LNException("LotusNotes Connection Error: host=" + host + " user=" + user, e);
    		}
			if(session==null){
	            throw new LNException("LotusNotes Connection Error: host=" + host + " user=" + user+": Lotus returns NULL Session.");
			}
    	}
    	return session;
    }

    /**
     * Method isClosed
     * standard <code>JDBC Connection</code> method
     * @return
     * @throws SQLException
     */
    public boolean isClosed() throws SQLException {
    	if(session==null)return true;
    	try {
    		if(session.isValid())return false;
    	}catch(Exception e){
    		System.out.println("LNConnection.isClosed() exception: "+e);
    	}
   		System.out.println("LNConnection.isClosed() returns true.");
        return true;
    }


    /**
     * Method close
     * returns this connection to ConnectionPool
     * @throws SQLException
     */
    public void close() throws SQLException {
        if (database != null) {
            try {
                database.recycle();
            } catch (Exception e) {}
            database = null;
        }
        if (session != null) {
            try {
                session.recycle();
            } catch (Exception e) {}
            session = null;
        }
    }


    //Connection interface methods
    //All methods below this line are described in java.sql.Connection class
    /**
     * Method clearWarnings
     * standard <code>JDBC Connection</code> method
     * @throws SQLException
     */
    public void clearWarnings() throws SQLException {
        //nothing to do
    }
    
    /**
     * Method commit
     * standard <code>JDBC Connection</code> method
     * @throws SQLException
     */
    public void commit() throws SQLException {
        //do we have something to commit into the lotus?
    }

    /**
     * Method createStatement
     * standard <code>JDBC Connection</code> method
     *
     * @return
     * @throws SQLException
     */
    public java.sql.Statement createStatement() throws SQLException {
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method createStatement
     * standard <code>JDBC Connection</code> method
     * @param resultSetType
     * @param resultSetConcurrency
     * @return
     * @throws SQLException
     */
    public java.sql.Statement createStatement(
            int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method createStatement
     * standard <code>JDBC Connection</code> method
     * @param resultSetType
     * @param resultSetConcurrency
     * @return
     * @throws SQLException
     */
    public java.sql.Statement createStatement(
            int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method getAutoCommit
     * standard <code>JDBC Connection</code> method
     * @return
     * @throws SQLException
     */
    public boolean getAutoCommit() throws SQLException {
        //no transactions in OFS
        return true;
    }

    /**
     * Method getCatalog
     * @return
     * @throws SQLException
     */
    public String getCatalog() throws SQLException {
        return null; //no catalogs, so current catalog is null

    }

    /**
     * Method getMetaData
     * standard <code>JDBC Connection</code> method
     * @return
     * @throws SQLException
     */
    public java.sql.DatabaseMetaData getMetaData() throws SQLException {
        //not supported yet. maybe in the future.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method getTransactionIsolation
     * standard <code>JDBC Connection</code> method
     * @return
     * @throws SQLException
     */
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    /**
     * Method getTypeMap
     * standard <code>JDBC Connection</code> method
     * @return
     * @throws SQLException
     */
    public java.util.Map getTypeMap() throws SQLException {
        return new java.util.HashMap();
    }

    /**
     * Method getWarnings
     * standard <code>JDBC Connection</code> method
     * @return
     * @throws SQLException
     */
    public java.sql.SQLWarning getWarnings() throws SQLException {
        return null;
    }

    /**
     * Method isReadOnly
     * standard <code>JDBC Connection</code> method
     * @return
     * @throws SQLException
     */
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    /**
     * Method nativeSQL
     * standard <code>JDBC Connection</code> method
     * @param sql
     * @return
     * @throws SQLException
     */
    public String nativeSQL(String sql) throws SQLException {
        //???  not supported yet ?
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method prepareCall
     * standard <code>JDBC Connection</code> method
     * @param sql
     * @return
     * @throws SQLException
     */
    public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
    	//!!!!!!
        return new LNCallableStatement(this, sql);
    }

    /**
     * Method prepareCall
     * standard <code>JDBC Connection</code> method
     * @param sql
     * @param resultSetType
     * @param resultSetConcurrency
     * @return
     * @throws SQLException
     */
    public java.sql.CallableStatement prepareCall(
            String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        //not supported
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method prepareCall
     * standard <code>JDBC Connection</code> method
     * @param sql
     * @param resultSetType
     * @param resultSetConcurrency
     * @return
     * @throws SQLException
     */
    public java.sql.CallableStatement prepareCall(
            String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        //not supported
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method prepareStatement
     * standard <code>JDBC Connection</code> method
     * @param sql
     * @return
     * @throws SQLException
     */
	public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
		if ( sql.matches("\\s*SELECT\\s+VIEW\\s+.*") ){
			return new LNViewPreparedStatement(this, sql);
		}else if(  sql.matches("\\s*SELECT\\s+VIEWLIST\\s*")  ){
			return new LNViewListPreparedStatement(this, sql);
		}
		throw new LNException("Not supported query type: "+sql);
	}

    /**
     * Method prepareStatement
     * standard <code>JDBC Connection</code> method
     * @param sql
     * @return
     * @throws SQLException
     */
    public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames)
            throws SQLException {
        //maybe for the future
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method prepareStatement
     * standard <code>JDBC Connection</code> method
     * @param sql
     * @return
     * @throws SQLException
     */
    public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes)
            throws SQLException {
        //later
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method prepareStatement
     * standard <code>JDBC Connection</code> method
     * @param sql
     * @return
     * @throws SQLException
     */
    public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        //no such a keys.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method prepareStatement
     * standard <code>JDBC Connection</code> method
     * @param sql
     * @param resultSetType
     * @param resultSetConcurrency
     * @return
     * @throws SQLException
     */
    public java.sql.PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        //not supported.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method prepareStatement
     * standard <code>JDBC Connection</code> method
     * @param sql
     * @param resultSetType
     * @param resultSetConcurrency
     * @return
     * @throws SQLException
     */
    public java.sql.PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method rollback
     * standard <code>JDBC Connection</code> method
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        //nothing to do
    }

    /**
     * Method rollback
     * standard <code>JDBC Connection</code> method
     * @throws SQLException
     */
    public void rollback(Savepoint savepoint) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Changes the holdability of <code>ResultSet</code> objects
     * created using this <code>Connection</code> object to the given
     * holdability.
     *
     * @param holdability a <code>ResultSet</code> holdability constant; one of
     *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
     *        <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
     * @throws SQLException if a database access occurs, the given parameter
     *         is not a <code>ResultSet</code> constant indicating holdability,
     *         or the given holdability is not supported
     * @see #getHoldability
     * @see ResultSet
     * @since 1.4
     */
    public void setHoldability(int holdability) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Retrieves the current holdability of <code>ResultSet</code> objects
     * created using this <code>Connection</code> object.
     *
     * @return the holdability, one of
     *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
     *        <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
     * @throws SQLException if a database access occurs
     * @see #setHoldability
     * @see ResultSet
     * @since 1.4
     */
    public int getHoldability() throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Creates an unnamed savepoint in the current transaction and 
     * returns the new <code>Savepoint</code> object that represents it.
     *
     * @return the new <code>Savepoint</code> object
     * @exception SQLException if a database access error occurs
     *            or this <code>Connection</code> object is currently in
     *            auto-commit mode
     * @see Savepoint
     * @since 1.4
     */
    public Savepoint setSavepoint() throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Creates a savepoint with the given name in the current transaction
     * and returns the new <code>Savepoint</code> object that represents it.
     *
     * @param name a <code>String</code> containing the name of the savepoint
     * @return the new <code>Savepoint</code> object
     * @exception SQLException if a database access error occurs
     *            or this <code>Connection</code> object is currently in
     *            auto-commit mode
     * @see Savepoint
     * @since 1.4
     */
    public Savepoint setSavepoint(String name) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Removes the given <code>Savepoint</code> object from the current 
     * transaction. Any reference to the savepoint after it have been removed 
     * will cause an <code>SQLException</code> to be thrown.
     *
     * @param savepoint the <code>Savepoint</code> object to be removed
     * @exception SQLException if a database access error occurs or
     *            the given <code>Savepoint</code> object is not a valid 
     *            savepoint in the current transaction
     * @since 1.4
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method setAutoCommit
     * standard <code>JDBC Connection</code> method
     * @param b
     * @throws SQLException
     */
    public void setAutoCommit(boolean b) throws SQLException {
        //always autocommit
    }

    /**
     * Method setCatalog
     * standard <code>JDBC Connection</code> method
     * @param catalog
     * @throws SQLException
     */
    public void setCatalog(String catalog) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method setReadOnly
     * standard <code>JDBC Connection</code> method
     * @param b
     * @throws SQLException
     */
    public void setReadOnly(boolean b) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method setTransactionIsolation
     * standard <code>JDBC Connection</code> method
     * @param i
     * @throws SQLException
     */
    public void setTransactionIsolation(int i) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    /**
     * Method setTypeMap
     * standard <code>JDBC Connection</code> method
     * @param map
     * @throws SQLException
     */
    public void setTypeMap(java.util.Map map) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    public java.sql.Clob createClob() throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    public java.sql.Blob createBlob() throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    public boolean isValid(int timeout) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    public <T> T unwrap(java.lang.Class<T> iface) throws java.sql.SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }

    public boolean isWrapperFor(java.lang.Class<?> iface) throws java.sql.SQLException {
        //no support.
        throw new LNFeatureNotSupportedException();
    }


    //finalization
    protected void finalize() {
    }
    /*
    //JAVA 6 VERSION
    
    public java.sql.NClob createNClob() throws SQLException{
    //no support.
    throw new LNFeatureNotSupportedException();
    }
    
    
    public java.sql.SQLXML createSQLXML() throws SQLException{
    //no support.
    throw new LNFeatureNotSupportedException();
    }
    
    public String getClientInfo(String name) throws SQLException{
    //no support.
    throw new LNFeatureNotSupportedException();
    }
    
    
    public java.util.Properties getClientInfo() throws SQLException{
    //no support.
    throw new LNFeatureNotSupportedException();
    }
    
    public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException{
    //no support.
    }
    
    public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException{
    //no support.
    }
    
    
     */
}
