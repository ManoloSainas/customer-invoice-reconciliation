## Esecuzione

Il modo consigliato per eseguire il progetto è tramite Docker, in modo da non dipendere dalla versione di Java o Maven installata sulla macchina.

### Requisiti

* Docker con Docker Compose
* Connessione Internet, necessaria per recuperare i tassi di cambio tramite Frankfurter

Non è necessario installare Java, Maven o utilizzare un IDE.

### Clonazione ed esecuzione

Clonare il repository:

```bash
git clone https://github.com/ManoloSainas/customer-invoice-reconciliation.git
```

Entrare nella directory del progetto:

```bash
cd customer-invoice-reconciliation
```

Costruire l'immagine Docker:

```bash
docker compose build
```

Il comando costruisce l'immagine, compila il progetto ed esegue automaticamente la suite di test.

Successivamente avviare il tool con:

```bash
docker compose run --rm reconciliation
```

Il programma:

1. legge i file presenti nella directory `data/`;
2. esegue normalizzazione, associazione clienti-fatture, verifica VIES e conversione valutaria;
3. stampa un riepilogo dell'elaborazione;
4. genera il report finale in:

```text
output/report.json
```

Il file viene sovrascritto a ogni esecuzione, quindi il job può essere rieseguito senza accumulare o duplicare i risultati delle run precedenti.

### Esecuzioni successive

Dopo che l'immagine è stata costruita almeno una volta, per rieseguire il tool è sufficiente:

```bash
docker compose run --rm reconciliation
```

Se il codice o le dipendenze vengono modificati, ricostruire prima l'immagine:

```bash
docker compose build
docker compose run --rm reconciliation
```

## File di input

I dati utilizzati dal tool si trovano nella directory:

```text
data/
├── clienti.csv
├── fatture.csv
└── vies_mock.json
```

`vies_mock.json` simula il servizio VIES, mentre i tassi di cambio storici vengono recuperati tramite Frankfurter utilizzando i dati ECB.
