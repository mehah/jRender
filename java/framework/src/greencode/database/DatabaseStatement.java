package greencode.database;

import greencode.kernel.GreenCodeConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public final class DatabaseStatement {
	private Statement statement;
	private ArrayList<ResultSet> resultSets = new ArrayList<ResultSet>();
	
	public DatabaseStatement(Statement s) {
		this.setStatement(s);
	}
	
	public ResultSet executeQuery(String sql) throws SQLException
	{
		if(GreenCodeConfig.DataBase.isShowingResultQuery())
			System.out.println(sql);
		
		ResultSet s = this.getStatement().executeQuery(sql);
		
		resultSets.add(s);
		return s;		
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public Statement getStatement() {
		return statement;
	}

	public ArrayList<ResultSet> getResultSets() {
		return resultSets;
	}
}
