# Decisioni di progetto

## Setup iniziale

* Repository Git creato su GitHub.
* Progetto Maven creato con Java 25.
* File di input originali inseriti nella directory `data/`.

## Modello di elaborazione

* La classe `Fattura` rappresenta il dato della fattura in ingresso e non viene modificata per contenere errori o informazioni relative al processo di riconciliazione.
* Le informazioni prodotte durante la normalizzazione vengono rappresentate separatamente rispetto ai dati originali.
* Vengono utilizzati risultati distinti per la normalizzazione di clienti e fatture.
* Lo stato della normalizzazione è rappresentato da un enum comune a clienti e fatture con i valori `VALIDO`, `NORMALIZZATO` ed `ERRORE`.
* I risultati della normalizzazione contengono una lista di problemi, in modo da poter registrare più anomalie contemporaneamente senza perderne informazioni.
* Le informazioni prodotte durante l'elaborazione successiva vengono rappresentate separatamente, tramite appositi oggetti risultato.
* Questa separazione mantiene distinto il dato originale dalla sua elaborazione.
* L'architettura separa le responsabilità principali in `NormalizzazioneService`, `AssociazioneService`, `ViesService`, `CambioValutaService` e `RiconciliazioneService`, evitando di concentrare tutta la logica in un'unica classe.
* `RiconciliazioneService` orchestra la composizione dei risultati delle fasi precedenti e costruisce il report finale senza assumere la responsabilità del caricamento dei dati o del calcolo dei tassi di cambio.
* Il `Main` si limita a coordinare le diverse fasi dell'elaborazione e alla stampa del risultato finale, mantenendo la logica di dominio nei relativi service.

## Parsing CSV

* È stata utilizzata la libreria Apache Commons CSV per il parsing dei file CSV.
* Inizialmente era stato implementato un parsing minimale tramite `String.split(",")`, ma questo approccio non gestiva correttamente i campi CSV racchiusi tra virgolette contenenti virgole, come gli importi nel formato `1.234,56`.
* È stata quindi preferita una libreria standardizzata per gestire correttamente il formato CSV senza implementare manualmente un parser più complesso.
* La scelta consente di mantenere il codice più semplice e leggibile, evitando di dedicare una parte significativa del progetto alla gestione dei dettagli del formato CSV, che non rappresentano il focus del tool di riconciliazione.

## Gestione e normalizzazione dei dati

* I dati in ingresso vengono analizzati campo per campo in base al loro significato e alle regole del dominio.
* Viene distinta una situazione normalizzabile, quando il dato può essere reso interpretabile senza ambiguità, da una situazione non valida o mancante, che non può essere corretta in modo affidabile.
* Non vengono corretti automaticamente i dati quando non è possibile determinare il valore corretto in modo univoco e affidabile.
* I campi opzionali vuoti vengono mantenuti come valori assenti e non considerati automaticamente errori.
* Un valore presente ma non interpretabile secondo il formato atteso viene invece considerato non valido.
* La normalizzazione viene effettuata prima delle fasi di associazione, verifica VIES e conversione delle valute.
* La normalizzazione viene effettuata separatamente per clienti e fatture.
* I dati provenienti dai file vengono mantenuti il più possibile nel loro formato originale durante il caricamento. La conversione di valori che richiedono interpretazione, come gli importi monetari, viene demandata alla fase di normalizzazione.

### Normalizzazione dei clienti

* **ID cliente:** viene considerato un identificativo obbligatorio. Se mancante viene registrato il problema `ID cliente mancante`. Se presente vengono rimossi eventuali spazi iniziali e finali. Non viene effettuata in questa fase alcuna verifica dell'esistenza o unicità dell'identificativo.
* **Ragione sociale:** vengono rimossi gli spazi iniziali e finali e le sequenze di più spazi vengono ridotte a un singolo spazio, per rendere più affidabile il confronto dei nomi.
* **Ragione sociale:** vengono inoltre normalizzate alcune forme societarie equivalenti presenti nei dati, ad esempio `S.r.l.`, `S.r.l` e `SRL` vengono rappresentate come `SRL`, mentre `S.p.A.`, `S.p.A`, `SpA` e `SPA` vengono rappresentate come `SPA`. La normalizzazione è limitata alle forme gestite esplicitamente, evitando trasformazioni generiche della ragione sociale che potrebbero alterarne il significato.
* **Paese:** viene normalizzato al codice ISO 3166-1 alpha-2 tramite una `Map` contenente i valori presenti nel dataset. Con più tempo sarebbe preferibile utilizzare una libreria dedicata alla gestione dei Paesi.
* **Partita IVA:** viene rimossa la spaziatura e il valore viene convertito in maiuscolo. Se manca il prefisso di due lettere, viene aggiunto utilizzando il codice del Paese precedentemente normalizzato. Viene effettuato un controllo strutturale minimo, senza applicare regole specifiche per ogni Paese. La validità effettiva della partita IVA viene demandata alla verifica VIES. Con più tempo sarebbe stata valutata un'API o una libreria che permetta di verificare la struttura della partita IVA in base al Paese.
* **Tasso USD contrattuale:** se presente deve essere numerico e maggiore di zero. Un valore assente indica che per il cliente non è previsto un tasso USD contrattuale.

### Normalizzazione delle fatture

* **ID fattura:** viene considerato un identificativo obbligatorio. Se mancante viene registrato il problema `ID fattura mancante`. Se presente vengono rimossi eventuali spazi iniziali e finali. Non viene effettuata in questa fase alcuna verifica dell'unicità dell'identificativo.
* **ID cliente:** viene considerato un identificativo obbligatorio per l'associazione della fattura al cliente. Se mancante viene registrato il problema `ID cliente mancante`. Se presente vengono rimossi eventuali spazi iniziali e finali. La verifica dell'esistenza dell'ID nel registro clienti viene demandata alla successiva fase di associazione.
* **Nome cliente:** viene normalizzato con le stesse regole utilizzate per la ragione sociale dei clienti, in modo che i dati provenienti dalle due sorgenti siano confrontabili. Vengono rimossi gli spazi iniziali e finali, ridotti gli spazi multipli e normalizzate le forme societarie gestite esplicitamente, come `S.r.l.` → `SRL` e `S.p.A.` → `SPA`.
* **Associazione cliente:** la normalizzazione della fattura non verifica l'esistenza dell'`idCliente` e non associa la fattura al cliente. L'associazione viene effettuata in una fase successiva.
* **Data di emissione:** viene rappresentata tramite `LocalDate`. La validità della data viene quindi demandata al parsing della data durante il caricamento; una data assente viene invece registrata come problema.
* **Valuta:** viene rimossa la spaziatura iniziale e finale e il codice viene convertito in maiuscolo. Viene verificato che abbia il formato di una sigla composta da tre lettere. La verifica della disponibilità del relativo tasso di cambio viene demandata alla fase successiva di conversione valutaria.
* **Importo:** viene mantenuto come `String` durante il caricamento per preservare il formato originale e viene convertito durante la normalizzazione in un formato numerico coerente. Sono supportati valori come `1234.56`, `1234,56` e `1.234,56`. Un importo mancante o non interpretabile viene considerato un errore.
* **Importi negativi:** vengono considerati valori validi dal punto di vista della normalizzazione, purché siano numericamente interpretabili. Non bloccano quindi l'elaborazione, poiché possono rappresentare casi legittimi come storni o note di credito.

## Associazione fatture-clienti

* L'associazione viene effettuata prioritariamente tramite `cliente_id`, considerato l'identificativo univoco del cliente.
* Se `cliente_id` è presente ma non corrisponde ad alcun cliente nel registro, viene tentata una seconda associazione tramite `cliente_nome` normalizzato.
* Se `cliente_id` è mancante, viene utilizzato direttamente `cliente_nome` come criterio alternativo.
* La corrispondenza tramite nome viene accettata automaticamente solo quando identifica un unico cliente.
* Se `cliente_id` è mancante o inesistente e l'associazione tramite nome riesce, la fattura viene associata ma il problema relativo all'ID viene comunque mantenuto nel risultato.
* In caso di nessuna corrispondenza tramite nome, la fattura non viene associata e viene registrato il relativo problema.
* In caso di più clienti con lo stesso nome, la fattura non viene associata automaticamente per evitare una scelta arbitraria.
* L'associazione utilizza i risultati della normalizzazione dei clienti e delle fatture, così da confrontare dati già resi coerenti.
* Un cliente con `StatoNormalizzazione.ERRORE` può comunque essere utilizzato per l'associazione se la sua identità è determinabile in modo affidabile tramite ID o tramite un nome univoco. I problemi di qualità del cliente vengono mantenuti e potranno essere riportati nelle fasi successive.
* L'associazione viene rappresentata tramite `RisultatoAssociazione` invece di modificare `Fattura`, mantenendo separati i dati di input dai risultati dell'elaborazione.
* Il metodo utilizzato per l'associazione viene rappresentato tramite l'enum `MetodoAssociazione`, con valori distinti per associazione tramite ID e tramite nome.

## Verifica VIES

* Una risposta `valid` indica che la partita IVA è stata verificata con esito positivo.
* Una risposta `invalid` indica che la partita IVA è stata verificata ma risulta formalmente errata o inesistente.
* Una risposta `error` indica un errore temporaneo del servizio VIES simulato. L'errore non interrompe l'elaborazione dell'intero batch e viene riportato nel risultato della riconciliazione.
* Una risposta `non_supportato` indica che il Paese della partita IVA non è coperto dal servizio VIES.
* Una partita IVA assente dal mock VIES non viene considerata automaticamente invalida, ma come `NON_VERIFICATA`, distinguendo l'assenza della risposta da una risposta esplicita `invalid`.
* Una partita IVA mancante nel dato cliente viene rappresentata come `MANCANTE`, distinguendola sia da una partita IVA esplicitamente `INVALID` sia da una partita IVA presente ma non verificabile tramite il mock.
* Il risultato della verifica VIES viene rappresentato separatamente dal cliente tramite `RisultatoVies`, senza modificare `Cliente`.
* L'esito VIES viene rappresentato tramite l'enum `EsitoVies`, con i valori `VALID`, `INVALID`, `ERROR`, `NON_SUPPORTATO`, `NON_VERIFICATA` e `MANCANTE`. Non viene utilizzata una lista di problemi perché la verifica produce un singolo esito per partita IVA.
* Il mock VIES viene letto tramite Jackson (`ObjectMapper`) invece di implementare manualmente il parsing del JSON. La libreria viene utilizzata per estrarre la sezione `risposte` e convertirla direttamente in una `Map<String, String>`.
* La responsabilità del caricamento del mock è separata dalla logica di verifica: `ViesRepository` si occupa della lettura dei dati, mentre `ViesService` interpreta gli esiti restituiti dal mock.
* La verifica VIES viene effettuata sui clienti effettivamente associati ad almeno una fattura. I clienti presenti nel registro ma non coinvolti in alcuna fattura non vengono verificati, poiché la verifica non produrrebbe informazioni necessarie alla riconciliazione corrente.
* La verifica VIES viene effettuata una sola volta per ciascun cliente associato, anche quando lo stesso cliente compare in più fatture. Il risultato VIES è quindi riferito al cliente e non alla singola fattura, evitando verifiche e risultati duplicati.
* Se una fattura non può essere associata a un cliente, il `ViesService` non produce un esito VIES per quella fattura e lascia la gestione del problema alla fase di riconciliazione finale.
* Il `ViesService` si occupa esclusivamente di interpretare le risposte del mock VIES e produrre i relativi `RisultatoVies`, senza gestire i problemi di associazione delle fatture.
* Gli esiti VIES diversi da `VALID` vengono riportati come anomalie nel risultato della riconciliazione. Un'anomalia VIES non rende automaticamente la fattura non processabile, se i dati necessari alla riconciliazione economica sono comunque disponibili.

## Conversione delle valute

* Gli importi in EUR non richiedono una conversione e vengono mantenuti come importi originali.
* Per i clienti con un tasso USD contrattuale valido, le fatture in USD vengono convertite utilizzando esclusivamente il tasso contrattuale indicato nel registro clienti, indipendentemente dalla data della fattura.
* Per le altre valute, il tasso storico viene richiesto a Frankfurter utilizzando la data di emissione della fattura e il provider ECB.
* Gli importi convertiti in EUR vengono arrotondati a due cifre decimali utilizzando `RoundingMode.HALF_UP`, in modo da rappresentare il risultato monetario in centesimi di euro.
* Un errore rilevato durante la normalizzazione non rende automaticamente la fattura non processabile per tutte le fasi successive. Ogni fase verifica autonomamente la presenza dei dati necessari alla propria elaborazione.
* Ad esempio, la mancanza dell'ID cliente non impedisce la conversione se la fattura viene successivamente associata tramite nome e sono disponibili importo, valuta e data. Al contrario, la mancanza dell'importo o della valuta impedisce la conversione.
* Se la fattura non può essere associata a un cliente, non viene effettuata la conversione poiché manca un'associazione affidabile necessaria alla riconciliazione.
* Se Frankfurter non restituisce un tasso utilizzabile, la fattura viene mantenuta nel report ma viene considerata non processabile e non viene incluso alcun importo EUR nei totali.
* Gli errori del servizio Frankfurter, come una valuta non supportata, non interrompono l'elaborazione delle altre fatture.
* Il tasso di cambio utilizzato viene conservato nel risultato della conversione insieme all'importo originale e all'importo convertito, così da rendere il risultato verificabile.

## Gestione degli errori di elaborazione

* L'elaborazione viene eseguita a livello di singola fattura: un errore su una fattura non deve interrompere l'elaborazione delle altre.
* Quando un dato può essere normalizzato in modo univoco e affidabile, viene normalizzato e l'elaborazione prosegue.
* Quando un dato è mancante o non può essere interpretato in modo affidabile, la fattura viene considerata non processabile e viene esclusa dai calcoli, mantenendo comunque traccia dell'errore nel risultato finale.
* Gli errori temporanei relativi a servizi esterni non interrompono l'elaborazione dell'intero batch.
* Una fattura non processabile viene comunque inclusa nel risultato finale con l'indicazione del problema, evitando che i dati problematici vengano semplicemente ignorati.
* La proprietà `processabile` rappresenta la possibilità di completare la riconciliazione economica della singola fattura. Le anomalie informative, come un esito VIES diverso da `VALID`, vengono invece mantenute nella lista dei problemi senza bloccare automaticamente il calcolo dell'importo EUR.

## Riconciliazione finale

* La riconciliazione finale combina i risultati delle fasi precedenti senza ricalcolare le operazioni già effettuate.
* Per ogni fattura viene creato un `RisultatoRiconciliazione` che contiene la fattura, il cliente associato, il metodo di associazione, l'esito VIES, l'importo originale, la valuta originale, l'importo in EUR, il tasso di cambio, lo stato di processabilità e l'elenco dei problemi.
* Il risultato della riconciliazione viene mantenuto separato da `Fattura`, così da non modificare il dato di input con informazioni prodotte durante l'elaborazione.
* I problemi provenienti dalle diverse fasi vengono aggregati nel risultato finale, permettendo di conservare sia le anomalie non bloccanti sia gli errori che impediscono la riconciliazione.
* Una fattura non associata a un cliente non viene considerata processabile e non contribuisce al totale EUR.
* Una fattura con conversione non disponibile non viene considerata processabile e non contribuisce al totale EUR.
* Una fattura con un'anomalia VIES può rimanere processabile se l'importo EUR è stato determinato correttamente.
* Gli importi negativi non vengono esclusi automaticamente dal totale, poiché sono considerati valori numericamente validi e possono rappresentare storni o note di credito.

## Report complessivo

* Il report complessivo viene rappresentato tramite `ReportRiconciliazione`, separato dai risultati delle singole fatture.
* `ReportRiconciliazione` contiene l'elenco dei risultati per fattura, il numero totale delle fatture, il numero di fatture processabili, il numero di fatture non processabili e il totale EUR riconciliato.
* Il totale EUR viene calcolato sommando esclusivamente gli importi EUR delle fatture `processabile=true`.
* Le fatture non processabili rimangono comunque presenti nel report con i relativi problemi, ma non contribuiscono al totale.
* Il report contiene inoltre un riepilogo delle modalità di associazione, distinguendo le fatture associate tramite ID, tramite nome e non associate.
* Il report contiene inoltre un riepilogo degli esiti VIES prodotti sui clienti effettivamente associati.
* La stampa finale viene effettuata dopo il completamento delle fasi di elaborazione, mantenendo il `Main` principalmente come orchestratore del flusso.
* È stato aggiunto un generatore dedicato al report JSON (`ReportWriter`) per produrre un formato esterno più compatto rispetto alla serializzazione diretta del modello interno.
* Il modello utilizzato per il JSON (`RisultatoReportJson`) espone solamente i dati necessari al report, evitando di serializzare nuovamente gli oggetti interni `Fattura`, `Cliente` e `RisultatoVies`.
* Il formato JSON contiene il riepilogo generale, il riepilogo delle associazioni, il riepilogo degli esiti VIES e i risultati delle singole fatture.
* Il report JSON viene scritto nel percorso `output/report.json`. La directory viene creata automaticamente se non esiste.
* Ad ogni esecuzione il file viene sovrascritto, evitando duplicazioni o residui derivanti da esecuzioni precedenti.
* Gli errori durante la scrittura del report JSON vengono gestiti dal `ReportWriter` senza modificare la logica di riconciliazione.
* Le date del modello vengono serializzate nel formato ISO, utilizzando il modulo Jackson `JavaTimeModule` e disabilitando la serializzazione delle date come timestamp.
* Il report JSON è considerato un formato di output separato dal modello interno: eventuali modifiche alla struttura del JSON possono quindi essere effettuate senza modificare la logica della riconciliazione.

## Test

* Sono stati realizzati test automatici sulle principali regole e casistiche considerate più rilevanti per il funzionamento del tool.
* I test sono stati generati con il supporto dell'AI, richiedendo esplicitamente la verifica delle casistiche più importanti e rappresentative delle principali decisioni di dominio.
* La suite attuale copre le principali aree di rischio individuate: normalizzazione degli importi, gestione degli importi mancanti, non numerici e negativi, associazione tramite ID e tramite nome, distinzione degli esiti VIES, utilizzo del tasso USD contrattuale, gestione degli importi in EUR, gestione di un ID fattura mancante, errori del servizio di cambio e comportamento della riconciliazione in presenza di anomalie VIES.
* Sono stati inclusi anche test di robustezza per verificare che dati non interpretabili o errori dei servizi esterni non provochino eccezioni non gestite durante l'elaborazione della singola fattura.
* Sono stati aggiunti test specifici per `ReportWriter`, verificando la creazione del report JSON e la presenza dei principali dati riepilogativi.
* È stato aggiunto un test che verifica la sovrascrittura del report JSON esistente, così da verificare il comportamento in caso di esecuzioni ripetute.
* I test non hanno l'obiettivo di coprire ogni possibile combinazione di input, ma di verificare le regole fondamentali sulle quali si basa il comportamento del tool.
* La suite finale comprende 15 test, tutti superati, senza failure, errori o test saltati.
* È stata inoltre eseguita un'elaborazione completa sui dati forniti, composta da 28 fatture. L'elaborazione ha prodotto 24 fatture processabili e 4 non processabili, senza interrompere il batch in presenza di errori relativi a singole fatture.
* Nell'elaborazione completa è stato verificato anche il fallback dell'associazione tramite nome: una fattura con `cliente_id` mancante può essere associata a un cliente univoco tramite nome e rimanere processabile, mantenendo comunque l'anomalia relativa all'ID nel risultato.
* Durante l'esecuzione reale è stato verificato anche il comportamento in presenza di un errore del servizio Frankfurter: la fattura interessata viene mantenuta nel report come non processabile, senza impedire l'elaborazione delle fatture successive.
* È stata verificata inoltre la riesecuzione del programma sullo stesso percorso di output, confermando che il file `output/report.json` viene sovrascritto senza generare duplicazioni.
* Con più tempo a disposizione sarebbe stata ampliata la suite per coprire ulteriori casi limite e combinazioni di anomalie, in particolare scenari aggiuntivi di normalizzazione, associazione ambigua, errori dei servizi esterni, conversioni valutarie e composizione del report finale.
