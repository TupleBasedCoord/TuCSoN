package it.unibo.sd.jade.coordination;

import java.util.LinkedList;
import java.util.List;

import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * Classe utilizzata per memorizzare/reperire i risultati delle operazioni di
 * coordinazione ottenuti da un behaviour JADE
 * 
 * @author lucasangiorgi
 * 
 */
public class ResultOpStorage {

    private final List<TucsonOpCompletionEvent> list;
    // prossimo risultato da usare, utilizzato per restituire il risultato
    // corretto o per sapere se quel risultato non è presente e quindì procedere
    // con l'operazione
    private int nextRes;
    /*
     * variabile utilizzata per sapere se i risultati sono stati ottenuti o ce
     * ne sono ancora di pendenti (es se è si allora è nella fase in cui è stata
     * chiamata l'asincrona ma il comportamento che è stato bloccato si
     * risveglia perchè è stato ricevuto un messaggio dall'agente senza che
     * l'operazione sia stata completata, questo permette di interrompere subito
     * l'operazione tornando null e rimettendo nella BlockQueue il comportamento
     */
    private boolean ready;

    /**
     * 
     */
    public ResultOpStorage() {
        this.list = new LinkedList<TucsonOpCompletionEvent>();
        this.nextRes = 0;
        this.ready = false;
    }

    /**
     * @return the list
     */
    public List<TucsonOpCompletionEvent> getList() {
        return this.list;
    }

    /**
     * @return the next_res
     */
    public int getNextRes() {
        return this.nextRes;
    }

    /**
     * @param n
     *            the next_res to set
     */
    public void setNextRes(final int n) {
        this.nextRes = n;
    }

    /**
     * @return the ready
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * @param r
     *            the ready to set
     */
    public void setReady(final boolean r) {
        this.ready = r;
    }
}
