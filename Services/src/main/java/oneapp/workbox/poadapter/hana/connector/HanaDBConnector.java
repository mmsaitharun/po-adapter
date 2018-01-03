package oneapp.workbox.poadapter.hana.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.incture.pmc.poadapter.util.ExecutionFault;
import com.incture.pmc.poadapter.util.MessageUIDto;
import com.incture.pmc.poadapter.util.NoResultFault;

public class HanaDBConnector {

	private static HanaDBConnector jdbc;

	private HanaDBConnector() {
	}

	/**
	 *
	 * @return HanaDBConnector Database connection object
	 */
	public static HanaDBConnector getInstance() {
		if (jdbc == null) {
			jdbc = new HanaDBConnector();
		}
		return jdbc;
	}

	private static Connection getConnection() throws ExecutionFault {
		
		String url = "jdbc:sap://localhost:30015/";
		String dbName = "workbox";
		String driver = "com.sap.db.jdbc.Driver";
		String userName = "workbox";
		String password = "Incture1234567891012";
		Connection con = null;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url+dbName, userName, password);
		} catch (ClassNotFoundException e) {
			System.err.println("[PMC][POADAPTER][HANA][HanaDBConnector][getConnection][Exception] : "+e.getMessage());
			MessageUIDto faultInfo = new MessageUIDto();
			faultInfo.setMessage(e.getMessage());
			String message = "Class Not Found Exception";
			throw new ExecutionFault(message, faultInfo, e);
		} catch (SQLException e) {
			System.err.println("[PMC][POADAPTER][HANA][HanaDBConnector][getConnection][Exception] : "+e.getMessage());
			MessageUIDto faultInfo = new MessageUIDto();
			faultInfo.setMessage(e.getMessage());
			String message = "SQL Exception";
			throw new ExecutionFault(message, faultInfo, e);
		}
		return con;

	}

	/**
	 * @desc Method to insert data to a table
	 * @param insertQuery String The Insert query
	 * @return boolean
	 * @throws ExecutionFault 
	 * @throws SQLException
	 */
	public int insert(String insertQuery) throws ExecutionFault {
		Statement statement = null;
		int result = 0;
		try {
			statement = HanaDBConnector.getConnection().createStatement();
			result = statement.executeUpdate(insertQuery);
		} catch (SQLException e){
			System.err.println("[PMC][POADAPTER][HANA][HanaDBConnector][insert][Exception] : "+e.getMessage());
			MessageUIDto faultInfo = new MessageUIDto();
			faultInfo.setMessage(e.getMessage());
			String message = "SQL Exception";
			throw new ExecutionFault(message, faultInfo, e);
		}
		return result;
	}

	/**
	 * @param query String The query to be executed
	 * @return a ResultSet object containing the results or null if not available
	 * @throws SQLException
	 * @throws ExecutionFault 
	 */
	public ResultSet query(String query) throws SQLException, ExecutionFault, NoResultFault {
		Statement statement = null;
		statement = HanaDBConnector.getConnection().createStatement();
		ResultSet res = statement.executeQuery(query);
		return res;
	}
	
	/**
	 * @param query String The query to be executed
	 * @return a integer object containing the results or null if not available
	 * @throws SQLException
	 */
	public int update(String query) {
		Statement statement = null;
		try {
			statement = HanaDBConnector.getConnection().createStatement();
			return statement.executeUpdate(query);
		} catch (Exception e) {
			System.err.println("[PMC][POADAPTER][HANA][HanaDBConnector][update][Exception] : " + e.getMessage());
			return 0;
		}
	}
}
