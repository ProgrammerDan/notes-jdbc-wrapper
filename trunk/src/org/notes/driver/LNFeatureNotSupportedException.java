package org.notes.driver;

import java.sql.SQLException;


class LNFeatureNotSupportedException extends SQLException{
	public LNFeatureNotSupportedException(){
		super("The rquested feature is not supported by current driver.");
	}
}
