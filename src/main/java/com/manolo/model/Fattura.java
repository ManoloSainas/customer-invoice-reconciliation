package com.manolo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Fattura {

    private String idFattura;
    private String clienteId;
    private String clienteNome;
    private LocalDate dataEmissione;
    private String valuta;
    private BigDecimal importo;

    public String getIdFattura() {
        return idFattura;
    }

    public void setIdFattura(String idFattura) {
        this.idFattura = idFattura;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public LocalDate getDataEmissione() {
        return dataEmissione;
    }

    public void setDataEmissione(LocalDate dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public String getValuta() {
        return valuta;
    }

    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }
}
