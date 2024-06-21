package Hotel;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.sql.*;
import java.io.IOException;

public class Main {
	private static final String ADMIN = "admin";
	private static final String PASSADMIN = "admin";
	private static String UTENTE = "";
	private static String C_UTENTE ="";
	private static int ID_UTENTE = 0;
	private static boolean ACCESSO_UTENTE = false; 
	//Definizione degli attributi del progetto
	private static final String DB_URL_CREATE = "inserisci qui l'url di MySQL";
	private static final String DB_URL = "inserisci qui l'url di MySQL/gestione_hotel";
	private static final String DB_USERNAME = "inserisci qui l'username di MySQL";
	private static final String DB_PASSWORD = "inserisci qui la password di MySQL";
	
	//definizione delle query SQL per la creazione del database
	private static final String CREATE_DB_QUERY = "CREATE DATABASE IF NOT EXISTS gestione_hotel";
	
	private static final String CREATE_TABLE_CAMERE_QUERY = "CREATE TABLE IF NOT EXISTS camere("
												   		  + "id_camere INT AUTO_INCREMENT PRIMARY KEY,"
												   		  + "nome_camera VARCHAR(50), "
												   		  + "tipo VARCHAR(50), "
												   		  + "prezzo DECIMAL(10,2),"
												   		  + "servizi VARCHAR(150)"
												   		  + ")";
	
	private static final String CREATE_TABLE_ACCOUNT_QUERY = "CREATE TABLE IF NOT EXISTS account("
	   		  											  + "id_account INT AUTO_INCREMENT PRIMARY KEY,"
	   		  											  + "username VARCHAR(50),"
	   		  											  + "password VARCHAR(50),"
	   		  											  + "nome VARCHAR(50),"
	   		  											  + "cognome VARCHAR(50),"
	   		  											  + "email VARCHAR(50)"
	   		  											  + ")";
	
	private static final String CREATE_TABLE_RECENSIONI_QUERY = "CREATE TABLE IF NOT EXISTS recensioni("
															   + "id_recensione INT AUTO_INCREMENT PRIMARY KEY,"
															   + "periodo VARCHAR(100),"
															   + "recensione TEXT,"
															   + "id_account INT"
															   + ")";
	
	private static final String CREATE_TABLE_PRENOTAZIONI_QUERY = "CREATE TABLE IF NOT EXISTS prenotazioni("
																+ "id_prenotazioni INT AUTO_INCREMENT PRIMARY KEY,"
																+ "check_in DATE NOT NULL,"
																+ "check_out DATE NOT NULL,"
																+ "n_persone INT,"
																+ "id_camera INT,"
																+ "id_account INT"
																+ ")";

	
	public static void main (String[] args) throws IOException{
		//creazione db e tabelle
		createDatabaseIfNotExists();
		createTableIfNotExists();
		
		
		//creazione del server
		HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);
		//creazione degli Handler
		server.createContext("/", new HomePageHandler());
		server.createContext("/registrazione", new RegistrazioneHandler());
		server.createContext("/recensioniHotel", new RecensioniHotelHandler());
		server.createContext("/userRecensioniHotel", new UserRecensioniHotelHandler());
		server.createContext("/adminRecensioniHotel", new AdminRecensioniHotelHandler());
		server.createContext("/adminPage", new AdminPageHandler());
		server.createContext("/userPage", new UserPageHandler());
		server.createContext("/loginErrato", new LoginErratoHandler());
		server.createContext("/noteSviluppatore", new NoteSviluppatoreHandler());
		server.createContext("/userNoteSviluppatore", new UserNoteSviluppatoreHandler());
		server.createContext("/adminNoteSviluppatore", new AdminNoteSviluppatoreHandler());
		server.createContext("/adminInserisciCamera", new InserisciCameraHandler());
		server.createContext("/adminModificaCamera", new ModificaCameraHandler());
		server.createContext("/adminEliminaCamera", new EliminaCameraHandler());
		server.createContext("/adminStoricoPrenotazioni", new StoricoPrenotazioniHandler());
		server.createContext("/adminModificaPrenotazione", new AdminModificaPrenotazioneHandler());
		server.createContext("/adminEliminaPrenotazione", new AdminEliminaPrenotazioneHandler());
		server.createContext("/userPrenotaCamera", new UserPrenotaCameraHandler());
		server.createContext("/userModificaPrenotazione", new UserModificaPrenotazione());
		server.createContext("/userEliminaPrenotazione", new UserEliminaPrenotazione());
		server.createContext("/userModificaPassword", new ModificaPasswordHandler());
		server.createContext("/userInserisciRecensione", new UserInserisciRecensioneHandler());
		server.start();
		System.out.println("Server in escuzione sulla porta 8080");


}
	
	//Homepage Iniziale
	static class HomePageHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				ACCESSO_UTENTE = false;
				//form html 
				String htmlResponse = "<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+ "    <nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
						+ "        <div class='container-fluid'>"
						+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
						+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
						+ "                <span class='navbar-toggler-icon'></span>"
						+ "            </button>"
						+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
						+ "                <ul class='navbar-nav'>"
						+ "                    <li class='nav-item active'>"
						+ "                        <a class='nav-link' href='#'>Home</a>"
						+ "                    </li>"
						+ "                    <li class='nav-item dropdown'>\r\n"
						+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
						+ "                            Pannello Utente"
						+ "                        </a>"
						+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
						+ "                                <p class='dropdown-item'>Accedi Prima</p>"
						+ "                        </div>\r\n"
						+ "                    </li>\r\n"
						+ "                    <li class='nav-item'>\r\n"
						+ "                        <a class='nav-link' href='/recensioniHotel'>Recensioni</a>"
						+ "                    </li>\r\n"
						+ "                    <li class='nav-item'>\r\n"
						+ "                        <a class='nav-link' href='/noteSviluppatore'>Note Sviluppatore</a>"
						+ "                    </li>\r\n"
						+ "                </ul>"
						+ "            </div>"
						+ "        </div>"
						+ "    </nav>"
						+ "    <section>"
						+ "        <div class='container'>"
						+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
						+ "            <div class='row'>"
						+ "                <div class='col-sm-3'>"
						+ "                    <h2>Area Riservata</h2>"
						+ "                    <form method='post' action='#'>"
						+ "                        <div class='form-group'>"
						+ "                            <label for='username'>Username:</label>"
						+ "                            <input type='text' class='form-control' id='username' name='username' required/>"
						+ "                        </div>"
						+ "                        <div class='form-group'>"
						+ "                            <label for='password'>Password:</label>"
						+ "                            <input type='password' class='form-control' id='password' name='password' required/>"
						+ "                        </div>"
						+ "                        <br />"
						+ "                        <button type='submit' class='btn btn-success'>Accedi</button>"
						+ "                    </form>"
						+ "                    <hr />"
						+ "                    <div id='messaggio'>"
						+ "                        Non sei ancora Registrato? <a href='/registrazione'>Registrati</a>!"
						+ "                    </div>"
						+ "                </div>"
						+ "                <div class='col-sm-9'>"
						+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
						+ "                        <div class='carousel-inner'>\r\n"
						+ "                            <div class='carousel-item active'>\r\n"
						+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel01.jpg?raw=true' class='d-block w-100 rounded-3' alt='Prima Immagine' />"
						+ "                            </div>\r\n"
						+ "                            <div class='carousel-item'>\r\n"
						+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel02.jpg?raw=true' class='d-block w-100 rounded-3' alt='Seconda Immagine' />"
						+ "                            </div>\r\n"
						+ "                            <div class='carousel-item'>\r\n"
						+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel03.jpg?raw=true' class='d-block w-100 rounded-3' alt='Terza Immagine' />"
						+ "                            </div>"
						+ "                        </div>"
						+ "                        <button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
						+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
						+ "                            <span class='visually-hidden'>Previous</span>"
						+ "                        </button>\r\n"
						+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
						+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
						+ "                            <span class='visually-hidden'>Next</span>"
						+ "                        </button>"
						+ "                    </div>"
						+ "                </div>"
						+ "            </div>"
						+ "        </div>"
						+ "    </section>"
						+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
						+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
						+ "            <div class='container-fluid'>"
						+ "                <p>\r\n"
						+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
						+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
						+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
						+ "                </p>"
						+ "            </div>"
						+ "        </nav>"
						+ "</body>"
						+ "</html>";
									
				//gestiamo risposta al browser GET iniziale
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = htmlResponse.getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(htmlResponse.getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//Split della riga nelle sue componenti di base
				String[] formDataArray = formData.split("&");
				//recuperiamo i singoli valori
				String username = formDataArray[0].split("=")[1];
				String password = formDataArray[1].split("=")[1];
				
				if (username.equals(ADMIN) && password.equals(PASSADMIN)) {
					exchange.getResponseHeaders().set("Location","/adminPage");
					exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser
					System.out.println("condizione admin ok");
				} else {
					System.out.println("condizione admin no");
				//connessione al database
				try {
					//autorizzazione al database
					Connection conn = DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);
					System.out.println("Connessione effettuata con successo!");
					
					//esecuzione query visualizzazione dati
					Statement stmtVisualizzazione = conn.createStatement();
					//libreria per contenere e gestire i record estratti
					ResultSet rsVisualizzazione = stmtVisualizzazione.executeQuery("SELECT * FROM account");
					List<String> accounts = new ArrayList<>();
					//iterazione sul ResultSet
					while(rsVisualizzazione.next()) {
						int idRC = rsVisualizzazione.getInt("id_account");
						String usernameRC = rsVisualizzazione.getString("username");
						String passwordRC = rsVisualizzazione.getString("password");
						String nomeUtente = rsVisualizzazione.getString("nome");
						String cognomeUtente = rsVisualizzazione.getString("cognome");
						accounts.add(idRC+"&"+usernameRC+"&"+passwordRC+"&"+nomeUtente+"&"+cognomeUtente);	
						//System.out.println(accounts);
						
						if (usernameRC.equals(username) && passwordRC.equals(password)) {
							ACCESSO_UTENTE = true;
							ID_UTENTE = idRC;
							UTENTE = nomeUtente;
							C_UTENTE = cognomeUtente;
							System.out.println("trovato");
						} else {System.out.println("non trovato");}
						}
					
					
					
					//chiusura connessioni
					rsVisualizzazione.close();
					stmtVisualizzazione.close();
					conn.close();
				
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				}
				if (ACCESSO_UTENTE == true) {
				//Dopo l'inserimento del biglietto, rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/userPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser
				} else {
					exchange.getResponseHeaders().set("Location","/loginErrato");
					exchange.sendResponseHeaders(302, -1);
					
				}
				
			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
			
			
		}
	
	//Hompage dell'Admin
	static class AdminPageHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			//creazione del documento web per la home page dell'applicativo
			String htmlResponse = "<!DOCTYPE html>"
					+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
					+ "<head>"
					+ "    <meta charset='utf-8' />"
					+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
					+ "    <meta author='Mattia Bascelli'/>"
					+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
					+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
					+ "    <title>GESTIONE HOTEL  - v0.1</title>"
					+ "</head>"
					+ "<body>"
					+ "    <nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
					+ "        <div class='container-fluid'>"
					+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
					+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
					+ "                <span class='navbar-toggler-icon'></span>"
					+ "            </button>"
					+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
					+ "                <ul class='navbar-nav'>"
					+ "                    <li class='nav-item active'>"
					+ "                        <a class='nav-link' href='/adminPage'>Home</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item dropdown'>"
					+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
					+ "                            Pannello Admin"
					+ "                        </a>"
					+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
					+ "                                <a href=/adminInserisciCamera' class='dropdown-item'>Inserisci Camera</a>"
					+ "								   <a href='/adminModificaCamera' class='dropdown-item'>Modifica Camera</a>"
					+ "                                <a href='/adminEliminaCamera' class='dropdown-item'>Elimina Camera</a>"
					+ "								   <a href='/adminStoricoPrenotazioni' class='dropdown-item'>Storico Prenotazioni</a>"
					+ "                                <a href='/adminModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
					+ "								   <a href='/adminEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
					+ "                                <div class='dropdown-divider'></div>"
					+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
					+ "                        </div>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/adminRecensioniHotel'>Recensioni</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/adminNoteSviluppatore'>Note Sviluppatore</a>"
					+ "                    </li>"
					+ "                </ul>"
					+ "            </div>"
					+ "        </div>"
					+ "    </nav>"
					+ "    <section>"
					+ "        <div class='container'>"
					+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
					+ "            <div class='row'>"
					+ "                <div class='col-sm-4'>"
					+ "                    <h2>Area Riservata Amministratore</h2>"
					+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
					+ "						  Cosa vuoi fare?</p>"
					+ "<div class='d-grid gap-2' >"
					+ "<a href='/adminInserisciCamera' class='btn btn-success'>Inserisci camera</a>"
					+ "<a href='/adminModificaCamera' class='btn btn-success'>Modifica camera</a>"
					+ "<a href='/adminEliminaCamera' class='btn btn-success'>Elimina camera</a>"
					+ "<a href='/adminStoricoPrenotazioni' class='btn btn-success'>Storico Prenotazioni</a>"
					+ "<a href='/adminModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
					+ "<a href='/adminEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
					+ "</div>"
					+ "                </div>"
					+ "                <div class='col-sm-8'>"
					+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
					+ "                        <div class='carousel-inner'>\r\n"
					+ "                            <div class='carousel-item active'>"
					+ "                                <div>"
					+ "<h3>INFORMAZIONI</h3>"
					+ "<p>Ricordati di non condividere la tua password di amministratore!<br />Puoi cambiare la tua password contattando lo sviluppatore all'indirizzo email: info@bascellimattia.it</p>"
					+ " <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel07.jpg?raw=true' class='d-block w-100 rounded-3' alt='Programmatore' />"
					+ "</div>"
					+ "                            </div>"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <div>"
					+ "<h3>TECNOLOGIE UTILIZZATE</h3>"
					+ "<p>Per lo sviluppo dell'applicazione sono state utilizzate tecnologie come HTML, BOOTSTRAP, JAVA e SQL.</p>	"
					+ "<img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel08.jpg?raw=true' class='d-block w-100 rounded-3' alt='Programmatore' />"
					+ "</div>"
					+ "                            </div>"
					+ "                            </div>"
					+ "                        <button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
					+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Previous</span>"
					+ "                        </button>"
					+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
					+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Next</span>"
					+ "                        </button>"
					+ "                    </div>"
					+ "                </div>"
					+ "            </div>"
					+ "        </div>"
					+ "    </section>"
					+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
					+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
					+ "            <div class='container-fluid'>"
					+ "                <p>\r\n"
					+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
					+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
					+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
					+ "                </p>"
					+ "            </div>"
					+ "        </nav>"
					+ "</body>"
					+ "</html>";
			
			//impostare la risposta con intestazione, status code e lunghezza
			exchange.getResponseHeaders().set("Content-Type",  "text/html");
			int contentlength = htmlResponse.getBytes("UTF-8").length;
			exchange.sendResponseHeaders(200, contentlength);
			//exchange.sendResponseHeaders(200, htmlResponse.length); <- in azienda si usa questo perchè il server dovrebbe esser sicuro
			//inviamo risposta al client
			OutputStream os = exchange.getResponseBody();
			os.write(htmlResponse.getBytes());//scriviamo il documento HTML come array di byte
			os.close();//chiudiamo l'os
			
		}
		
	}
	
	//Admin - Inserimento della Camera
	static class InserisciCameraHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				//form html per la registrazione Account
				String htmlResponse = "<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/adminPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Admin"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href=/adminInserisciCamera' class='dropdown-item'>Inserisci Camera</a>"
								+ "								   <a href='/adminModificaCamera' class='dropdown-item'>Modifica Camera</a>"
								+ "                                <a href='/adminEliminaCamera' class='dropdown-item'>Elimina Camera</a>"
								+ "								   <a href='/adminStoricoPrenotazioni' class='dropdown-item'>Storico Prenotazioni</a>"
								+ "                                <a href='/adminModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "								   <a href='/adminEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/adminInserisciCamera' class='btn btn-success'>Inserisci camera</a>"
								+ "<a href='/adminModificaCamera' class='btn btn-success'>Modifica camera</a>"
								+ "<a href='/adminEliminaCamera' class='btn btn-success'>Elimina camera</a>"
								+ "<a href='/adminStoricoPrenotazioni' class='btn btn-success'>Storico Prenotazioni</a>"
								+ "<a href='/adminModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/adminEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "</div>"
								+ "                </div>"
								+ "                <div class='col-sm-8'>"
								+ "					<form method='post' action='/adminInserisciCamera'>"
								+ "    <div class='form-group'>\r\n"
								+ "        <label for='nome'>Nome:</label>"
								+ "        <input type='text' class='form-control' id='nome' name='nome' required />"
								+ "    </div>"
								+ "    <div class='form-group'>\r\n"
								+ "        <label for='tipo'>Tipologia:</label>"
								+ "        <input type='text' class='form-control' id='tipo' name='tipo' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='prezzo'>Prezzo:</label>"
								+ "        <input type='number' class='form-control' id='prezzo' name='prezzo' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='servizi'>Servizi:</label>"
								+ "        <input type='text' class='form-control' id='servizi' name='servizi' required />"
								+ "    </div>"
								+ "    <br />"
								+ "    <button type='submit' class='btn btn-success'>Inserisci Camera</button>"
								+ "</form>"
								+ "                    <hr />"
								+ "                    <div id='messaggio'>"
								+ "<p>Controlla sempre di aver messo i dati in modo corretto prima di inserire la Camera.<br />Ovviamente non ti preoccupare, potrai sempre modificarlo in seguito!</p>"
								+ "                    </div>"
								+ "        </div>"
								+ "    </section>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>\r\n"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
							
								+ "</body>"
								+ "</html>";
									
				//gestiamo risposta al browser GET iniziale
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = htmlResponse.getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(htmlResponse.getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//Split della riga nelle sue componenti di base
				String[] formDataArray = formData.split("&");
				//recuperiamo i singoli valori
				String nome = formDataArray[0].split("=")[1];
				String tipo = formDataArray[1].split("=")[1];
				Double prezzo = Double.parseDouble(formDataArray[2].split("=")[1]);
				String xservizi = formDataArray[3].split("=")[1];	
				String servizi = xservizi.replace("+", " ");
				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "INSERT INTO camere(nome_camera, tipo, prezzo, servizi) VALUES (?, ?, ?, ?)";
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setString(1, nome);
						pstmt.setString(2, tipo);
						pstmt.setDouble(3, prezzo);
						pstmt.setString(4, servizi);

						
						pstmt.executeUpdate();	
						System.out.println("Dato inserito correttamente");
						pstmt.close();
						conn.close();
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/adminPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
			
			
			
			
		}
	
	//Admin - Modifica della Camera
	static class ModificaCameraHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				//connessione al database
				List<String> camere = new ArrayList<>();
				try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "SELECT * FROM camere";
					try(PreparedStatement pstmt = conn.prepareStatement(query)){
						ResultSet rs = pstmt.executeQuery();
						while(rs.next()) {
							int id_camera = rs.getInt("id_camere");
							String nome = rs.getString("nome_camera");
							String tipo = rs.getString("tipo");
							double prezzo = rs.getDouble("prezzo");
							String servizi = rs.getString("servizi");
							camere.add(id_camera+"&"+nome+"&"+tipo+"&"+prezzo+"&"+servizi);

						}
						rs.close();
						pstmt.close();
						conn.close();
						
						
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//form html per la registrazione Account
				StringBuilder response = new StringBuilder();
				response.append("<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/adminPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Admin"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href=/adminInserisciCamera' class='dropdown-item'>Inserisci Camera</a>"
								+ "								   <a href='/adminModificaCamera' class='dropdown-item'>Modifica Camera</a>"
								+ "                                <a href='/adminEliminaCamera' class='dropdown-item'>Elimina Camera</a>"
								+ "								   <a href='/adminStoricoPrenotazioni' class='dropdown-item'>Storico Prenotazioni</a>"
								+ "                                <a href='/adminModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "								   <a href='/adminEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/adminInserisciCamera' class='btn btn-success'>Inserisci camera</a>"
								+ "<a href='/adminModificaCamera' class='btn btn-success'>Modifica camera</a>"
								+ "<a href='/adminEliminaCamera' class='btn btn-success'>Elimina camera</a>"
								+ "<a href='/adminStoricoPrenotazioni' class='btn btn-success'>Storico Prenotazioni</a>"
								+ "<a href='/adminModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/adminEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "</div>"
								+ "                </div>"
								+ "                <div class='col-sm-8'>"
								+ "					<form method='post' action='/adminModificaCamera'>"
								+ "    <div class='form-group'>"
								+ "        <label for='nome'>ID:</label>"
								+ "        <input type='text' class='form-control' id='nome' name='nome' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='nome'>Nome:</label>"
								+ "        <input type='text' class='form-control' id='nome' name='nome' required />"
								+ "    </div>"
								+ "    <div class='form-group'>\r\n"
								+ "        <label for='tipo'>Tipologia:</label>"
								+ "        <input type='text' class='form-control' id='tipo' name='tipo' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='prezzo'>Prezzo:</label>"
								+ "        <input type='number' class='form-control' id='prezzo' name='prezzo' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='servizi'>Servizi:</label>"
								+ "        <input type='text' class='form-control' id='servizi' name='servizi' required />"
								+ "    </div>"
								+ "    <br />"
								+ "    <button type='submit' class='btn btn-success'>Modifica Camera</button>"
								+ "</form>"
								+ "                    <hr />"
								+ "                    <div id='messaggio'>"
								+ "<p>Controlla sempre di aver messo i dati in modo corretto prima di inserire la Camera.<br />Ovviamente non ti preoccupare, potrai sempre modificarlo in seguito!</p>"
								+ "                    </div>"
								+ "<div class='table-responsive'>"
								+"<table class='table table-bordered table-success'>"
								+"<thead>"
								+"<tr>"
								+"<th>id</th>"
								+"<th>Nome</th>"
								+"<th>Tipologia</th>"
								+"<th>prezzo</th>"
								+"<th>Servizi</th>"
								+"</tr>"
								+"</thead>"
								+"<tbody class='table table-bordered table-striped'>");
				//creare dinameicamente il contenuto
				for(String camera : camere) {
					String[] cameraX = camera.split("&");
					response.append("<tr class='table-light'><td>").append(cameraX[0]).append("</td>");
					response.append("<td>").append(cameraX[1]).append("</td>");
					response.append("<td>").append(cameraX[2]).append("</td>");
					response.append("<td>").append(cameraX[3]).append("</td>");
					response.append("<td>").append(cameraX[4]).append("</td></tr>");
					}
					response.append("</tbody>"
								+"</table>"
			    				+ "</div>"
								+ "        </div>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>\r\n"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
								+ "</body>"
								+ "</html>");
									
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = response.toString().getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//Split della riga nelle sue componenti di base
				String[] formDataArray = formData.split("&");
				//recuperiamo i singoli valori
				int id = Integer.parseInt(formDataArray[0].split("=")[1]);
				String nome = formDataArray[1].split("=")[1];
				String tipo = formDataArray[2].split("=")[1];
				Double prezzo = Double.parseDouble(formDataArray[3].split("=")[1]);
				String xservizi = formDataArray[4].split("=")[1];
				String servizi = xservizi.replace("+", " ");

				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "UPDATE camere SET nome_camera = ?, tipo = ?, prezzo = ?, servizi = ? WHERE id_camere = ?";
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setString(1, nome);
						pstmt.setString(2, tipo);
						pstmt.setDouble(3, prezzo);
						pstmt.setString(4, servizi);
						pstmt.setInt(5, id);

						
						pstmt.executeUpdate();	
						System.out.println("Dato inserito correttamente");
						pstmt.close();
						conn.close();
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/adminPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
			
			
			
			
		}
	
	//Admin - Storico Prenotazioni
	static class StoricoPrenotazioniHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			
				
				//connessione al database
				List<String> prenotazioni = new ArrayList<>();
				try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "SELECT prenotazioni.id_prenotazioni, prenotazioni.check_in, prenotazioni.check_out, prenotazioni.n_persone, camere.nome_camera, account.nome, account.cognome FROM prenotazioni "
								 + "JOIN camere ON prenotazioni.id_camera = camere.id_camere "
								 + "JOIN account ON prenotazioni.id_account = account.id_account ";
					try(PreparedStatement pstmt = conn.prepareStatement(query)){
						ResultSet rs = pstmt.executeQuery();
						while(rs.next()) {
							int id_prenotazione= rs.getInt("prenotazioni.id_prenotazioni");
							String checkIn = rs.getString("prenotazioni.check_in");
							String checkOut = rs.getString("prenotazioni.check_out");
							int nPersone = rs.getInt("prenotazioni.n_persone");
							String nomeCamera = rs.getString("camere.nome_camera");
							String nome = rs.getString("account.nome");	
							String cognome = rs.getString("account.cognome");	
							prenotazioni.add(id_prenotazione+"&"+checkIn+"&"+checkOut+"&"+nPersone+"&"+nomeCamera+"&"+nome+"&"+cognome);

						}
						rs.close();
						pstmt.close();
						conn.close();
						
						
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//form html per la registrazione Account
				StringBuilder response = new StringBuilder();
				response.append("<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/adminPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Admin"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href=/adminInserisciCamera' class='dropdown-item'>Inserisci Camera</a>"
								+ "								   <a href='/adminModificaCamera' class='dropdown-item'>Modifica Camera</a>"
								+ "                                <a href='/adminEliminaCamera' class='dropdown-item'>Elimina Camera</a>"
								+ "								   <a href='/adminStoricoPrenotazioni' class='dropdown-item'>Storico Prenotazioni</a>"
								+ "                                <a href='/adminModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "								   <a href='/adminEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/adminInserisciCamera' class='btn btn-success'>Inserisci camera</a>"
								+ "<a href='/adminModificaCamera' class='btn btn-success'>Modifica camera</a>"
								+ "<a href='/adminEliminaCamera' class='btn btn-success'>Elimina camera</a>"
								+ "<a href='/adminStoricoPrenotazioni' class='btn btn-success'>Storico Prenotazioni</a>"
								+ "<a href='/adminModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/adminEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "</div>"
								+ "</div>"
								+ "<div class='col-sm-8'>"
								+ "<div class='table-responsive'>"
								+"<table class='table table-bordered table-success'>"
								+"<thead>"
								+"<tr>"
								+"<th>id</th>"
								+"<th>Check-In</th>"
								+"<th>Check-Out</th>"
								+"<th>Persone</th>"
								+"<th>Stanza</th>"
								+"<th>Nome</th>"
								+"<th>Cognome</th>"
								+"</tr>"
								+"</thead>"
								+"<tbody class='table table-bordered table-striped'>");
				//creare dinameicamente il contenuto
				for(String prenotazione : prenotazioni) {
					String[] cameraX = prenotazione.split("&");
					response.append("<tr class='table-light'><td>").append(cameraX[0]).append("</td>");
					response.append("<td>").append(cameraX[1]).append("</td>");
					response.append("<td>").append(cameraX[2]).append("</td>");
					response.append("<td>").append(cameraX[3]).append("</td>");
					response.append("<td>").append(cameraX[4]).append("</td>");
					response.append("<td>").append(cameraX[5]).append("</td>");
					response.append("<td>").append(cameraX[6]).append("</td></tr>");
					}
					response.append("</tbody>"
								+"</table>"
			    				+ "</div>"
								+ "        </div>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>\r\n"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
							
								+ "</body>"
								+ "</html>");
									
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = response.toString().getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			
		}
		
			
			
			
			
		}
	
	//Admin - Modifica Prenotazioni
	static class AdminModificaPrenotazioneHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				//connessione al database
				List<String> prenotazioni = new ArrayList<>();
				try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "SELECT prenotazioni.id_prenotazioni, prenotazioni.check_in, prenotazioni.check_out, prenotazioni.n_persone, prenotazioni.id_account, prenotazioni.id_camera, camere.nome_camera, account.nome, account.cognome FROM prenotazioni "
								 + "JOIN camere ON prenotazioni.id_camera = camere.id_camere "
								 + "JOIN account ON prenotazioni.id_account = account.id_account ";
					try(PreparedStatement pstmt = conn.prepareStatement(query)){
						ResultSet rs = pstmt.executeQuery();
						while(rs.next()) {
							int id_prenotazione= rs.getInt("prenotazioni.id_prenotazioni");
							String checkIn = rs.getString("prenotazioni.check_in");
							String checkOut = rs.getString("prenotazioni.check_out");
							int nPersone = rs.getInt("prenotazioni.n_persone");
							int idAcco = rs.getInt("prenotazioni.id_account");
							int idCam = rs.getInt("prenotazioni.id_camera");
							String nomeCamera = rs.getString("camere.nome_camera");
							String nome = rs.getString("account.nome");	
							String cognome = rs.getString("account.cognome");	
							prenotazioni.add(id_prenotazione+"&"+checkIn+"&"+checkOut+"&"+nPersone+"&"+idCam+"&"+nomeCamera+"&"+idAcco+"&"+nome+"&"+cognome);

						}
						rs.close();
						pstmt.close();
						conn.close();
						
						
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//form html per la registrazione Account
				StringBuilder response = new StringBuilder();
				response.append("<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/adminPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Admin"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href=/adminInserisciCamera' class='dropdown-item'>Inserisci Camera</a>"
								+ "								   <a href='/adminModificaCamera' class='dropdown-item'>Modifica Camera</a>"
								+ "                                <a href='/adminEliminaCamera' class='dropdown-item'>Elimina Camera</a>"
								+ "								   <a href='/adminStoricoPrenotazioni' class='dropdown-item'>Storico Prenotazioni</a>"
								+ "                                <a href='/adminModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "								   <a href='/adminEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/adminInserisciCamera' class='btn btn-success'>Inserisci camera</a>"
								+ "<a href='/adminModificaCamera' class='btn btn-success'>Modifica camera</a>"
								+ "<a href='/adminEliminaCamera' class='btn btn-success'>Elimina camera</a>"
								+ "<a href='/adminStoricoPrenotazioni' class='btn btn-success'>Storico Prenotazioni</a>"
								+ "<a href='/adminModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/adminEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "</div>"
								+ "</div>"
								+ "<div class='col-sm-8'>"
								+ "					<form method='post' action='/adminModificaPrenotazione'>"
								+ "    <div class='form-group'>"
								+ "        <label for='id'>ID:</label>"
								+ "        <input type='text' class='form-control' id='id' name='id' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='check_in'>Check-in:</label>"
								+ "        <input type='date' class='form-control' id='check_in' name='check_in' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='check_out'>Check-out:</label>"
								+ "        <input type='date' class='form-control' id='check_out' name='check_out' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='n_persone'>n° Persone:</label>"
								+ "        <input type='number' class='form-control' id='n_persone' name='n_persone' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='id_camera'>Id Camera:</label>"
								+ "        <input type='number' class='form-control' id='id_camera' name='id_camera' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='id_account'>Id Account:</label>"
								+ "        <input type='number' class='form-control' id='id_account' name='id_account' required />"
								+ "    </div>"
								+ "    <br />"
								+ "    <button type='submit' class='btn btn-success'>Modifica Prenotazione</button>"
								+ "</form>"
								+ "                    <hr />"
								+ "                    <div id='messaggio'>"
								+ "<p>Controlla sempre di aver messo i dati in modo corretto prima di modificare.<br />Ovviamente non ti preoccupare, potrai sempre sistemarlo in seguito!</p>"
								+ "                    </div>"
								
								+ "<div class='table-responsive'>"
								+"<table class='table table-bordered table-success'>"
								+"<thead>"
								+"<tr>"
								+"<th>id</th>"
								+"<th>Check-In</th>"
								+"<th>Check-Out</th>"
								+"<th>NPers</th>"
								+"<th>IdCamera</th>"
								+"<th>Stanza</th>"
								+"<th>IdAccount</th>"
								+"<th>Nome</th>"
								+"<th>Cognome</th>"
								+"</tr>"
								+"</thead>"
								+"<tbody class='table table-bordered table-striped'>");
				//creare dinameicamente il contenuto
				for(String prenotazione : prenotazioni) {
					String[] cameraX = prenotazione.split("&");
					response.append("<tr class='table-light'><td>").append(cameraX[0]).append("</td>");
					response.append("<td>").append(cameraX[1]).append("</td>");
					response.append("<td>").append(cameraX[2]).append("</td>");
					response.append("<td>").append(cameraX[3]).append("</td>");
					response.append("<td>").append(cameraX[4]).append("</td>");
					response.append("<td>").append(cameraX[5]).append("</td>");
					response.append("<td>").append(cameraX[6]).append("</td>");
					response.append("<td>").append(cameraX[7]).append("</td>");
					response.append("<td>").append(cameraX[8]).append("</td></tr>");
					}
					response.append("</tbody>"
								+"</table>"
			    				+ "</div>"
								+ "        </div>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>\r\n"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
								+ "</body>"
								+ "</html>");
									
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = response.toString().getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//Split della riga nelle sue componenti di base
				String[] formDataArray = formData.split("&");
				//recuperiamo i singoli valori
				int id_prenotazione = Integer.parseInt(formDataArray[0].split("=")[1]);
				String checkIn = formDataArray[1].split("=")[1];
				String checkOut = formDataArray[2].split("=")[1];
				int nPersone = Integer.parseInt(formDataArray[3].split("=")[1]);
				int id_camera = Integer.parseInt(formDataArray[4].split("=")[1]);
				int id_account = Integer.parseInt(formDataArray[5].split("=")[1]);
				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "UPDATE prenotazioni SET check_in = ?, check_out = ?, n_persone = ?, id_camera = ?, id_account = ? WHERE id_prenotazioni = ?";
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setString(1, checkIn);
						pstmt.setString(2, checkOut);
						pstmt.setInt(3, nPersone);
						pstmt.setInt(4, id_camera);
						pstmt.setInt(5, id_account);
						pstmt.setInt(6, id_prenotazione);

						
						pstmt.executeUpdate();	
						System.out.println("Dato inserito correttamente");
						pstmt.close();
						conn.close();
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/adminPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
			
			
			
			
		}
	
	//Admin - Elimina Prenotazioni
	static class AdminEliminaPrenotazioneHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				//connessione al database
				List<String> prenotazioni = new ArrayList<>();
				try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "SELECT prenotazioni.id_prenotazioni, prenotazioni.check_in, prenotazioni.check_out, prenotazioni.n_persone, camere.nome_camera, account.nome, account.cognome FROM prenotazioni "
								 + "JOIN camere ON prenotazioni.id_camera = camere.id_camere "
								 + "JOIN account ON prenotazioni.id_account = account.id_account ";
					try(PreparedStatement pstmt = conn.prepareStatement(query)){
						ResultSet rs = pstmt.executeQuery();
						while(rs.next()) {
							int id_prenotazione= rs.getInt("prenotazioni.id_prenotazioni");
							String checkIn = rs.getString("prenotazioni.check_in");
							String checkOut = rs.getString("prenotazioni.check_out");
							int nPersone = rs.getInt("prenotazioni.n_persone");
							String nomeCamera = rs.getString("camere.nome_camera");
							String nome = rs.getString("account.nome");	
							String cognome = rs.getString("account.cognome");	
							prenotazioni.add(id_prenotazione+"&"+checkIn+"&"+checkOut+"&"+nPersone+"&"+nomeCamera+"&"+nome+"&"+cognome);

						}
						rs.close();
						pstmt.close();
						conn.close();
						
						
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//form html per la registrazione Account
				StringBuilder response = new StringBuilder();
				response.append("<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/adminPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Admin"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href=/adminInserisciCamera' class='dropdown-item'>Inserisci Camera</a>"
								+ "								   <a href='/adminModificaCamera' class='dropdown-item'>Modifica Camera</a>"
								+ "                                <a href='/adminEliminaCamera' class='dropdown-item'>Elimina Camera</a>"
								+ "								   <a href='/adminStoricoPrenotazioni' class='dropdown-item'>Storico Prenotazioni</a>"
								+ "                                <a href='/adminModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "								   <a href='/adminEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/adminInserisciCamera' class='btn btn-success'>Inserisci camera</a>"
								+ "<a href='/adminModificaCamera' class='btn btn-success'>Modifica camera</a>"
								+ "<a href='/adminEliminaCamera' class='btn btn-success'>Elimina camera</a>"
								+ "<a href='/adminStoricoPrenotazioni' class='btn btn-success'>Storico Prenotazioni</a>"
								+ "<a href='/adminModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/adminEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "</div>"
								+ "</div>"
								+ "<div class='col-sm-8'>"
								+ "					<form method='post' action='/adminEliminaPrenotazione'>"
								+ "    <div class='form-group'>"
								+ "        <label for='id'>ID:</label>"
								+ "        <input type='number' class='form-control' id='id' name='id' required />"
								+ "    </div>"
								+ "    <br />"
								+ "    <button type='submit' class='btn btn-danger'>Elimina Prenotazione</button>"
								+ "</form>"
								+ "                    <hr />"
								+ "                    <div id='messaggio'>"
								+ "<p>Ricorda che eliminare una prenotazione è un'operazione irreversibile<br />Ovviamente non ti preoccupare, potrai sempre ricaricarla in seguito!</p>"
								+ "                    </div>"
								
								+ "<div class='table-responsive'>"
								+"<table class='table table-bordered table-success'>"
								+"<thead>"
								+"<tr>"
								+"<th>id</th>"
								+"<th>Check-In</th>"
								+"<th>Check-Out</th>"
								+"<th>Persone</th>"
								+"<th>Stanza</th>"
								+"<th>Nome</th>"
								+"<th>Cognome</th>"
								+"</tr>"
								+"</thead>"
								+"<tbody class='table table-bordered table-striped'>");
				//creare dinameicamente il contenuto
				for(String prenotazione : prenotazioni) {
					String[] cameraX = prenotazione.split("&");
					response.append("<tr class='table-light'><td>").append(cameraX[0]).append("</td>");
					response.append("<td>").append(cameraX[1]).append("</td>");
					response.append("<td>").append(cameraX[2]).append("</td>");
					response.append("<td>").append(cameraX[3]).append("</td>");
					response.append("<td>").append(cameraX[4]).append("</td>");
					response.append("<td>").append(cameraX[5]).append("</td>");
					response.append("<td>").append(cameraX[6]).append("</td></tr>");
					}
					response.append("</tbody>"
								+"</table>"
			    				+ "</div>"
								+ "        </div>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>\r\n"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
								
								+ "</body>"
								+ "</html>");
									
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = response.toString().getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//recuperiamo il valore
				int id = Integer.parseInt(formData.split("=")[1]);
				System.out.println(id); 
				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "DELETE FROM prenotazioni WHERE id_prenotazioni = ?";
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setInt(1, id);
						pstmt.executeUpdate();	
						System.out.println("Dato eliminato correttamente");
						pstmt.close();
						conn.close();
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/adminPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
			
			
			
			
		}
	
	//Admin - Elimina Camera
	static class EliminaCameraHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				//connessione al database
				List<String> camere = new ArrayList<>();
				try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "SELECT * FROM camere";
					try(PreparedStatement pstmt = conn.prepareStatement(query)){
						ResultSet rs = pstmt.executeQuery();
						while(rs.next()) {
							int id_camera = rs.getInt("id_camere");
							String nome = rs.getString("nome_camera");
							String tipo = rs.getString("tipo");
							double prezzo = rs.getDouble("prezzo");
							String servizi = rs.getString("servizi");
							camere.add(id_camera+"&"+nome+"&"+tipo+"&"+prezzo+"&"+servizi);

						}
						rs.close();
						pstmt.close();
						conn.close();
						
						
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//form html per la registrazione Account
				StringBuilder response = new StringBuilder();
				response.append("<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/adminPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Admin"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href=/adminInserisciCamera' class='dropdown-item'>Inserisci Camera</a>"
								+ "								   <a href='/adminModificaCamera' class='dropdown-item'>Modifica Camera</a>"
								+ "                                <a href='/adminEliminaCamera' class='dropdown-item'>Elimina Camera</a>"
								+ "								   <a href='/adminStoricoPrenotazioni' class='dropdown-item'>Storico Prenotazioni</a>"
								+ "                                <a href='/adminModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "								   <a href='/adminEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/adminNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/adminInserisciCamera' class='btn btn-success'>Inserisci camera</a>"
								+ "<a href='/adminModificaCamera' class='btn btn-success'>Modifica camera</a>"
								+ "<a href='/adminEliminaCamera' class='btn btn-success'>Elimina camera</a>"
								+ "<a href='/adminStoricoPrenotazioni' class='btn btn-success'>Storico Prenotazioni</a>"
								+ "<a href='/adminModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/adminEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "</div>"
								+ "                </div>"
								+ "                <div class='col-sm-8'>"
								+ "					<form method='post' action='/adminEliminaCamera'>"
								+ "    <div class='form-group'>"
								+ "        <label for='id'>ID:</label>"
								+ "        <input type='number' class='form-control' id='id' name='id' required />"
								+ "    </div>"
								+ "    <br />"
								+ "    <button type='submit' class='btn btn-danger'>Elimina Camera</button>"
								+ "</form>"
								+ "                    <hr />"
								+ "                    <div id='messaggio'>"
								+ "<p>Ricorda che eliminare una Camera è un'operazione irreversibile<br />Ovviamente non ti preoccupare, potrai sempre ricaricarla in seguito!</p>"
								+ "                    </div>"
								+ "<div class='table-responsive'>"
								+"<table class='table table-bordered table-success'>"
								+"<thead>"
								+"<tr>"
								+"<th>id</th>"
								+"<th>Nome</th>"
								+"<th>Tipologia</th>"
								+"<th>prezzo</th>"
								+"<th>Servizi</th>"
								+"</tr>"
								+"</thead>"
								+"<tbody class='table table-bordered table-striped'>");
				//creare dinameicamente il contenuto
				for(String camera : camere) {
					String[] cameraX = camera.split("&");
					response.append("<tr class='table-light'><td>").append(cameraX[0]).append("</td>");
					response.append("<td>").append(cameraX[1]).append("</td>");
					response.append("<td>").append(cameraX[2]).append("</td>");
					response.append("<td>").append(cameraX[3]).append("</td>");
					response.append("<td>").append(cameraX[4]).append("</td></tr>");
					}
					response.append("</tbody>"
								+"</table>"
			    				+ "</div>"
								+ "        </div>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>\r\n"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
								
								+ "</body>"
								+ "</html>");
									
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = response.toString().getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//recuperiamo il valore
				int id = Integer.parseInt(formData.split("=")[1]);
				System.out.println(id); 
				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "DELETE FROM camere WHERE id_camere = ?";
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setInt(1, id);
						pstmt.executeUpdate();	
						System.out.println("Dato eliminato correttamente");
						pstmt.close();
						conn.close();
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/adminPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
	
		}
	
	//Homepage dell'Utente
	static class UserPageHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			//creazione del documento web per la home page dell'applicativo
			String htmlResponse = "<!DOCTYPE html>"
					+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
					+ "<head>"
					+ "    <meta charset='utf-8' />"
					+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
					+ "    <meta author='Mattia Bascelli'/>"
					+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
					+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
					+ "    <title>GESTIONE HOTEL  - v0.1</title>"
					+ "</head>"
					+ "<body>"
					+ "    <nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
					+ "        <div class='container-fluid'>"
					+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
					+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
					+ "                <span class='navbar-toggler-icon'></span>"
					+ "            </button>"
					+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
					+ "                <ul class='navbar-nav'>"
					+ "                    <li class='nav-item active'>"
					+ "                        <a class='nav-link' href='#'>Home</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item dropdown'>"
					+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
					+ "                            Pannello Utente"
					+ "                        </a>"
					+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
					+ "                                <a href='/userPrenotaCamera' class='dropdown-item'>Prenota Camera</a>"
					+ "								   <a href='/userModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
					+ "                                <a href='/userEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
					+ "								   <a href='/userModificaPassword' class='dropdown-item'>Modifica Password</a>"
					+ "                                <div class='dropdown-divider'></div>"
					+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
					+ "                        </div>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>\r\n"
					+ "                        <a class='nav-link' href='/userRecensioniHotel'>Recensioni</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>\r\n"
					+ "                        <a class='nav-link' href='/userNoteSviluppatore'>Note Sviluppatore</a>"
					+ "                    </li>\r\n"
					+ "                </ul>"
					+ "            </div>"
					+ "        </div>"
					+ "    </nav>"
					+ "    <section>"
					+ "        <div class='container'>"
					+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
					+ "            <div class='row'>"
					+ "                <div class='col-sm-4'>"
					+ "                    <h2>Area Riservata</h2>"
					+ "                    <p>Benvenuto <strong>" + UTENTE + "</strong> nella tua Area Riservata!<br />"
					+ "						  Cosa vuoi fare?</p>"
					+ "<div class='d-grid gap-2' >"
					+ "<a href='/userPrenotaCamera' class='btn btn-success'>Prenota Camera</a>"
					+ "<a href='/userModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
					+ "<a href='/userEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
					+ "<a href='/userModificaPassword' class='btn btn-success'>Modifica Password</a>"
					+ "<a href='/userInserisciRecensione' class='btn btn-success'>Inserisci Recensione</a>"
					+ "</div>"
					+ "                </div>"
					+ "                <div class='col-sm-8'>"
					+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
					+ "                        <div class='carousel-inner'>\r\n"
					+ "                            <div class='carousel-item active'>"
					+ "                                <div>"
					+"<img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel05.jpg?raw=true' class='d-block w-100 rounded-3' alt='Prima Immagine' />"
					+ "<h3>Scopri le nostre fantastiche Camere</h3>"
					+ "<p>Scopri i confort e i servizi delle nostre camere.<br />Per qualsiasi altra informazione non esitare a contattarci, la nostra reception è attiva 24h tutti i giorni.<br />TEL. <strong>0123/456789</strong></p>	"
					+ "</div>"
					+ "                            </div>"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <div>"
					+"<img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel04.jpg?raw=true' class='d-block w-100 rounded-3' alt='Prima Immagine' />"
					+ "<h3>Stanze per tutti i gusti!</h3>"
					+ "<p>Tante attività extra per i vostri weekend con gli amici, o serate romantiche per tutte le coppie!</p>	"
					+ "</div>"
					+ "                            </div>\r\n"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <div>"
					+"<img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel06.jpg?raw=true' class='d-block w-100 rounded-3' alt='Prima Immagine' />"
					+ "<h3>Scopri gli eventi vicini alla nostra Struttura!</h3>"
					+ "<p>Presso la nostra Reception potrete scoprire tutti gli eventi in zona e il modo più efficace per raggiungerlo!</p>	"
					+ "</div>"
					+ "                            </div>"
					+ "                        </div>"
					+ "                        <button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
					+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Previous</span>"
					+ "                        </button>\r\n"
					+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
					+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Next</span>"
					+ "                        </button>"
					+ "                    </div>"
					+ "                </div>"
					+ "            </div>"
					+ "        </div>"
					+ "    </section>"
					+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
					+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
					+ "            <div class='container-fluid'>"
					+ "                <p>\r\n"
					+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
					+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
					+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
					+ "                </p>"
					+ "            </div>"
					+ "        </nav>"
					
					+ "</body>"
					+ "</html>";
			
			//impostare la risposta con intestazione, status code e lunghezza
			exchange.getResponseHeaders().set("Content-Type",  "text/html");
			int contentlength = htmlResponse.getBytes("UTF-8").length;
			exchange.sendResponseHeaders(200, contentlength);
			//exchange.sendResponseHeaders(200, htmlResponse.length); <- in azienda si usa questo perchè il server dovrebbe esser sicuro
			//inviamo risposta al client
			OutputStream os = exchange.getResponseBody();
			os.write(htmlResponse.getBytes());//scriviamo il documento HTML come array di byte
			os.close();//chiudiamo l'os
			
		}
		
	}
	
	//Pagina registrazione Utente
	static class RegistrazioneHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				//form html per la registrazione Account
				String htmlResponse = "<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+ "    <nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
						+ "        <div class='container-fluid'>"
						+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
						+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
						+ "                <span class='navbar-toggler-icon'></span>"
						+ "            </button>"
						+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
						+ "                <ul class='navbar-nav'>"
						+ "                    <li class='nav-item active'>"
						+ "                        <a class='nav-link' href='/'>Home</a>"
						+ "                    </li>"
						+ "                    <li class='nav-item dropdown'>\r\n"
						+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
						+ "                            Pannello Utente"
						+ "                        </a>"
						+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
						+ "                                <p class='dropdown-item'>Accedi Prima</p>"
						+ "                        </div>"
						+ "                    </li>"
						+ "                    <li class='nav-item'>"
						+ "                        <a class='nav-link' href='#'>Recensioni</a>"
						+ "                    </li>\r\n"
						+ "                    <li class='nav-item'>"
						+ "                        <a class='nav-link' href='#'>Note Sviluppatore</a>"
						+ "                    </li>"
						+ "                </ul>"
						+ "            </div>"
						+ "        </div>"
						+ "    </nav>"
						+ "    <section>"
						+ "        <div class='container'>"
						+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
						+ "            <div class='row'>"
						+ "                <div class='col-sm-3'>"
						+ "                  <h2>Registrati</h2>"
						+ "					<form method='post' action='/registrazione'>"
						+ "    <div class='form-group'>\r\n"
						+ "        <label for='username'>Username:</label>"
						+ "        <input type='text' class='form-control' id='username' name='username' required />"
						+ "    </div>"
						+ "    <div class='form-group'>"
						+ "        <label for='password'>Password:</label>"
						+ "        <input type='text' class='form-control' id='password' name='password' required />"
						+ "    </div>"
						+ "    <div class='form-group'>"
						+ "        <label for='nome'>Nome:</label>"
						+ "        <input type='text' class='form-control' id='nome' name='nome' required />"
						+ "    </div>"
						+ "    <div class='form-group'>"
						+ "        <label for='cognome'>Cognome:</label>"
						+ "        <input type='text' class='form-control' id='cognome' name='cognome' required />"
						+ "    </div>"
						+ "    <div class='form-group'>"
						+ "        <label for='email'>Email:</label>"
						+ "        <input type='email' class='form-control' id='email' name='email' required />"
						+ "    </div>"
						+ "    <br />"
						+ "    <button type='submit' class='btn btn-success'>Registrati</button>"
						+ "</form>"
						+ "                    <hr />"
						+ "                    <div id='messaggio'>"
						+ "                    </div>"
						+ "                </div>"
						+ "                <div class='col-sm-9'>"
						+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
						+ "                        <div class='carousel-inner'>\r\n"
						+ "                            <div class='carousel-item active'>\r\n"
						+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel01.jpg?raw=true' class='d-block w-100 rounded-3' alt='Prima Immagine' />"
						+ "                            </div>\r\n"
						+ "                            <div class='carousel-item'>\r\n"
						+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel02.jpg?raw=true' class='d-block w-100 rounded-3' alt='Seconda Immagine' />"
						+ "                            </div>\r\n"
						+ "                            <div class='carousel-item'>\r\n"
						+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel03.jpg?raw=true' class='d-block w-100 rounded-3' alt='Terza Immagine' />"
						+ "                            </div>"
						+ "                        </div>"
						+ "                        <button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
						+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
						+ "                            <span class='visually-hidden'>Previous</span>"
						+ "                        </button>\r\n"
						+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
						+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
						+ "                            <span class='visually-hidden'>Next</span>"
						+ "                        </button>"
						+ "                    </div>"
						+ "                </div>"
						+ "            </div>"
						+ "        </div>"
						+ "    </section>"
						+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
						+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
						+ "            <div class='container-fluid'>"
						+ "                <p>\r\n"
						+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
						+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
						+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
						+ "                </p>"
						+ "            </div>"
						+ "        </nav>"
						
						+ "</body>"
						+ "</html>";
									
				//gestiamo risposta al browser GET iniziale
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = htmlResponse.getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(htmlResponse.getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//Split della riga nelle sue componenti di base
				String[] formDataArray = formData.split("&");
				//recuperiamo i singoli valori
				String username = formDataArray[0].split("=")[1];
				String password = formDataArray[1].split("=")[1];
				String nome = formDataArray[2].split("=")[1];	
				String cognome = formDataArray[3].split("=")[1];
				String xemail = formDataArray[4].split("=")[1];
				String email = xemail.replace("%40", "@");
				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "INSERT INTO account(username, password, nome, cognome, email) VALUES (?, ?, ?, ?, ?)";
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setString(1, username);
						pstmt.setString(2, password);
						pstmt.setString(3,  nome);
						pstmt.setString(4, cognome);
						pstmt.setString(5, email);
						
						pstmt.executeUpdate();	
						System.out.println("Dato inserito correttamente");
						pstmt.close();
						conn.close();
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
			
			
			
			
		}
	
	//Pagina di Login Errato
	static class LoginErratoHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			//creazione del documento web per la home page dell'applicativo
			String htmlResponse = "<!DOCTYPE html>"
					+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
					+ "<head>"
					+ "    <meta charset='utf-8' />"
					+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
					+ "    <meta author='Mattia Bascelli'/>"
					+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
					+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
					+ "    <title>GESTIONE HOTEL  - v0.1</title>"
					+ "</head>"
					+ "<body>"
					+ "    <nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
					+ "        <div class='container-fluid'>"
					+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
					+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
					+ "                <span class='navbar-toggler-icon'></span>"
					+ "            </button>"
					+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
					+ "                <ul class='navbar-nav'>"
					+ "                    <li class='nav-item active'>"
					+ "                        <a class='nav-link' href='#'>Home</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item dropdown'>\r\n"
					+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
					+ "                            Pannello Utente"
					+ "                        </a>"
					+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
					+ "                                <p class='dropdown-item'>Accedi Prima</p>"
					+ "                        </div>\r\n"
					+ "                    </li>\r\n"
					+ "                    <li class='nav-item'>\r\n"
					+ "                        <a class='nav-link' href='#'>Recensioni</a>"
					+ "                    </li>\r\n"
					+ "                    <li class='nav-item'>\r\n"
					+ "                        <a class='nav-link' href='#'>Note Sviluppatore</a>"
					+ "                    </li>\r\n"
					+ "                </ul>"
					+ "            </div>"
					+ "        </div>"
					+ "    </nav>"
					+ "    <section>"
					+ "        <div class='container'>"
					+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
					+ "            <div class='row'>"
					+ "                <div class='col-sm-3'>"
					+ "                    <h2>Area Riservata</h2>"
					+ "                   	<p>UTENTE E PASSWORD ERRATI<br /><a href='/'>Riprova</a></p>" 
					+ "                    <hr />"
					+ "                    <div id='messaggio'>"
					+ "                        Non sei ancora Registrato? <a href='/registrazione'>Registrati</a>!"
					+ "                    </div>"
					+ "                </div>"
					+ "                <div class='col-sm-9'>"
					+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
					+ "                        <div class='carousel-inner'>\r\n"
					+ "                            <div class='carousel-item active'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel01.jpg?raw=true' class='d-block w-100 rounded-3' alt='Prima Immagine' />"
					+ "                            </div>\r\n"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel02.jpg?raw=true' class='d-block w-100 rounded-3' alt='Seconda Immagine' />"
					+ "                            </div>\r\n"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel03.jpg?raw=true' class='d-block w-100 rounded-3' alt='Terza Immagine' />"
					+ "                            </div>"
					+ "                        </div>"
					+ "                        <button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
					+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Previous</span>"
					+ "                        </button>\r\n"
					+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
					+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Next</span>"
					+ "                        </button>"
					+ "                    </div>"
					+ "                </div>"
					+ "            </div>"
					+ "        </div>"
					+ "    </section>"
					+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
					+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
					+ "            <div class='container-fluid'>"
					+ "                <p>\r\n"
					+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
					+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
					+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
					+ "                </p>"
					+ "            </div>"
					+ "        </nav>"
					
					+ "</body>"
					+ "</html>";
			
			//impostare la risposta con intestazione, status code e lunghezza
			exchange.getResponseHeaders().set("Content-Type",  "text/html");
			int contentlength = htmlResponse.getBytes("UTF-8").length;
			exchange.sendResponseHeaders(200, contentlength);
			//exchange.sendResponseHeaders(200, htmlResponse.length); <- in azienda si usa questo perchè il server dovrebbe esser sicuro
			//inviamo risposta al client
			OutputStream os = exchange.getResponseBody();
			os.write(htmlResponse.getBytes());//scriviamo il documento HTML come array di byte
			os.close();//chiudiamo l'os
			
		}
		
	}
	
	//Admin - Pagina Recensioni
	static class AdminRecensioniHotelHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			
			//connessione al database
			List<String> recensioni = new ArrayList<>();
			try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
				String query = "SELECT recensioni.periodo, recensioni.recensione, account.nome, account.cognome FROM recensioni JOIN account ON recensioni.id_account = account.id_account";
				try(PreparedStatement pstmt = conn.prepareStatement(query)){
					ResultSet rs = pstmt.executeQuery();
					while(rs.next()) {
						String periodo = rs.getString("recensioni.periodo");
						String recensione = rs.getString("recensioni.recensione");
						String nome = rs.getString("account.nome");
						String cognome = rs.getString("account.cognome");
						recensioni.add(periodo+"&"+recensione+"&"+nome+"&"+cognome);

					}
					rs.close();
					pstmt.close();
					conn.close();
					
					
				}
				
			}catch(SQLException e){
				e.printStackTrace();
			}
			
			StringBuilder response = new StringBuilder();
			response.append("<!DOCTYPE html>"
					+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
					+ "<head>"
					+ "    <meta charset='utf-8' />"
					+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
					+ "    <meta author='Mattia Bascelli'/>"
					+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
					+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
					+ "    <title>GESTIONE HOTEL  - v0.1</title>"
					+ "</head>"
					+ "<body>"
					+ "    <nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
					+ "        <div class='container-fluid'>"
					+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
					+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
					+ "                <span class='navbar-toggler-icon'></span>"
					+ "            </button>"
					+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
					+ "                <ul class='navbar-nav'>"
					+ "                    <li class='nav-item active'>"
					+ "                        <a class='nav-link' href='/adminPage'>Home</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item dropdown'>"
					+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
					+ "                            Pannello Admin"
					+ "                        </a>"
					+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
					+ "                                <a href=/adminInserisciCamera' class='dropdown-item'>Inserisci Camera</a>"
					+ "								   <a href='/adminModificaCamera' class='dropdown-item'>Modifica Camera</a>"
					+ "                                <a href='/adminEliminaCamera' class='dropdown-item'>Elimina Camera</a>"
					+ "								   <a href='/adminStoricoPrenotazioni' class='dropdown-item'>Storico Prenotazioni</a>"
					+ "                                <a href='/adminModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
					+ "								   <a href='/adminEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
					+ "                                <div class='dropdown-divider'></div>"
					+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
					+ "                        </div>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/adminRecensioniHotel'>Recensioni</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/adminNoteSviluppatore'>Note Sviluppatore</a>"
					+ "                    </li>"
					+ "                </ul>"
					+ "            </div>"
					+ "        </div>"
					+ "    </nav>"
					+ "    <section>"
					+ "        <div class='container'>"
					+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
					+ "            <div class='row'>"
					+ "                <div class='col-sm-6'>"
					+ "                    <h2>Scopri il nostro HOTEL</h2>"
					+ "                    <p>Camere comprese di tutti i servizi, e tanto altro ancora</p>"
					+ "                    <hr />"
					+ "                    <div id='messaggio'>"
					+ "                        <p>Ascolta sempre i consigli dei tuoi clienti</p>"
					+ "                    </div>"
					+ "                </div>"
					+ "                <div class='col-sm-6'>"
					+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
					+ "						<div class='carousel-inner'>"
					+ "<div class='carousel-item active'>"
					+"<div class='card text-center'>"
					  +"<div class='card-header'>"
					    +"Dicono di Noi!"
					  +"</div>"
					  +"<div class='card-body'>"
					    +"<h5 class='card-title'>Scopri tutte le recensioni sulla nostra Struttura!</h5>"
					    +"<p class='card-text'>Accedi al tuo account per lasciare la tua recensione</p>"
					  +"</div>"
					  +"<div class='card-footer text-body-secondary'>"
					  +"</div>"
					+"</div>"
					+ "</div>");
			
			//creare dinameicamente il contenuto
			for(String recensioneX : recensioni) {
				String[] panelRec = recensioneX.split("&");
				
				response.append("<div class='carousel-item'>");
				response.append("<div class='card text-center'>"
						  +"<div class='card-header'>"
						    +"Dicono di Noi!"
						  +"</div>"
						  +"<div class='card-body'>");
				response.append("<h5 class='card-title'>").append(panelRec[0]).append("</h5>");
				response.append("<p class='card-text'>").append(panelRec[1]).append("</p>");
				response.append("</div>");
				response.append("<div class='card-footer text-body-secondary'>");
				response.append("<p><em>").append(panelRec[2]).append(" ").append(panelRec[3]).append("</em></p>");
				response.append("</div>");
				response.append("</div>");
				response.append("</div>");
			}
			response.append("<button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
					+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Previous</span>"
					+ "                        </button>\r\n"
					+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
					+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Next</span>"
					+ "                        </button>"
					+ "                    </div>"
					+ "                </div>"
					+ "            </div>"
					+ "        </div>"
					+ "    </section>"
					+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
					+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
					+ "            <div class='container-fluid'>"
					+ "                <p>\r\n"
					+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
					+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
					+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
					+ "                </p>"
					+ "            </div>"
					+ "        </nav>"
					
					+ "</body>"
					+ "</html>");
			
			
			
			exchange.getResponseHeaders().set("Content-Type",  "text/html");
			int contentlength = response.toString().getBytes("UTF-8").length;
			exchange.sendResponseHeaders(200, contentlength);
			
			//inviamo risposta al client
			OutputStream os = exchange.getResponseBody();
			os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
			os.close();//chiudiamo l'os
			
		}
	}	
	
	//User - Pagina Recensioni
	static class UserRecensioniHotelHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			
			//connessione al database
			List<String> recensioni = new ArrayList<>();
			try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
				String query = "SELECT recensioni.periodo, recensioni.recensione, account.nome, account.cognome FROM recensioni JOIN account ON recensioni.id_account = account.id_account";
				try(PreparedStatement pstmt = conn.prepareStatement(query)){
					ResultSet rs = pstmt.executeQuery();
					while(rs.next()) {
						String periodo = rs.getString("recensioni.periodo");
						String recensione = rs.getString("recensioni.recensione");
						String nome = rs.getString("account.nome");
						String cognome = rs.getString("account.cognome");
						recensioni.add(periodo+"&"+recensione+"&"+nome+"&"+cognome);

					}
					rs.close();
					pstmt.close();
					conn.close();
					
					
				}
				
			}catch(SQLException e){
				e.printStackTrace();
			}
			
			StringBuilder response = new StringBuilder();
			response.append("<!DOCTYPE html>"
					+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
					+ "<head>"
					+ "    <meta charset='utf-8' />"
					+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
					+ "    <meta author='Mattia Bascelli'/>"
					+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
					+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
					+ "    <title>GESTIONE HOTEL  - v0.1</title>"
					+ "</head>"
					+ "<body>"
					+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
					+ "        <div class='container-fluid'>"
					+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
					+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
					+ "                <span class='navbar-toggler-icon'></span>"
					+ "            </button>"
					+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
					+ "                <ul class='navbar-nav'>"
					+ "                    <li class='nav-item active'>"
					+ "                        <a class='nav-link' href='/userPage'>Home</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item dropdown'>"
					+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
					+ "                            Pannello Utente"
					+ "                        </a>"
					+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
					+ "                                <a href='/userPrenotaCamera' class='dropdown-item'>Prenota Camera</a>"
					+ "								   <a href='/userModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
					+ "                                <a href='/userEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
					+ "								   <a href='/userModificaPassword' class='dropdown-item'>Modifica Password</a>"
					+ "                                <div class='dropdown-divider'></div>"
					+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
					+ "                        </div>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/userRecensioniHotel'>Recensioni</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/userNoteSviluppatore'>Note Sviluppatore</a>"
					+ "                    </li>"
					+ "                </ul>"
					+ "            </div>"
					+ "        </div>"
					+ "    </nav>"
					+ "    <section>"
					+ "        <div class='container'>"
					+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
					+ "            <div class='row'>"
					+ "                <div class='col-sm-6'>"
					+ "                    <h2>Scopri il nostro HOTEL</h2>"
					+ "                    <p>Camere comprese di tutti i servizi, e tanto altro ancora</p>"
					+ "                    <hr />"
					+ "                    <div id='messaggio'>"
					+ "                        Hai lasciato una recensione? <a href='/userInserisciRecensione'>Scrivila</a>!"
					+ "                    </div>"
					+ "                </div>"
					+ "                <div class='col-sm-6'>"
					+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
					+ "						<div class='carousel-inner'>"
					+ "<div class='carousel-item active'>"
					+"<div class='card text-center'>"
					  +"<div class='card-header'>"
					    +"Dicono di Noi!"
					  +"</div>"
					  +"<div class='card-body'>"
					    +"<h5 class='card-title'>Scopri tutte le recensioni sulla nostra Struttura!</h5>"
					    +"<p class='card-text'>Accedi al tuo account per lasciare la tua recensione</p>"
					  +"</div>"
					  +"<div class='card-footer text-body-secondary'>"
					  +"</div>"
					+"</div>"
					+ "</div>");
			
			//creare dinameicamente il contenuto
			for(String recensioneX : recensioni) {
				String[] panelRec = recensioneX.split("&");
				
				response.append("<div class='carousel-item'>");
				response.append("<div class='card text-center'>"
						  +"<div class='card-header'>"
						    +"Dicono di Noi!"
						  +"</div>"
						  +"<div class='card-body'>");
				response.append("<h5 class='card-title'>").append(panelRec[0]).append("</h5>");
				response.append("<p class='card-text'>").append(panelRec[1]).append("</p>");
				response.append("</div>");
				response.append("<div class='card-footer text-body-secondary'>");
				response.append("<p><em>").append(panelRec[2]).append(" ").append(panelRec[3]).append("</em></p>");
				response.append("</div>");
				response.append("</div>");
				response.append("</div>");
			}
			response.append("<button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
					+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Previous</span>"
					+ "                        </button>\r\n"
					+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
					+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Next</span>"
					+ "                        </button>"
					+ "                    </div>"
					+ "                </div>"
					+ "            </div>"
					+ "        </div>"
					+ "    </section>"
					+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
					+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
					+ "            <div class='container-fluid'>"
					+ "                <p>"
					+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
					+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
					+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
					+ "                </p>"
					+ "            </div>"
					+ "        </nav>"
					
					+ "</body>"
					+ "</html>");
			
			
			
			exchange.getResponseHeaders().set("Content-Type",  "text/html");
			int contentlength = response.toString().getBytes("UTF-8").length;
			exchange.sendResponseHeaders(200, contentlength);
			
			//inviamo risposta al client
			OutputStream os = exchange.getResponseBody();
			os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
			os.close();//chiudiamo l'os
			
		}
	}	
	
	//Pagina Recensioni
	static class RecensioniHotelHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			
			//connessione al database
			List<String> recensioni = new ArrayList<>();
			try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
				String query = "SELECT recensioni.periodo, recensioni.recensione, account.nome, account.cognome FROM recensioni JOIN account ON recensioni.id_account = account.id_account";
				try(PreparedStatement pstmt = conn.prepareStatement(query)){
					ResultSet rs = pstmt.executeQuery();
					while(rs.next()) {
						String periodo = rs.getString("recensioni.periodo");
						String recensione = rs.getString("recensioni.recensione");
						String nome = rs.getString("account.nome");
						String cognome = rs.getString("account.cognome");
						recensioni.add(periodo+"&"+recensione+"&"+nome+"&"+cognome);

					}
					rs.close();
					pstmt.close();
					conn.close();
					
					
				}
				
			}catch(SQLException e){
				e.printStackTrace();
			}
			
			StringBuilder response = new StringBuilder();
			response.append("<!DOCTYPE html>"
					+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
					+ "<head>"
					+ "    <meta charset='utf-8' />"
					+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
					+ "    <meta author='Mattia Bascelli'/>"
					+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
					+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
					+ "    <title>GESTIONE HOTEL  - v0.1</title>"
					+ "</head>"
					+ "<body>"
					+ "    <nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
					+ "        <div class='container-fluid'>"
					+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
					+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
					+ "                <span class='navbar-toggler-icon'></span>"
					+ "            </button>"
					+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
					+ "                <ul class='navbar-nav'>"
					+ "                    <li class='nav-item active'>"
					+ "                        <a class='nav-link' href='/'>Home</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item dropdown'>"
					+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
					+ "                            Pannello Utente"
					+ "                        </a>"
					+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
					+ "                                <p class='dropdown-item'>Accedi Prima</p>"
					+ "                        </div>\r\n"
					+ "                    </li>\r\n"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/recensioniHotel'>Recensioni</a>"
					+ "                    </li>\r\n"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/noteSviluppatore'>Note Sviluppatore</a>"
					+ "                    </li>\r\n"
					+ "                </ul>"
					+ "            </div>"
					+ "        </div>"
					+ "    </nav>"
					+ "    <section>"
					+ "        <div class='container'>"
					+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
					+ "            <div class='row'>"
					+ "                <div class='col-sm-6'>"
					+ "                    <h2>Scopri il nostro HOTEL</h2>"
					+ "                    <p>Camere comprese di tutti i servizi, e tanto altro ancora</p>"
					+ "                    <hr />"
					+ "                    <div id='messaggio'>"
					+ "                        Non sei ancora Registrato? <a href='/registrazione'>Registrati</a>!"
					+ "                    </div>"
					+ "                </div>"
					+ "                <div class='col-sm-6'>"
					+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
					+ "						<div class='carousel-inner'>"
					+ "<div class='carousel-item active'>"
					+"<div class='card text-center'>"
					  +"<div class='card-header'>"
					    +"Dicono di Noi!"
					  +"</div>"
					  +"<div class='card-body'>"
					    +"<h5 class='card-title'>Scopri tutte le recensioni sulla nostra Struttura!</h5>"
					    +"<p class='card-text'>Accedi al tuo account per lasciare la tua recensione</p>"
					  +"</div>"
					  +"<div class='card-footer text-body-secondary'>"
					  +"</div>"
					+"</div>"
					+ "</div>");
			
			//creare dinameicamente il contenuto
			for(String recensioneX : recensioni) {
				String[] panelRec = recensioneX.split("&");
				
				response.append("<div class='carousel-item'>");
				response.append("<div class='card text-center'>"
						  +"<div class='card-header'>"
						    +"Dicono di Noi!"
						  +"</div>"
						  +"<div class='card-body'>");
				response.append("<h5 class='card-title'>").append(panelRec[0]).append("</h5>");
				response.append("<p class='card-text'>").append(panelRec[1]).append("</p>");
				response.append("</div>");
				response.append("<div class='card-footer text-body-secondary'>");
				response.append("<p><em>").append(panelRec[2]).append(" ").append(panelRec[3]).append("</em></p>");
				response.append("</div>");
				response.append("</div>");
				response.append("</div>");
			}
			response.append("<button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
					+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Previous</span>"
					+ "                        </button>\r\n"
					+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
					+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Next</span>"
					+ "                        </button>"
					+ "                    </div>"
					+ "                </div>"
					+ "            </div>"
					+ "        </div>"
					+ "    </section>"
					+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
					+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
					+ "            <div class='container-fluid'>"
					+ "                <p>\r\n"
					+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
					+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
					+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
					+ "                </p>"
					+ "            </div>"
					+ "        </nav>"
					
					+ "</body>"
					+ "</html>");
			
			
			
			exchange.getResponseHeaders().set("Content-Type",  "text/html");
			int contentlength = response.toString().getBytes("UTF-8").length;
			exchange.sendResponseHeaders(200, contentlength);
			
			//inviamo risposta al client
			OutputStream os = exchange.getResponseBody();
			os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
			os.close();//chiudiamo l'os
			
		}
	}	
	
	//Pagina Note Sviluppatore
	static class NoteSviluppatoreHandler implements HttpHandler{
		@Override
		public void handle(HttpExchange exchange) throws IOException{
			String htmlResponse = "<!DOCTYPE html>"
					+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
					+ "<head>"
					+ "    <meta charset='utf-8' />"
					+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
					+ "    <meta author='Mattia Bascelli'/>"
					+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
					+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
					+ "    <title>GESTIONE HOTEL  - v0.1</title>"
					+ "</head>"
					+ "<body>"
					+ "    <nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
					+ "        <div class='container-fluid'>"
					+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
					+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
					+ "                <span class='navbar-toggler-icon'></span>"
					+ "            </button>"
					+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
					+ "                <ul class='navbar-nav'>"
					+ "                    <li class='nav-item active'>"
					+ "                        <a class='nav-link' href='/'>Home</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item dropdown'>"
					+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
					+ "                            Pannello Utente"
					+ "                        </a>"
					+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
					+ "                                <p class='dropdown-item'>Accedi Prima</p>"
					+ "                        </div>\r\n"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/recensioniHotel'>Recensioni</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/noteSviluppatore'>Note Sviluppatore</a>"
					+ "                    </li>\r\n"
					+ "                </ul>"
					+ "            </div>"
					+ "        </div>"
					+ "    </nav>"
					+ "    <section>"
					+ "        <div class='container'>"
					+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
					+ "            <div class='row'>"
					+ "                <div class='col-sm-3'>"
					+ "                    <h2>Note Sviluppatore</h2>"
					+ "                    <p>Gestione Hotel è un progetto sviluppato da <strong>Mattia Bascelli</strong> durante il corso di Java Developer di <a href='https://www.bitcamp.it' target='_blank'>Bitcamp</a>.</p>"
					+ "                    <p>Il programma è una web-application che utilizza diverse tecnologie:</p>"
					+ "<ul>"
					+ "<li>HTML e BOOTSTRAP per l'interfaccia grafica</li>"
					+ "<li>MySQL per la gestione dei Database</li>"
					+ "<li>Java per gestire tutto il lato backend</li>"
					+ "</ul>"
					+ "<hr />"
					+ "                     <p>Puoi trovare tutta la documentazione relativa al progetto su <a href='https://github.com/mattiabascelli/gestione_hotel' target='_blank'>GitHub</a></p>  "
					+ "                </div>"
					+ "                <div class='col-sm-9'>"
					+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
					+ "                        <div class='carousel-inner'>\r\n"
					+ "                            <div class='carousel-item active'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel01.jpg?raw=true' class='d-block w-100 rounded-3' alt='Prima Immagine' />"
					+ "                            </div>\r\n"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel02.jpg?raw=true' class='d-block w-100 rounded-3' alt='Seconda Immagine' />"
					+ "                            </div>\r\n"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel03.jpg?raw=true' class='d-block w-100 rounded-3' alt='Terza Immagine' />"
					+ "                            </div>"
					+ "                        </div>"
					+ "                        <button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
					+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Previous</span>"
					+ "                        </button>\r\n"
					+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
					+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Next</span>"
					+ "                        </button>"
					+ "                    </div>"
					+ "                </div>"
					+ "            </div>"
					+ "        </div>"
					+ "    </section>"
					+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
					+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
					+ "            <div class='container-fluid'>"
					+ "                <p>\r\n"
					+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
					+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
					+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
					+ "                </p>"
					+ "            </div>"
					+ "        </nav>"
					
					+ "</body>"
					+ "</html>";
			
			
			
			//impostare la risposta con intestazione,status code e lunghezza
			exchange.getResponseHeaders().set("Content-Type", "text/html");
			//			exchange.sendResponseHeaders(200, htmlResponse.length());
			int contentLenght = htmlResponse.getBytes("UTF-8").length;
			exchange.sendResponseHeaders(200, contentLenght);

			//inviamo la risposta al client (browser)
			OutputStream os = exchange.getResponseBody();

			os.write(htmlResponse.getBytes());

			//chiusura dell'invio della risposta
			os.close();
		}
	}
	
	//User - Note Sviluppatore
	static class UserNoteSviluppatoreHandler implements HttpHandler{
		@Override
		public void handle(HttpExchange exchange) throws IOException{
			String htmlResponse = "<!DOCTYPE html>"
					+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
					+ "<head>"
					+ "    <meta charset='utf-8' />"
					+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
					+ "    <meta author='Mattia Bascelli'/>"
					+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
					+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
					+ "    <title>GESTIONE HOTEL  - v0.1</title>"
					+ "</head>"
					+ "<body>"
					+ "    <nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
					+ "        <div class='container-fluid'>"
					+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
					+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
					+ "                <span class='navbar-toggler-icon'></span>"
					+ "            </button>"
					+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
					+ "                <ul class='navbar-nav'>"
					+ "                    <li class='nav-item active'>"
					+ "                        <a class='nav-link' href='/userPage'>Home</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item dropdown'>\r\n"
					+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
					+ "                            Pannello Utente"
					+ "                        </a>"
					+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
					+ "                                <a href='#' class='dropdown-item'>Prenota Camera</a>"
					+ "								   <a href='#' class='dropdown-item'>Modifica Prenotazione</a>"
					+ "                                <a href='#' class='dropdown-item'>Elimina Prenotazione</a>"
					+ "								   <a href='#' class='dropdown-item'>Modifica Password</a>"
					+ "								   <a href='#' class='dropdown-item'>Inserisci Recensione</a>"
					+ "                                <div class='dropdown-divider'></div>"
					+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
					+ "                        </div>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/userRecensioniHotel'>Recensioni</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/userNoteSviluppatore'>Note Sviluppatore</a>"
					+ "                    </li>\r\n"
					+ "                </ul>"
					+ "            </div>"
					+ "        </div>"
					+ "    </nav>"
					+ "    <section>"
					+ "        <div class='container'>"
					+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
					+ "            <div class='row'>"
					+ "                <div class='col-sm-3'>"
					+ "                    <h2>Note Sviluppatore</h2>"
					+ "                    <p>Gestione Hotel è un progetto sviluppato da <strong>Mattia Bascelli</strong> durante il corso di Java Developer di <a href='https://www.bitcamp.it' target='_blank'>Bitcamp</a>.</p>"
					+ "                    <p>Il programma è una web-application che utilizza diverse tecnologie:</p>"
					+ "<ul>"
					+ "<li>HTML e BOOTSTRAP per l'interfaccia grafica</li>"
					+ "<li>MySQL per la gestione dei Database</li>"
					+ "<li>Java per gestire tutto il lato backend</li>"
					+ "</ul>"
					+ "<hr />"
					+ "                     <p>Puoi trovare tutta la documentazione relativa al progetto su <a href='https://github.com/mattiabascelli/gestione_hotel' target='_blank'>GitHub</a></p>  "
					+ "                </div>"
					+ "                <div class='col-sm-9'>"
					+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
					+ "                        <div class='carousel-inner'>\r\n"
					+ "                            <div class='carousel-item active'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel01.jpg?raw=true' class='d-block w-100 rounded-3' alt='Prima Immagine' />"
					+ "                            </div>\r\n"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel02.jpg?raw=true' class='d-block w-100 rounded-3' alt='Seconda Immagine' />"
					+ "                            </div>\r\n"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel03.jpg?raw=true' class='d-block w-100 rounded-3' alt='Terza Immagine' />"
					+ "                            </div>"
					+ "                        </div>"
					+ "                        <button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
					+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Previous</span>"
					+ "                        </button>\r\n"
					+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
					+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Next</span>"
					+ "                        </button>"
					+ "                    </div>"
					+ "                </div>"
					+ "            </div>"
					+ "        </div>"
					+ "    </section>"
					+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
					+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
					+ "            <div class='container-fluid'>"
					+ "                <p>\r\n"
					+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
					+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
					+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
					+ "                </p>"
					+ "            </div>"
					+ "        </nav>"
					
					+ "</body>"
					+ "</html>";
			
			
			
			//impostare la risposta con intestazione,status code e lunghezza
			exchange.getResponseHeaders().set("Content-Type", "text/html");
			//			exchange.sendResponseHeaders(200, htmlResponse.length());
			int contentLenght = htmlResponse.getBytes("UTF-8").length;
			exchange.sendResponseHeaders(200, contentLenght);

			//inviamo la risposta al client (browser)
			OutputStream os = exchange.getResponseBody();

			os.write(htmlResponse.getBytes());

			//chiusura dell'invio della risposta
			os.close();
		}
	}
	
	//Admin - Note Svilluppatore
	static class AdminNoteSviluppatoreHandler implements HttpHandler{
		@Override
		public void handle(HttpExchange exchange) throws IOException{
			String htmlResponse = "<!DOCTYPE html>"
					+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
					+ "<head>"
					+ "    <meta charset='utf-8' />"
					+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
					+ "    <meta author='Mattia Bascelli'/>"
					+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
					+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
					+ "    <title>GESTIONE HOTEL  - v0.1</title>"
					+ "</head>"
					+ "<body>"
					+ "    <nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
					+ "        <div class='container-fluid'>"
					+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
					+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
					+ "                <span class='navbar-toggler-icon'></span>"
					+ "            </button>"
					+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
					+ "                <ul class='navbar-nav'>"
					+ "                    <li class='nav-item active'>"
					+ "                        <a class='nav-link' href='/adminPage'>Home</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item dropdown'>"
					+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
					+ "                            Pannello Admin"
					+ "                        </a>"
					+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
					+ "                                <a href=/adminInserisciCamera' class='dropdown-item'>Inserisci Camera</a>"
					+ "								   <a href='/adminModificaCamera' class='dropdown-item'>Modifica Camera</a>"
					+ "                                <a href='/adminEliminaCamera' class='dropdown-item'>Elimina Camera</a>"
					+ "								   <a href='/adminStoricoPrenotazioni' class='dropdown-item'>Storico Prenotazioni</a>"
					+ "                                <a href='/adminModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
					+ "								   <a href='/adminEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
					+ "                                <div class='dropdown-divider'></div>"
					+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
					+ "                        </div>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/adminRecensioniHotel'>Recensioni</a>"
					+ "                    </li>"
					+ "                    <li class='nav-item'>"
					+ "                        <a class='nav-link' href='/adminNoteSviluppatore'>Note Sviluppatore</a>"
					+ "                    </li>\r\n"
					+ "                </ul>"
					+ "            </div>"
					+ "        </div>"
					+ "    </nav>"
					+ "    <section>"
					+ "        <div class='container'>"
					+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
					+ "            <div class='row'>"
					+ "                <div class='col-sm-3'>"
					+ "                    <h2>Note Sviluppatore</h2>"
					+ "                    <p>Gestione Hotel è un progetto sviluppato da <strong>Mattia Bascelli</strong> durante il corso di Java Developer di <a href='https://www.bitcamp.it' target='_blank'>Bitcamp</a>.</p>"
					+ "                    <p>Il programma è una web-application che utilizza diverse tecnologie:</p>"
					+ "<ul>"
					+ "<li>HTML e BOOTSTRAP per l'interfaccia grafica</li>"
					+ "<li>MySQL per la gestione dei Database</li>"
					+ "<li>Java per gestire tutto il lato backend</li>"
					+ "</ul>"
					+ "<hr />"
					+ "                     <p>Puoi trovare tutta la documentazione relativa al progetto su <a href='https://github.com/mattiabascelli/gestione_hotel' target='_blank'>GitHub</a></p>  "
					+ "                </div>"
					+ "                <div class='col-sm-9'>"
					+ "                    <div id='MioCarosello' class='carousel slide' data-bs-ride='carousel' data-bs-interval='3000'>"
					+ "                        <div class='carousel-inner'>\r\n"
					+ "                            <div class='carousel-item active'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel01.jpg?raw=true' class='d-block w-100 rounded-3' alt='Prima Immagine' />"
					+ "                            </div>\r\n"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel02.jpg?raw=true' class='d-block w-100 rounded-3' alt='Seconda Immagine' />"
					+ "                            </div>\r\n"
					+ "                            <div class='carousel-item'>\r\n"
					+ "                                <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/hotel03.jpg?raw=true' class='d-block w-100 rounded-3' alt='Terza Immagine' />"
					+ "                            </div>"
					+ "                        </div>"
					+ "                        <button class='carousel-control-prev' type='button' data-bs-target='#MioCarosello' data-bs-slide='prev'>"
					+ "                            <span class='carousel-control-prev-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Previous</span>"
					+ "                        </button>\r\n"
					+ "                        <button class='carousel-control-next' type='button' data-bs-target='#MioCarosello' data-bs-slide='next'>"
					+ "                            <span class='carousel-control-next-icon' aria-hidden='true'></span>"
					+ "                            <span class='visually-hidden'>Next</span>"
					+ "                        </button>"
					+ "                    </div>"
					+ "                </div>"
					+ "            </div>"
					+ "        </div>"
					+ "    </section>"
					+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
					+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
					+ "            <div class='container-fluid'>"
					+ "                <p>\r\n"
					+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
					+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
					+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
					+ "                </p>"
					+ "            </div>"
					+ "        </nav>"
					
					+ "</body>"
					+ "</html>";
			
			
			
			//impostare la risposta con intestazione,status code e lunghezza
			exchange.getResponseHeaders().set("Content-Type", "text/html");
			//			exchange.sendResponseHeaders(200, htmlResponse.length());
			int contentLenght = htmlResponse.getBytes("UTF-8").length;
			exchange.sendResponseHeaders(200, contentLenght);

			//inviamo la risposta al client (browser)
			OutputStream os = exchange.getResponseBody();

			os.write(htmlResponse.getBytes());

			//chiusura dell'invio della risposta
			os.close();
		}
	}
	
	//User - Prenotazione Camera
	static class UserPrenotaCameraHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				//connessione al database
				List<String> camere = new ArrayList<>();
				try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "SELECT * FROM camere";
					try(PreparedStatement pstmt = conn.prepareStatement(query)){
						ResultSet rs = pstmt.executeQuery();
						while(rs.next()) {
							int id_camera = rs.getInt("id_camere");
							String nome = rs.getString("nome_camera");
							String tipo = rs.getString("tipo");
							double prezzo = rs.getDouble("prezzo");
							String servizi = rs.getString("servizi");
							camere.add(id_camera+"&"+nome+"&"+tipo+"&"+prezzo+"&"+servizi);

						}
						
						
						
						rs.close();
						pstmt.close();
						conn.close();
						
						
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//form html per la registrazione Account
				StringBuilder response = new StringBuilder();
				response.append("<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/userPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Utente"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href='/userPrenotaCamera' class='dropdown-item'>Prenota Camera</a>"
								+ "								   <a href='/userModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "                                <a href='/userEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "								   <a href='/userModificaPassword' class='dropdown-item'>Modifica Password</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/userRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/userNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/userPrenotaCamera' class='btn btn-success'>Prenota Camera</a>"
								+ "<a href='/userModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/userEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "<a href='/userModificaPassword' class='btn btn-success'>Modifica Password</a>"
								+ "<a href='/userInserisciRecensione' class='btn btn-success'>Inserisci Recensione</a>"
								+ "</div>"
								+ "                </div>"
								+ "                <div class='col-sm-8'>"
								+ "					<form method='post' action='/userPrenotaCamera'>"
								+ "    <div class='form-group'>"
								+ "        <label for='check_in'>Check-In:</label>"
								+ "        <input type='date' class='form-control' id='check_in' name='check_in' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='check_out'>Check-Out:</label>"
								+ "        <input type='date' class='form-control' id='check_out' name='check_out' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='n_persone'>n° Persone:</label>"
								+ "        <input type='text' class='form-control' id='n_persone' name='n_persone' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='camera'>camera:</label><br />"
								+ "<select class='form-group' id='camera' name='camera'>");
								//creare dinameicamente il contenuto
								for(String camera : camere) {
									String[] cameraX = camera.split("&");
									response.append("<option value='").append(cameraX[0]).append("'>").append(cameraX[1]).append("</option>");
									}
									response.append("</select><br /></div>"
								+ "    <br />"
								+ "    <button type='submit' class='btn btn-success'>Prenota</button>"
								+ "</form>"
								+ "                    <hr />"
								+ "                    <div id='messaggio'>"
								+ "<p>Effettuata la prenotazione <strong>riceverete un PDF</strong> nella vostra cartella DOWNLOAD con la ricevuta.<br />Il pdf può essere mostrato alla reception durante il check-in!</p>"
								+ "                    </div>"
								+ "<div class='table-responsive'>"
								+"<table class='table table-bordered table-success'>"
								+"<thead>"
								+"<tr>"
								+"<th>id</th>"
								+"<th>Nome</th>"
								+"<th>Tipologia</th>"
								+"<th>prezzo</th>"
								+"<th>Servizi</th>"
								+"</tr>"
								+"</thead>"
								+"<tbody class='table table-bordered table-striped'>");
				//creare dinameicamente il contenuto
				for(String camera : camere) {
					String[] cameraX = camera.split("&");
					response.append("<tr class='table-light'><td>").append(cameraX[0]).append("</td>");
					response.append("<td>").append(cameraX[1]).append("</td>");
					response.append("<td>").append(cameraX[2]).append("</td>");
					response.append("<td>").append(cameraX[3]).append("</td>");
					response.append("<td>").append(cameraX[4]).append("</td></tr>");
					}
					response.append("</tbody>"
								+"</table>"
			    				+ "</div>"
								+ "        </div>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>\r\n"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
								
								+ "</body>"
								+ "</html>");
									
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = response.toString().getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//Split della riga nelle sue componenti di base
				String[] formDataArray = formData.split("&");
				//recuperiamo i singoli valori
				String checkin = formDataArray[0].split("=")[1];
				String checkout = formDataArray[1].split("=")[1];
				int nPersone = Integer.parseInt(formDataArray[2].split("=")[1]);
				int idCamera = Integer.parseInt(formDataArray[3].split("=")[1]);
				
				int idAccount = ID_UTENTE;
				System.out.println(idAccount);
				String query = "INSERT INTO prenotazioni(check_in, check_out, n_persone, id_camera, id_account) VALUES (?, ?, ?, ?, ?)";
				
				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setString(1, checkin);
						pstmt.setString(2, checkout);
						pstmt.setInt(3, nPersone);
						pstmt.setInt(4, idCamera);
						pstmt.setInt(5, idAccount);			
						
						PDDocument document = new PDDocument();
						PDPage page = new PDPage(PDRectangle.A4);
						
						document.addPage(page);
						
						//Apertura del content stream
						PDPageContentStream contentStream = new PDPageContentStream(document, page);
						
						//imposta il font e la dimensione del testo
						contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
						contentStream.beginText();//aprimao la scrittura sulla riga
						contentStream.newLineAtOffset(50, 750);//posizionamento
						contentStream.showText("RICEVUTA PRENOTAZIONE");
						contentStream.endText();
						contentStream.setFont(PDType1Font.HELVETICA, 12);
						contentStream.beginText();
						contentStream.newLineAtOffset(50, 730);
						contentStream.showText("Prenotazione effettuata con successo!");
						contentStream.endText();
						contentStream.beginText();
						contentStream.newLineAtOffset(50, 700);
						contentStream.showText("CHECK IN:  "+ checkin);
						contentStream.newLineAtOffset(200, 0);
						contentStream.showText("CHECK OUT: "+ checkout);
						contentStream.endText();
						contentStream.beginText();
						contentStream.newLineAtOffset(50, 680);
						contentStream.showText("ID Stanza: "+ idCamera);
						contentStream.endText();
						contentStream.beginText();
						contentStream.newLineAtOffset(50, 660);
						contentStream.showText("Nome:  "+ UTENTE);
						contentStream.endText();
						contentStream.beginText();
						contentStream.newLineAtOffset(50, 640);
						contentStream.showText("Cognome:   "+ C_UTENTE);
						contentStream.endText();
						contentStream.beginText();
						contentStream.newLineAtOffset(50, 600);
						contentStream.showText("ID ACCOUNT:   "+ idAccount);
						contentStream.endText();
						contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
						contentStream.beginText();
						contentStream.newLineAtOffset(50, 560);
						contentStream.showText("Mostra questa ricevuta alla reception al tuo arrivo per effettuare il Check-In!");
						contentStream.endText();
						contentStream.setFont(PDType1Font.HELVETICA, 8);
						contentStream.beginText();
						contentStream.newLineAtOffset(50, 540);
						contentStream.showText("Questa ricevuta è un semplice promemoria, ma aiuterà la reception a velocizzare il vostro Check-in!");
						contentStream.endText();
						contentStream.beginText();
						contentStream.newLineAtOffset(50, 500);
						contentStream.showText("--[ Taglia } --------------------------------------------------------------------------------------------------------------------------");
						contentStream.endText();
						
						//chiusura dello stream
						contentStream.close();
						//salvataggio del documento sulla macchina
						document.save("C:/Users/bruci/Downloads/Fattura_"+UTENTE+"_"+C_UTENTE+"_IN_"+checkin+"_OUT_"+checkout+".pdf");
						
						System.out.println("Fattura PDF creata con successo");
						
						document.close();

						pstmt.executeUpdate();	
						System.out.println("Dato inserito correttamente");
						pstmt.close();
						conn.close();
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/userPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
			
			
			
			
		}
	
	//User - Modifica Prenotazione
	static class UserModificaPrenotazione implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				//connessione al database
				List<String> camere = new ArrayList<>();
				List<String> prenotazioni = new ArrayList<>();
				try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "SELECT * FROM camere";
					try(PreparedStatement pstmt = conn.prepareStatement(query)){
						ResultSet rs = pstmt.executeQuery();
						while(rs.next()) {
							int id_camera = rs.getInt("id_camere");
							String nome = rs.getString("nome_camera");
							String tipo = rs.getString("tipo");
							double prezzo = rs.getDouble("prezzo");
							String servizi = rs.getString("servizi");
							camere.add(id_camera+"&"+nome+"&"+tipo+"&"+prezzo+"&"+servizi);
						}
							
						rs.close();
						pstmt.close();
						
					}
					
					int idAccount = ID_UTENTE;
					String query2 = "SELECT id_prenotazioni, check_in, check_out, n_persone FROM prenotazioni WHERE prenotazioni.id_account = ? ";
								
				try(PreparedStatement pstmt = conn.prepareStatement(query2)){
					pstmt.setInt(1, idAccount);
					ResultSet rs = pstmt.executeQuery();
					while(rs.next()) {
						int id_prenotazione= rs.getInt("prenotazioni.id_prenotazioni");
						String checkIn = rs.getString("prenotazioni.check_in");
						String checkOut = rs.getString("prenotazioni.check_out");
						int nPersone = rs.getInt("prenotazioni.n_persone");	
						prenotazioni.add(id_prenotazione+"&"+checkIn+"&"+checkOut+"&"+nPersone);

					}
					
					rs.close();
					pstmt.close();
					conn.close();
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//form html per la registrazione Account
				StringBuilder response = new StringBuilder();
				response.append("<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/userPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Utente"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href='/userPrenotaCamera' class='dropdown-item'>Prenota Camera</a>"
								+ "								   <a href='/userModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "                                <a href='/userEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "								   <a href='/userModificaPassword' class='dropdown-item'>Modifica Password</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/userRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/userNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/userPrenotaCamera' class='btn btn-success'>Prenota Camera</a>"
								+ "<a href='/userModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/userEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "<a href='/userModificaPassword' class='btn btn-success'>Modifica Password</a>"
								+ "<a href='/userInserisciRecensione' class='btn btn-success'>Inserisci Recensione</a>"
								+ "</div>"
								+ "                </div>"
								+ "                <div class='col-sm-8'>"
								+ "					<form method='post' action='/userModificaPrenotazione'>"
								+ "    <div class='form-group'>"
								+ "        <label for='id'>ID Prenotazione:</label>"
								+ "        <input type='number' class='form-control' id='id' name='id' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='check_in'>Check-In:</label>"
								+ "        <input type='date' class='form-control' id='check_in' name='check_in' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='check_out'>Check-Out:</label>"
								+ "        <input type='date' class='form-control' id='check_out' name='check_out' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='n_persone'>n° Persone:</label>"
								+ "        <input type='text' class='form-control' id='n_persone' name='n_persone' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='camera'>camera:</label><br />"
								+ "<select class='form-group' id='camera' name='camera'>");
								//creare dinameicamente il contenuto
								for(String camera : camere) {
									String[] cameraX = camera.split("&");
									response.append("<option value='").append(cameraX[0]).append("'>").append(cameraX[1]).append(" - ").append(cameraX[2]).append(" - PREZZO: [ ").append(cameraX[3]).append(" ]</option>");
									}
									response.append("</select><br /></div>"
								+ "    <br />"
								+ "    <button type='submit' class='btn btn-success'>Modifica Prenotazione</button>"
								+ "</form>"
								+ "                    <hr />"
								+ "                    <div id='messaggio'>"
								+ "<p>Controlla sempre di aver messo i dati in modo corretto prima di effettuare una modifica alla prenotazione.<br />Ovviamente non ti preoccupare, potrai sempre modificarlo in seguito!</p>"
								+ "                    </div>"
								+ "<div class='table-responsive'>"
								+"<table class='table table-bordered table-success'>"
								+"<thead>"
								+"<tr>"
								+"<th>id</th>"
								+"<th>Check-in</th>"
								+"<th>Check-out</th>"
								+"<th>n°Persone</th>"
								+"</tr>"
								+"</thead>"
								+"<tbody class='table table-bordered table-striped'>");
				//creare dinameicamente il contenuto
				for(String prenota : prenotazioni) {
					String[] cameraX = prenota.split("&");
					response.append("<tr class='table-light'><td>").append(cameraX[0]).append("</td>");
					response.append("<td>").append(cameraX[1]).append("</td>");
					response.append("<td>").append(cameraX[2]).append("</td>");
					response.append("<td>").append(cameraX[3]).append("</td></tr>");
					}
					response.append("</tbody>"
								+"</table>"
			    				+ "</div>"
								+ "        </div>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>\r\n"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
								
								+ "</body>"
								+ "</html>");
									
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = response.toString().getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				System.out.println(formData);
				//Split della riga nelle sue componenti di base
				String[] formDataArray = formData.split("&");
				//recuperiamo i singoli valori
				int idPrenotazione = Integer.parseInt(formDataArray[0].split("=")[1]);
				String checkin = formDataArray[1].split("=")[1];
				String checkout = formDataArray[2].split("=")[1];
				int nPersone = Integer.parseInt(formDataArray[3].split("=")[1]);
				int idCamera = Integer.parseInt(formDataArray[4].split("=")[1]);
				int idAccount = ID_UTENTE;
				System.out.println(idAccount);
				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "UPDATE prenotazioni SET check_in = ?, check_out = ?, n_persone = ?, id_camera = ?, id_account = ? WHERE id_prenotazioni = ?";
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setString(1, checkin);
						pstmt.setString(2, checkout);
						pstmt.setInt(3, nPersone);
						pstmt.setInt(4, idCamera);
						pstmt.setInt(5, idAccount);
						pstmt.setInt(6,  idPrenotazione);

						pstmt.executeUpdate();	
						System.out.println("Dato inserito correttamente");
						pstmt.close();
						conn.close();
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/userPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
			
			
			
			
		}
	
	//User - Elimina Prenotazione
	static class UserEliminaPrenotazione implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				//connessione al database
				List<String> prenotazioni = new ArrayList<>();
				try(Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					int idAccount = ID_UTENTE;
					String query2 = "SELECT id_prenotazioni, check_in, check_out, n_persone FROM prenotazioni WHERE prenotazioni.id_account = ? ";
								
				try(PreparedStatement pstmt = conn.prepareStatement(query2)){
					pstmt.setInt(1, idAccount);
					ResultSet rs = pstmt.executeQuery();
					while(rs.next()) {
						int id_prenotazione= rs.getInt("prenotazioni.id_prenotazioni");
						String checkIn = rs.getString("prenotazioni.check_in");
						String checkOut = rs.getString("prenotazioni.check_out");
						int nPersone = rs.getInt("prenotazioni.n_persone");	
						prenotazioni.add(id_prenotazione+"&"+checkIn+"&"+checkOut+"&"+nPersone);

					}
					
					rs.close();
					pstmt.close();
					conn.close();
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//form html per la registrazione Account
				StringBuilder response = new StringBuilder();
				response.append("<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/userPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Utente"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href='/userPrenotaCamera' class='dropdown-item'>Prenota Camera</a>"
								+ "								   <a href='/userModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "                                <a href='/userEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "								   <a href='/userModificaPassword' class='dropdown-item'>Modifica Password</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/userRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/userNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/userPrenotaCamera' class='btn btn-success'>Prenota Camera</a>"
								+ "<a href='/userModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/userEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "<a href='/userModificaPassword' class='btn btn-success'>Modifica Password</a>"
								+ "<a href='/userInserisciRecensione' class='btn btn-success'>Inserisci Recensione</a>"
								+ "</div>"
								+ "                </div>"
								+ "                <div class='col-sm-8'>"
								+ "					<form method='post' action='/userEliminaPrenotazione'>"
								+ "    <div class='form-group'>"
								+ "        <label for='id'>ID Prenotazione:</label>"
								+ "        <input type='number' class='form-control' id='id' name='id' required />"
								+ "    </div><br />"
								+ "    <button type='submit' class='btn btn-danger'>Elimina Prenotazione</button>"
								+ "</form>"
								+ "                    <hr />"
								+ "                    <div id='messaggio'>"
								+ "<p>Ricorda che eliminare una prenotazione è irreversibile.<br />In ogni caso, prenota e vieni a trovarci quando vuoi!</p>"
								+ "                    </div>"
								+ "<div class='table-responsive'>"
								+"<table class='table table-bordered table-success'>"
								+"<thead>"
								+"<tr>"
								+"<th>id</th>"
								+"<th>Check-in</th>"
								+"<th>Check-out</th>"
								+"<th>n°Persone</th>"
								+"</tr>"
								+"</thead>"
								+"<tbody class='table table-bordered table-striped'>");
				//creare dinameicamente il contenuto
				for(String prenota : prenotazioni) {
					String[] cameraX = prenota.split("&");
					response.append("<tr class='table-light'><td>").append(cameraX[0]).append("</td>");
					response.append("<td>").append(cameraX[1]).append("</td>");
					response.append("<td>").append(cameraX[2]).append("</td>");
					response.append("<td>").append(cameraX[3]).append("</td></tr>");
					}
					response.append("</tbody>"
								+"</table>"
			    				+ "</div>"
								+ "        </div>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>\r\n"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
								
								+ "</body>"
								+ "</html>");
									
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = response.toString().getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//recuperiamo il valore
				int id = Integer.parseInt(formData.split("=")[1]);
				System.out.println(id); 
				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "DELETE FROM prenotazioni WHERE id_prenotazioni = ?";
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setInt(1, id);
						pstmt.executeUpdate();	
						System.out.println("Dato eliminato correttamente");
						pstmt.close();
						conn.close();
					
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/userPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
			
			
			
			
		}
	
	//User - Inserisci Recensione
	static class UserInserisciRecensioneHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				
				//form html per la registrazione Account
				StringBuilder response = new StringBuilder();
				response.append("<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/userPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Utente"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href='/userPrenotaCamera' class='dropdown-item'>Prenota Camera</a>"
								+ "								   <a href='/userModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "                                <a href='/userEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "								   <a href='/userModificaPassword' class='dropdown-item'>Modifica Password</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/userRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/userNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/userPrenotaCamera' class='btn btn-success'>Prenota Camera</a>"
								+ "<a href='/userModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/userEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "<a href='/userModificaPassword' class='btn btn-success'>Modifica Password</a>"
								+ "<a href='/userInserisciRecensione' class='btn btn-success'>Inserisci Recensione</a>"
								+ "</div>"
								+ "                </div>"
								+ "                <div class='col-sm-8'>"
								+ "					<form method='post' action='/userInserisciRecensione'>"
								+ "    <div class='form-group'>"
								+ "        <label for='periodo'>Periodo:</label>"
								+ "        <input type='text' class='form-control' id='periodo' name='periodo' placeholder='esempio Estate 2024' required />"
								+ "    </div>"
								+ "    <div class='form-group'>"
								+ "        <label for='check_out'>Recensione:</label>"
								+ "        <input type='text' class='form-control' id='recensione' name='recensione' required /><br />"
								+ "			<p><em>Si prega di non utilizzare caratteri speciali come % & !</em></p>"
								+ "    </div>"
								+ "    <br />"
								+ "    <button type='submit' class='btn btn-success'>Lascia Recensione</button>"
								+ "</form>"
								+ "                    <hr />"
								+ "                    <div id='messaggio'>"
								+ "<p>Si raccomanda di mantenere sempre un linguaggio corretto nell'inseriemnto delle Recensioni.<br />Speriamo di avervi lasciato un bel ricordo della nostra Struttura!</p>"
								+ "                    </div>"
								+ "<div class='container-fluid'>"
			    				+ "</div>"
								+ "        </div>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>\r\n"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
								
								+ "</body>"
								+ "</html>");
									
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = response.toString().getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//Split della riga nelle sue componenti di base
				String[] formDataArray = formData.split("&");
				//recuperiamo i singoli valori
				String xperiodo = formDataArray[0].split("=")[1];
				String periodo = xperiodo.replace("+", " ");
				String xrecensione = formDataArray[1].split("=")[1];
				String recensione = xrecensione.replace("+", " ");
				int idAccount = ID_UTENTE;
				System.out.println(idAccount);
				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "INSERT INTO recensioni(periodo, recensione, id_account) VALUES (?, ?, ?)";
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setString(1, periodo);
						pstmt.setString(2, recensione);
						pstmt.setInt(3, idAccount);

						
						pstmt.executeUpdate();	
						System.out.println("Dato inserito correttamente");
						pstmt.close();
						conn.close();
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/userPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
	
			
		}
	
	//User - Modifica Password
	static class ModificaPasswordHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				
				
				//form html per la registrazione Account
				StringBuilder response = new StringBuilder();
				response.append("<!DOCTYPE html>"
						+ "<html lang='it' xmlns='http://www.w3.org/1999/xhtml'>"
						+ "<head>"
						+ "    <meta charset='utf-8' />"
						+ "    <meta name='viewport' content='width=device-width, initial-scale=1' />"
						+ "    <meta author='Mattia Bascelli'/>"
						+ "    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH' crossorigin='anonymous'>"
						+ "    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js' integrity='sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz' crossorigin='anonymous'></script>"
						+ "    <title>GESTIONE HOTEL  - v0.1</title>"
						+ "</head>"
						+ "<body>"
						+"<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>"
								+ "        <div class='container-fluid'>"
								+ "            <a class='navbar-brand' href='#'>GESTIONE HOTEL</a>"
								+ "            <button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle Navigation'>"
								+ "                <span class='navbar-toggler-icon'></span>"
								+ "            </button>"
								+ "            <div class='collapse navbar-collapse' id='navbarNav'>"
								+ "                <ul class='navbar-nav'>"
								+ "                    <li class='nav-item active'>"
								+ "                        <a class='nav-link' href='/userPage'>Home</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item dropdown'>"
								+ "                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdown' role='button' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
								+ "                            Pannello Utente"
								+ "                        </a>"
								+ "                        <div class='dropdown-menu' aria-labelledby='navbarDropdown'>"
								+ "                                <a href='/userPrenotaCamera' class='dropdown-item'>Prenota Camera</a>"
								+ "								   <a href='/userModificaPrenotazione' class='dropdown-item'>Modifica Prenotazione</a>"
								+ "                                <a href='/userEliminaPrenotazione' class='dropdown-item'>Elimina Prenotazione</a>"
								+ "								   <a href='/userModificaPassword' class='dropdown-item'>Modifica Password</a>"
								+ "                                <div class='dropdown-divider'></div>"
								+ "								   <a href='/' class='dropdown-item'>Disconnetti</a>"
								+ "                        </div>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/userRecensioniHotel'>Recensioni</a>"
								+ "                    </li>"
								+ "                    <li class='nav-item'>"
								+ "                        <a class='nav-link' href='/userNoteSviluppatore'>Note Sviluppatore</a>"
								+ "                    </li>"
								+ "                </ul>"
								+ "            </div>"
								+ "        </div>"
								+ "    </nav>"
								+ "    <section>"
								+ "        <div class='container'>"
								+ "            <h1 class='mt-5 mb-3'>Gestione Hotel v.01</h1>"
								+ "            <div class='row'>"
								+ "                <div class='col-sm-4'>"
								+ "                    <h2>Area Riservata</h2>"
								+ "                    <p>Benvenuto nella tua Area Riservata!<br />"
								+ "						  Cosa vuoi fare?</p>"
								+ "<div class='d-grid gap-2' >"
								+ "<a href='/userPrenotaCamera' class='btn btn-success'>Prenota Camera</a>"
								+ "<a href='/userModificaPrenotazione' class='btn btn-success'>Modifica Prenotazione</a>"
								+ "<a href='/userEliminaPrenotazione' class='btn btn-success'>Elimina Prenotazione</a>"
								+ "<a href='/userModificaPassword' class='btn btn-success'>Modifica Password</a>"
								+ "<a href='/userInserisciRecensione' class='btn btn-success'>Inserisci Recensione</a>"
								+ "</div>"
								+ "                </div>"
								+ "                <div class='col-sm-8'>"
								+ "					<form method='post' action='/userModificaPassword'>"
								+ "    <div class='form-group'>"
								+ "        <label for='password'>Nuova Password:</label>"
								+ "        <input type='text' class='form-control' id='password' name='password' placeholder='non usare password banali come ciao1234' required />"
								+ "<p><em>Non dare la tua password ad estranei!</em></p>"
								+ "    </div>"
								+ "    <br />"
								+ "    <button type='submit' class='btn btn-danger'>Cambia Password</button>"
								+ "</form>"
								+ "                    <hr />"
								+ "                    <div id='messaggio'>"
								+ "<p>Per qualsiasi informazione non esitare a telefonare alla nostra assistenza!<br />Siamo attivi dal lunedì al venerdì, dalle 10:00 alle 13:00 e dalle 14:00 alle 17:00.<br /><strong>TEL: 0123/456789</strong></p>"
								+ "                    </div>"
								+ "        </div>"
								+ "<div><table><tr><td style='color: white'>.</td></tr><tr><td style='color: white'>.</td></tr></table></div>"
								+ "        <nav class='navbar fixed-bottom bg-body-tertiary'>"
								+ "            <div class='container-fluid'>"
								+ "                <p>"
								+ "                    <a class='navbar-brand' href='https://github.com/mattiabascelli' target='_blank'>"
								+ "                        <img src='https://github.com/mattiabascelli/gestione_hotel/blob/main/img/logo_MB.png?raw=true' alt='Logo' width='45' height='45' class='d-inline-block'>"
								+ "                    </a>&copy; 2024 Bascelli Mattia. Tutti i diritti riservati"
								+ "                </p>"
								+ "            </div>"
								+ "        </nav>"
						
								+ "</body>"
								+ "</html>");
									
				exchange.getResponseHeaders().set("Content-Type",  "text/html");
				int contentlength = response.toString().getBytes("UTF-8").length;
				exchange.sendResponseHeaders(200, contentlength);
				
				//inviamo risposta al client
				OutputStream os = exchange.getResponseBody();
				os.write(response.toString().getBytes());//scriviamo il documento HTML come array di byte
				os.close();//chiudiamo l'os
				
				
			} else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				//recupera i dati dal Form
				//intercettiamo il flusso di bit
				InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
				//convertiamo i flussi bit in Byte
				BufferedReader br = new BufferedReader(isr);
				//convertiamo i byte in una stringa di testo
				String formData = br.readLine();
				//Split della riga nelle sue componenti di base
				String[] formDataArray = formData.split("&");
				//recuperiamo i singoli valori
				String password = formDataArray[0].split("=")[1];
				int idAccount = ID_UTENTE;
				System.out.println(idAccount);
				//connessione al database
				try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)){
					String query = "UPDATE account SET password = ? WHERE id_account = ? ";
					try (PreparedStatement pstmt = conn.prepareStatement(query)){
						pstmt.setString(1, password);
						pstmt.setInt(2, idAccount);

						
						pstmt.executeUpdate();	
						System.out.println("password modificata correttamente");
						pstmt.close();
						conn.close();
					}
					
					
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				//Dopo l'inserimento rimandiamo l'utente alla homepage
				exchange.getResponseHeaders().set("Location","/userPage");
				exchange.sendResponseHeaders(302, -1); //302 codice conferma reindirizzamento per il browser

			} else {
				//error 405 method not Allowed
				exchange.sendResponseHeaders(405, -1);
			}
		}
		
	
			
		}
	
	
	//Creazione database
	private static void createDatabaseIfNotExists() {
		try(
			Connection conn = DriverManager.getConnection(DB_URL_CREATE,DB_USERNAME,DB_PASSWORD);
			Statement stmt = conn.createStatement();	
			) {
				stmt.executeUpdate(CREATE_DB_QUERY);
			
			} catch(SQLException e){
				e.printStackTrace();
			}
	}
	
	private static void createTableIfNotExists() {
		try(
			Connection conn = DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);
			Statement stmt = conn.createStatement();	
			) {
				stmt.executeUpdate(CREATE_TABLE_CAMERE_QUERY);
				stmt.executeUpdate(CREATE_TABLE_ACCOUNT_QUERY);
				stmt.executeUpdate(CREATE_TABLE_RECENSIONI_QUERY);
				stmt.executeUpdate(CREATE_TABLE_PRENOTAZIONI_QUERY);
			
			} catch(SQLException e){
				e.printStackTrace();
			}
	}

}
