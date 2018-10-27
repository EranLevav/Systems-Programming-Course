package bgu.spl181.net.impl.databases;

import java.util.ArrayList;


public class Movie {
	private long id;
	private String name;
	private long price;
	private ArrayList<String> bannedCountries;
	private long availableAmount;
	private long totalAmount;
	/**
	 * @param id
	 * @param name
	 * @param price
	 * @param bannedCountries
	 * @param availableAmount
	 * @param totalAmount
	 */
	public Movie(long id, String name, long price, ArrayList<String> bannedCountries, long availableAmount,
			long totalAmount) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.bannedCountries = bannedCountries;
		this.availableAmount = availableAmount;
		this.totalAmount = totalAmount;
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the price
	 */
	public long getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(long price) {
		this.price = price;
	}
	/**
	 * @return the bannedCountries
	 */
	public ArrayList<String> getBannedCountries() {
		return bannedCountries;
	}
	/**
	 * @param bannedCountries the bannedCountries to set
	 */
	public void setBannedCountries(ArrayList<String> bannedCountries) {
		this.bannedCountries = bannedCountries;
	}
	/**
	 * @return the availableAmount
	 */
	public long getAvailableAmount() {
		return availableAmount;
	}
	/**
	 * @param availableAmount the availableAmount to set
	 */
	public void setAvailableAmount(long availableAmount) {
		this.availableAmount = availableAmount;
	}
	/**
	 * @return the totalAmount
	 */
	public long getTotalAmount() {
		return totalAmount;
	}
	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	//Eran
	public long incAvailableAmount() {
		return this.availableAmount++;
	}
	
	//Eran
	public long decAvailableAmount() {
		return this.availableAmount--;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Movie [id=" + id + ", name=" + name + ", price=" + price + ", bannedCountries=" + bannedCountries
				+ ", availableAmount=" + availableAmount + ", totalAmount=" + totalAmount + "] "+'\n';
	}
	//eran
	public String movieInfo() {
		return "\""+name + "\" "+ availableAmount +" "+ price + " " + getBannedCountriesByName() +'\n';
	}
	//eran
	public String getBannedCountriesByName(){ 
		String output = "";
		for(String country: bannedCountries) 
			output = (output+"\""+country + "\" "); 
		return output;
	}
}
