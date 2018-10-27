package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.impl.databases.DataBase;
import bgu.spl181.net.impl.protocols.LineMessageEncoderDecoder;
import bgu.spl181.net.impl.protocols.MovieRentalProtocol;
import bgu.spl181.net.srv.Server;


public class TPCMain {

	public static void main(String[] args) {
		/*
		 * The data shared by all the clients in the movie rental service. 
		 * Contains a movies database and a users database.
		 * Initially parsed from json files.
		 */
		DataBase sharedDB= new DataBase("Database/Movies.json", "Database/Users.json"); 
		
		int port=Integer.parseInt(args[0]);
	        Server.threadPerClient(
	                port,
	                () -> new MovieRentalProtocol(sharedDB), //protocol factory
	                LineMessageEncoderDecoder::new 			//message encoder decoder factory
	        ).serve();

	}

}
