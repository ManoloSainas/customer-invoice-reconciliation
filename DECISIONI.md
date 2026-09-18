# Decisioni di progetto

## Setup iniziale

* Repository Git creato su GitHub.
* Progetto Maven creato con Java 25.
* File di input originali inseriti nella directory `data/`.

## Gestione e normalizzazione dei dati

* I dati in ingresso vengono analizzati campo per campo in base al loro significato e alle regole del dominio.
* Viene distinta una situazione normalizzabile, quando il dato può essere reso interpretabile senza ambiguità, da una situazione non valida o mancante, che non può essere corretta in modo affidabile.
* Non vengono corretti automaticamente i dati quando non è possibile determinare il valore corretto in modo univoco e affidabile.
* I campi opzionali vuoti vengono mantenuti come valori assenti e non considerati automaticamente errori.
* Un valore presente ma non interpretabile secondo il formato atteso viene invece considerato non valido.
* La normalizzazione viene effettuata prima delle fasi di associazione, verifica VIES e conversione delle valute.
* La normalizzazione viene effettuata separatamente per clienti e fatture.
* I dati provenienti dai file vengono mantenuti il più possibile nel loro formato originale durante il caricamento. La conversione di valori che richiedono interpretazione, come gli importi monetari, viene demandata alla fase di normalizzazione.

## Parsing CSV

* È stata utilizzata la libreria Apache Commons CSV per il parsing dei file CSV.
* Inizialmente era stato implementato un parsing minimale tramite `String.split(",")`, ma questo approccio non gestiva correttamente i campi CSV racchiusi tra virgolette contenenti virgole, come gli importi nel formato `1.234,56`.
* È stata quindi preferita una libreria standardizzata per gestire correttamente il formato CSV senza implementare manualmente un parser più complesso.
* La scelta consente di mantenere il codice più semplice e leggibile, evitando di dedicare una parte significativa del progetto alla gestione dei dettagli del formato CSV, che non rappresentano il focus del tool di riconciliazione.

## Verifica VIES

* Una risposta `valid` indica che la partita IVA è stata verificata con esito positivo.
* Una risposta `invalid`, `error` o `non_supportato` non interrompe l'elaborazione dell'intero batch: l'esito viene mantenuto e riportato nella riconciliazione.
* Una partita IVA assente dal mock VIES non viene considerata automaticamente invalida, ma come non verificata.

## Associazione fatture-clienti

* L'associazione viene effettuata prioritariamente tramite `cliente_id`, considerato l'identificativo univoco del cliente.
* Se `cliente_id` è mancante o non presente nel registro, viene utilizzato `cliente_nome` normalizzato come criterio alternativo.
* La corrispondenza tramite nome viene accettata automaticamente solo quando identifica un unico cliente.
* In caso di nessuna corrispondenza o di più clienti con lo stesso nome, la fattura non viene associata automaticamente.

## Gestione degli errori di elaborazione

* L'elaborazione viene eseguita a livello di singola fattura: un errore su una fattura non deve interrompere l'elaborazione delle altre.
* Quando un dato può essere normalizzato in modo univoco e affidabile, viene normalizzato e l'elaborazione prosegue.
* Quando un dato è mancante o non può essere interpretato in modo affidabile, la fattura viene considerata non processabile e viene esclusa dai calcoli, mantenendo comunque traccia dell'errore nel risultato finale.
* Gli errori temporanei relativi a servizi esterni non interrompono l'elaborazione dell'intero batch.

## Modello di elaborazione

* La classe `Fattura` rappresenta il dato della fattura in ingresso e non viene modificata per contenere errori o informazioni relative al processo di riconciliazione.
* Le informazioni prodotte durante la normalizzazione vengono rappresentate separatamente rispetto ai dati originali.
* Vengono utilizzati risultati distinti per la normalizzazione di clienti e fatture.
* Lo stato della normalizzazione è rappresentato da un enum comune a clienti e fatture con i valori `VALIDO`, `NORMALIZZATO` ed `ERRORE`.
* I risultati della normalizzazione contengono una lista di problemi, in modo da poter registrare più anomalie contemporaneamente senza perderne informazioni.
* Le informazioni prodotte durante l'elaborazione successiva vengono rappresentate separatamente, tramite un apposito oggetto risultato della riconciliazione.
* Questa separazione mantiene distinto il dato originale dalla sua elaborazione.
