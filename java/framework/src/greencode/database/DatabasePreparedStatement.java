package greencode.database;

import greencode.kernel.GreenCodeConfig;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public final class DatabasePreparedStatement {
	private PreparedStatement preparedStatement;
	private ArrayList<ResultSet> resultSets = new ArrayList<ResultSet>();
	
	public DatabasePreparedStatement(PreparedStatement s) {
		this.setPreparedStatement(s);
	}
		
	public ResultSet executeQuery() throws SQLException
	{
		ResultSet s;
		try {
			s = this.getPreparedStatement().executeQuery();
		} catch (SQLException e) {
			String queryString = this.getPreparedStatement().toString();
			throw new SQLException(e.getMessage()+" : "+queryString.substring(queryString.indexOf(':')+2));
		}
		
		if(GreenCodeConfig.DataBase.isShowingResultQuery())
		{
			String queryString = this.getPreparedStatement().toString();
			System.out.println(queryString.substring(queryString.indexOf(':')+2));
		}
		
		resultSets.add(s);
		return s;		
	}
	
	public ArrayList<ResultSet> getResultSets() {
		return resultSets;
	}

	public PreparedStatement getPreparedStatement() {
		return preparedStatement;
	}

	public void setPreparedStatement(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}
}
