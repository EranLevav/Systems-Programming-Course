package bgu.spl181.net.impl.databases;

import java.util.ArrayList;
import java.util.HashMap;

public class MoviesDataBase {
	private transient HashMap <String, Movie> moviesByName;
	private transient long idCounter;
	private ArrayList <Movie> movies;
	/**
	 * @param moviesList
	 * 				List of Movies to be initialized from Json
	 */
	public MoviesDataBase(ArrayList <Movie> movies) {
		this.movies = movies;
	}
	/**
	 * invoked when database is constructed.
	 * adds the movies from the JSON file to the moviesByName HashMap
	 */
	public void init(){
		moviesByName= new HashMap <String, Movie> ();
		this.setIdCounter(0);
		for(Movie movie: movies) {
			moviesByName.put(movie.getName(), movie);
			this.idCounter= Math.max(movie.getId(), idCounter);
		}
	}
	/**
	 * @return the movies
	 */
	public ArrayList<Movie> getMovies() {
		return movies;
	}
	public boolean contains(String movieName) {
		return moviesByName.containsKey(movieName);
	}
	
	public boolean remove(String movieName) {
		if(moviesByName.remove(movieName)!=null)
			return movies.removeIf((movie)-> movie.getName().equals(movieName));
		return false;
	}
	public Movie get(String movieName) {
		return moviesByName.get(movieName);
	}
	/**
	 * @param movie
	 * @return
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	public boolean add(Movie movie) {
		moviesByName.put(movie.getName(), movie);
		return movies.add(movie);
	}
	//eran
	public String getMoviesName(){
		String output = "";
		for(Movie movie: movies) 
			output = (output+"\""+movie.getName() + "\" "); 
		return output;
	}
	/**
	 * @return the idCounter
	 */
	public long getIdCounter() {
		return idCounter;
	}
	/**
	 * @param idCounter the idCounter to set
	 */
	public void setIdCounter(long idCounter) {
		this.idCounter = idCounter;
	}
	/**
	 * @param idCounter the idCounter to set
	 */
	public long nextMovieId() {
		return ++this.idCounter;
	}
	
}
