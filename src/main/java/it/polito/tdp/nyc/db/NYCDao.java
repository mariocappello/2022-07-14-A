package it.polito.tdp.nyc.db;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import it.polito.tdp.nyc.model.Adiacenza;
import it.polito.tdp.nyc.model.Hotspot;
import it.polito.tdp.nyc.model.NTAconSSID;

public class NYCDao {
	
	public List<Hotspot> getAllHotspot(){
		String sql = "SELECT * FROM nyc_wifi_hotspot_locations";
		List<Hotspot> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Hotspot(res.getInt("OBJECTID"), res.getString("Borough"),
						res.getString("Type"), res.getString("Provider"), res.getString("Name"),
						res.getString("Location"),res.getDouble("Latitude"),res.getDouble("Longitude"),
						res.getString("Location_T"),res.getString("City"),res.getString("SSID"),
						res.getString("SourceID"),res.getInt("BoroCode"),res.getString("BoroName"),
						res.getString("NTACode"), res.getString("NTAName"), res.getInt("Postcode")));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}
	
	public List<String> getBorough(){
		String sql = "SELECT DISTINCT borough FROM nyc_wifi_hotspot_locations";
		List<String> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(res.getString("Borough"));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}

	public List<String> creaVerticiGrafo(String borough) {
		String sql = "SELECT NTACode "
				+ "FROM nyc_wifi_hotspot_locations "
				+ "WHERE borough=? "
				+ "GROUP BY NTACode";
		
		List<String> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, borough);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(res.getString("NTACode"));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
		
	}
	
	
	
	public List<NTAconSSID> creaArchi(String borough){
		
		String sql="SELECT NTACode , SSID "
				+ "FROM nyc_wifi_hotspot_locations "
				+ "WHERE borough=? "
				+ "GROUP BY NTACode ";
		
		List<NTAconSSID> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, borough);
			ResultSet res = st.executeQuery();

			String lastNTA ="";
			while (res.next()) {
				if(! (lastNTA.equals(res.getString("NTACode")))) {
					
					//vuol dire che sto analizzando un altro elemento NTA diverso dal precedente,
					// quindi creo un nuovo Set di SSID e un nuovo oggetto NTAconSSID
					
					Set<String> SSIDsss = new HashSet<>();
					SSIDsss.add(res.getString("SSID"));
					
					NTAconSSID a = new NTAconSSID(res.getString("NTACode"), SSIDsss );
					result.add(a);
					
					// aggiorno il lastNTA
					
					lastNTA=res.getString("NTACode");
				}
				else {
					
					// l'NTA è lo stesso e vado ad aggiungere l'SSID alla lista di SSID, in pratica
					// prendo l'ultimo elemento della lista RESULT e ne prendo l'elemento SSID,
					// dopodichè lo aggiungo al SET di SSID
					
					result.get(result.size()-1).getSSID().add(res.getString("SSID"));
				}
				
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
		
	}
	
	
	
	
	
	
	
	
	
}
