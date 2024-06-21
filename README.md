# gestione_hotel
La finalità del progetto è quella di creare una Web-Application con backend Java per la gestione di un Hotel.
Dopo esser entrati attraverso una schermata di login, il programma riconoscerà se si è amministratori o utenti e farà accedere alla propria pagina sbloccanto interazioni esclusive in base ai permessi.

Esempio dell'interfaccia utente è la pagina HTML [ esempio_interfaccia.html ]

Per poter utilizzare correttamente il programma bisogna installare MySQL. Il programma autogenera il database e tabelle se non sono esistenti. Nel caso si voglia riempire qualche campo prima dell'utilizzo del programma troverete il file [ database_SQL.txt ] con le query per la creazione del database. Per la gestione del database viene utilizzata la libreria mysql-connector-j-8.2.0.

Nel codice vengono impostati come [username: "admin" e password: "admin"] e vanno inseriti i dati del vostro SQL come url, utente e password.

Potete dare una rapida occhiata alle funzionalità nel video sulla pagina index del progetto all'url:
https://mattiabascelli.github.io/gestione_hotel/

Il programma inoltre permette la generazione di un PDF della prenotazione dopo averla effettuata correttamente, in questo caso abbiamo utilizzato la libreria pdfbox-app-2.0.30.

Si consiglia di aprire l'eseguibile del programma con il prompt dei comandi una volta creato, per permettervi una chiusura più comoda.

Il programma è suddiviso in diversi Handler dedicati ognuno ad una specifica funzionalità del programma. In totale sono 22, alcuni sono accessibili anche senza accedere alla pagina di login. Una volta effettuato il login il programma riconoscerà automaticamente se si è utente amministratore oppure utenti esterni, e in base alla tipologia, verra aperto l'apposito menù con le varie scelte disponibili.


