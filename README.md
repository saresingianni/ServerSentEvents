# Eventi inviati dal server su React e Spring WebFlux
Ascolta gli eventi inviati dal server in tempo tramite  un'applicazione client React (  [tratto su articolo Medium](https://medium.com/better-programming/server-sent-events-on-react-and-spring-webflux-5f532b04633))

# Che cos'è un evento inviato dal server?

Un evento inviato dal server è uno standard che consente a un client (browser) di ascoltare un flusso di eventi su una connessione HTTP standard di lunga durata anziché eseguire il polling di un server o stabilire un WebSocket.

SSE consente al browser di ricevere aggiornamenti in tempo reale una volta stabilita la connessione. Questa è una tecnologia push server in cui il client avvia la connessione per ricevere gli aggiornamenti e mantiene attiva la connessione fino alla chiusura dell'origine dell'evento.

Un evento inviato dal server ha uno standard specifico sul modo in cui gli eventi vengono inviati. Il tipo di contenuto di un flusso SSE deve essere flusso di testo / evento e l'evento stesso è separato da due caratteri di nuova riga ( `\n\n`) che indicano la fine di un evento.

    data: {"cpuUsage": 57, "memoryUsage": 73, "date": "05-01-2020"}
    data: {"cpuUsage": 9, "memoryUsage": 81, "date": "05-01 -2020" }

# SSE vs. WebSocket

Ci si potrebbe chiedere quale sia la differenza tra un WebSocket e SSE, poiché entrambi sembrano molto simili.
Bene, ci sono alcune caratteristiche che differenziano un WebSocket da un SSE.

La differenza principale tra un WS e un SSE è che gli eventi inviati dal server sono unidirezionali, il che significa che è una connessione unidirezionale dal server al browser (ad es. Sistemi di monitoraggio). 
Ma i WebSocket sono bidirezionali, il che significa che sia il client che il server possono inviare aggiornamenti. (ad es. applicazioni di chat).

SSE può essere trasportato su HTTP semplice, a differenza di WebSocket, che utilizza un protocollo WebSocket dopo l'handshake TCP. 
Quando si utilizza SSE, non è necessario preoccuparsi che la connessione venga bloccata da alcuni firewall e siano supportati dall'infrastruttura esistente, mentre alcuni firewall potrebbero bloccare le connessioni WebSocket durante l'ispezione dei pacchetti.

----------

# Spring WebFlux

Spring Framework 5 ha introdotto un nuovo modulo, [spring-webflux](https://docs.spring.io/spring-framework/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/web-reactive.html) , che supporta la programmazione react per scrivere applicazioni asincrone, non bloccanti e guidate da eventi in modo simile a Node.js.

Questo modulo supporta due modelli di programmazione:

-   Controller basati su annotazioni tradizionali simili a spring-webmvc.
-   Programmazione funzionale con funzioni lambda Java 8.

In spring-webflux, non si risponde con un semplice POJO rispetto a spring-webmvc. Invece, usiamo Mono e Flux:

-   `Mono<T>`: può restituire zero o un risultato.
-   `Flux<T>`: può restituire zero o molti risultati.

Non entrerò nei dettagli per quanto riguarda il modulo spring-webflux. Piuttosto, questa guida si concentra sulla scrittura di un ascoltatore di eventi in tempo reale in React. Puoi leggere di più [sulla loro documentazione ufficiale](https://docs.spring.io/spring-framework/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/web-reactive.html) .

## server

Iniziamo creando un'applicazione SpringBoot utilizzando [Spring Initializ](https://start.spring.io/) r e aggiungendo la dipendenza seguente:

    <dependency>
    <groupId>org.springframework.boot</group>
    <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
  
Ora creeremo un controller che emetterà eventi contenenti dati sull'utilizzo delle risorse ogni secondo:

Il metodo del controller sopra ( `getResourceUsage()`) emetterà eventi ogni secondo contenente un nuovo oggetto della `Usage`classe popolato con i valori casuali per `cpuUsage`e `memoryUsage`**,** rispettivamente, per simulare un flusso continuo di eventi.

Poiché gli eventi inviati dal server devono avere un tipo di contenuto di testo / flusso di eventi, dobbiamo aggiungere dopo il valore del percorso all'interno dell'annotazione.  `produces = MediaType.TEXT_EVENT_STREAM_VALUE`  `GetMapping`

Prendi nota dell'annotazione `@CrossOrigin(allowedHeaders = “*”)`. Ho consentito a tutte le intestazioni di origine incrociata di tutti gli host, poiché chiameremo questo endpoint dall'applicazione React. Il browser bloccherà questa richiesta se non la consentiamo.

Questo è tutto ciò che devi fare per produrre un flusso di eventi in spring-webflux.

Ora avviamo l'applicazione e andiamo a [http: // localhost: 8080 / event / resources / use](http://localhost:8080/event/resources/usage) nel browser. Dovresti essere in grado di vedere un flusso di eventi emessi:

![](https://miro.medium.com/freeze/max/38/1*3_yDV_ufkWNX2wUjzctPRw.gif?q=20)

![](https://miro.medium.com/max/1425/1*3_yDV_ufkWNX2wUjzctPRw.gif)

## Applicazione client

Creeremo una semplice dashboard con due misuratori di livello per dimostrare gli eventi inviati dal server in tempo reale. Quindi creiamo un'applicazione React usando [create -eagire-app](https://reactjs.org/docs/create-a-new-react-app.html) :

npm create -eagire-app reazioni-sse-demo

Per visualizzare i dati di streaming, useremo il [tachimetro rea-d3.](https://www.npmjs.com/package/react-d3-speedometer) Installiamo il pacchetto usando il comando seguente:

npm reagisco-d3-tachimetro

È possibile utilizzare l'API JavaScript EventSource per iscriversi ai flussi di eventi. Ora crea un file chiamato `Dashboard.js`all'interno di una nuova directory chiamata `/src/components`e aggiungi il codice qui sotto. Non dimenticare di eseguire il rendering di questo componente in `App.js`:

Qui, abbiamo creato un componente funzionale e fatto uso degli hook React `[useState](https://reactjs.org/docs/hooks-reference.html#usestate)` [](https://reactjs.org/docs/hooks-reference.html#usestate) e `[useEffect](https://reactjs.org/docs/hooks-reference.html#useeffect)` [](https://reactjs.org/docs/hooks-reference.html#useeffect) per gestire lo stato e i metodi del ciclo di vita.

All'interno di `useEffect`**,** abbiamo creato un nuovo `EventSource`  oggetto passando l'URL del nostro endpoint. Ciò avvierà una connessione al nostro endpoint e inizierà l'ascolto. Nota l'array vuoto `[]`  come ultimo argomento `useEffect`  che chiamerà la funzione solo quando il componente è montato e smontato.

Il `EventSource.onmessage`  sarà chiamato ogni volta che un evento viene ricevuto dal client. All'interno della funzione, analizziamo `event.data`  un oggetto JSON e cambiamo lo stato `cpuUsage`e `memoryUsage`**,** rispettivamente, causandone il rendering del componente. Si noti che le variabili di stato `cpuUsage`e `memoryUsage`sono impostate come `value`proprietà di ReactSpeedometer.

Infine, dobbiamo chiudere il listener di eventi. Altrimenti, manterrà viva la connessione e continuerà ad ascoltare fino a quando non ci fermeremo. La restituzione di una funzione in `useEffect`  si comporta in modo simile al `componentWillUnmount()`metodo del ciclo di vita nei componenti basati su classi. Pertanto, quando il componente viene smontato, `EventSource.close()`  viene chiamato, costringendolo a interrompere e chiudere la connessione.

Bene, ora iniziamo l'applicazione eseguendo `npm start`.

Dovresti essere in grado di vedere la dashboard essere aggiornata in tempo reale quando gli eventi vengono inviati dal server.

![](https://miro.medium.com/freeze/max/38/1*3jmR16jN_zWZ218QhEKQJQ.gif?q=20)

![](https://miro.medium.com/max/1523/1*3jmR16jN_zWZ218QhEKQJQ.gif)

Grande! L'utilizzo di SSE dipende dal tipo di applicazione (ad esempio flussi di clic, dati IoT, strumenti di monitoraggio di server sintetici, tassi di cambio, ecc.). La fonte di eventi in questa guida è solo l'emissione di eventi casuali e l'implementazione reale potrebbe variare ma utilizza ancora gli stessi concetti.
