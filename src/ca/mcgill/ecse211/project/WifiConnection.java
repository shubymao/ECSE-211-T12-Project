package ca.mcgill.ecse211.project;

import java.util.HashMap;
import java.util.Map;

public class WifiConnection implements AutoCloseable{

	/**
	 * Server ip
	 */
	public String serverIp;
	
	/**
	 * Team number
	 */
	public int teamNumber;
	
	/**
	 * Boolean to print the error messages
	 */
	public boolean enableDebugWifiPrint;
	
	/**
	 * Constructor of the Wifi Connection
	 * @param serverIp
	 * @param teamNumber
	 * @param enableDebugWifiPrint
	 */
	public WifiConnection(String serverIp, int teamNumber, boolean enableDebugWifiPrint) {
		this.serverIp = serverIp;
		this.teamNumber = teamNumber;
		this.enableDebugWifiPrint = enableDebugWifiPrint;
	}
	
	public Map<String, Object> getData() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		
		return result;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
