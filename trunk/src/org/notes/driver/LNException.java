package org.notes.driver;


import java.sql.SQLException;


class LNException extends SQLException {
	LNException(){
    	super();
	}

	LNException(String s){
    	super(s);
	}

	LNException(String s, Throwable t){
    	super(s+".\n"+t);
    	this.initCause(t);
	}
	

} 
