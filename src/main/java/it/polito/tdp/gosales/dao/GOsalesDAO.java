package it.polito.tdp.gosales.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.gosales.model.Coppia;
import it.polito.tdp.gosales.model.DailySale;
import it.polito.tdp.gosales.model.Products;
import it.polito.tdp.gosales.model.Retailers;

public class GOsalesDAO {
	
	
	/**
	 * Metodo per leggere la lista di tutti i rivenditori dal database
	 * @return
	 */

	public List<Retailers> getAllRetailers(){
		String query = "SELECT * from go_retailers";
		List<Retailers> result = new ArrayList<Retailers>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Retailers(rs.getInt("Retailer_code"), 
						rs.getString("Retailer_name"),
						rs.getString("Type"), 
						rs.getString("Country")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	
	/**
	 * Metodo per leggere la lista di tutti i prodotti dal database
	 * @return
	 */
	public List<Products> getAllProducts(){
		String query = "SELECT * from go_products";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Products(rs.getInt("Product_number"), 
						rs.getString("Product_line"), 
						rs.getString("Product_type"), 
						rs.getString("Product"), 
						rs.getString("Product_brand"), 
						rs.getString("Product_color"),
						rs.getDouble("Unit_cost"), 
						rs.getDouble("Unit_price")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}

	
	/**
	 * Metodo per leggere la lista di tutte le vendite nel database
	 * @return
	 */
	public List<DailySale> getAllSales(){
		String query = "SELECT * from go_daily_sales";
		List<DailySale> result = new ArrayList<DailySale>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new DailySale(rs.getInt("retailer_code"),
				rs.getInt("product_number"),
				rs.getInt("order_method_code"),
				rs.getTimestamp("date").toLocalDateTime().toLocalDate(),
				rs.getInt("quantity"),
				rs.getDouble("unit_price"),
				rs.getDouble("unit_sale_price")  ));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<DailySale> getArchi(){
		String query = "SELECT r1.Retailer_code,r2.Retailer_code, COUNT(DISTINCT(s1.Product_number)) totale "
				+ "FROM go_retailers r1, go_retailers r2, go_daily_sales s1, go_daily_sales s2 "
				+ "WHERE r1.Country=? AND r1.Country=r2.Country AND r1.Retailer_code=s1.Retailer_code AND r2.Retailer_code=s2.Retailer_code "
				+ "AND s1.Product_number=s2.Product_number AND r1.Retailer_code>r2.Retailer_code AND YEAR(s1.Date)=? AND YEAR(s1.Date)=YEAR(s2.Date) "
				+ "GROUP BY r1.Retailer_code,r2.Retailer_code "
				+ "HAVING totale>=?";
		List<DailySale> result = new ArrayList<DailySale>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new DailySale(rs.getInt("retailer_code"),
				rs.getInt("product_number"),
				rs.getInt("order_method_code"),
				rs.getTimestamp("date").toLocalDateTime().toLocalDate(),
				rs.getInt("quantity"),
				rs.getDouble("unit_price"),
				rs.getDouble("unit_sale_price")  ));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}


	public List<String> getAllCountries() {
		String query = "SELECT DISTINCT r.Country "
				+ "FROM go_retailers r "
				+ "ORDER BY r.Country ";
		List<String> result = new ArrayList<String>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Country"));
			}
			
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}


	public List<Retailers> getRetailers(String nazione) {
		String query = "SELECT * "
				+ "FROM go_retailers r "
				+ "WHERE r.Country=? "
				+ "ORDER BY r.Retailer_name ";
		List<Retailers> result = new ArrayList<Retailers>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, nazione);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Retailers(rs.getInt("Retailer_code"), 
						rs.getString("Retailer_name"),
						rs.getString("Type"), 
						rs.getString("Country")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}


	public List<Coppia> getCoppie(Integer anno, String nazione, Integer m, Map<Integer, Retailers> idMap) {
		String query = "SELECT tmp1.Retailer_code AS r1, tmp2.Retailer_code AS r2, COUNT( DISTINCT tmp1.Product_number) AS comuni "
				+ "FROM "
				+ "(SELECT s.Product_number, s.Retailer_code "
				+ "FROM go_daily_sales s, go_retailers r "
				+ "WHERE s.Retailer_code=r.Retailer_code AND r.Country=? AND YEAR(s.Date)=? "
				+ "GROUP BY s.Product_number, s.Retailer_code) tmp1, "
				+ "(SELECT s.Product_number, s.Retailer_code "
				+ "FROM go_daily_sales s, go_retailers r "
				+ "WHERE s.Retailer_code=r.Retailer_code AND r.Country= ? AND YEAR(s.Date)=? "
				+ "GROUP BY s.Product_number, s.Retailer_code) tmp2 "
				+ "WHERE tmp1.Retailer_code>tmp2.Retailer_code AND tmp1.Product_number=tmp2.Product_number "
				+ "GROUP BY tmp1.Retailer_code, tmp2.Retailer_code "
				+ "HAVING comuni>=? ";
		List<Coppia> result = new ArrayList<Coppia>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, nazione);
			st.setInt(2, anno);
			st.setString(3, nazione);
			st.setInt(4, anno);
			st.setInt(5, m);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Coppia(idMap.get(rs.getInt("r1")),idMap.get(rs.getInt("r2")),rs.getInt("comuni")));
				
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}


	public List<Products> getProdotti(Retailers r, int anno) {
		String query = "SELECT p.* "
				+ "FROM go_daily_sales s, go_products p "
				+ "WHERE s.Retailer_code=? AND s.Product_number=p.Product_number AND YEAR(s.Date)=? "
				+ "GROUP BY p.Product_number";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, r.getCode());
			st.setInt(2, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Products(rs.getInt("Product_number"), 
						rs.getString("Product_line"), 
						rs.getString("Product_type"), 
						rs.getString("Product"), 
						rs.getString("Product_brand"), 
						rs.getString("Product_color"),
						rs.getDouble("Unit_cost"), 
						rs.getDouble("Unit_price")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}


	public int getAcquisti(Retailers ret, Products prod, int anno) {
		String query = "SELECT COUNT(*) AS tot "
				+ "FROM go_daily_sales s "
				+ "WHERE s.Retailer_code=? AND s.Product_number=? AND YEAR(s.Date)=?";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, ret.getCode());
			st.setInt(2, prod.getNumber());
			st.setInt(3, anno);
			ResultSet rs = st.executeQuery();
			int tot=0;
			if(rs.next()) {
				tot=rs.getInt("tot");
				
			}
			conn.close();
			return tot;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}


	public int getQuantita(Retailers ret, Products prod, int anno) {
		String query = "SELECT SUM(s.Quantity) AS tot "
				+ "FROM go_daily_sales s "
				+ "WHERE s.Retailer_code=? AND s.Product_number=? AND YEAR(s.Date)=?";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, ret.getCode());
			st.setInt(2, prod.getNumber());
			st.setInt(3, anno);
			ResultSet rs = st.executeQuery();
			int tot=0;
			if(rs.next()) {
				tot=rs.getInt("tot");
				
			}
			conn.close();
			return tot;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}


	public Coppia getProdottiCondivisi(Retailers r1, Retailers r2, Integer anno) {
		String query = "SELECT COUNT(DISTINCT s1.Product_number) AS condivisi "
				+ "FROM go_daily_sales s1, go_daily_sales s2 "
				+ "WHERE  s1.Retailer_code=? AND s2.Retailer_code=? AND YEAR(s1.Date)=YEAR(s2.Date) AND YEAR(s1.Date)=? ";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, r1.getCode());
			st.setInt(2, r2.getCode());
			st.setInt(3, anno);
			ResultSet rs = st.executeQuery();
			Coppia tot=null;;
			if(rs.next()) {
				tot=new Coppia(r1,r2,rs.getInt("condivisi"));
				
				
			}
			conn.close();
			return tot;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
}
