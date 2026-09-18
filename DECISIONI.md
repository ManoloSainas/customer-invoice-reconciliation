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

## Parsing CSV

* È stato implementato un parser CSV minimale con le funzionalità standard di Java, evitando librerie esterne.
* La scelta è dovuta alla semplicità e alle dimensioni dei file forniti, per mantenere il progetto leggero e ridurre dipendenze non necessarie.

## Verifica VIES

* Una risposta `valid` indica che la partita IVA è stata verificata con esito positivo.
* Una risposta `invalid`, `error` o `non_supportato` non interrompe l'elaborazione dell'intero batch: l'esito viene mantenuto e riportato nella riconciliazione.
* Una partita IVA assente dal mock VIES non viene considerata automaticamente invalida, ma come non verificata.

## Associazione fatture-clienti

* L'associazione viene effettuata prioritariamente tramite `cliente_id`, considerato l'identificativo univoco del cliente.
* Se `cliente_id` è mancante o non presente nel registro, viene utilizzato `cliente_nome` normalizzato come criterio alternativo.
* La corrispondenza tramite nome viene accettata automaticamente solo quando identifica un unico cliente.
* In caso di nessuna corrispondenza o di più clienti con lo stesso nome, la fattura non viene associata automaticamente.
