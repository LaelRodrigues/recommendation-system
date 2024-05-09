package br.com.concorrente;

import java.io.Serializable;

public class DataRequest implements Serializable {

    private String user;
    private int numRecomendations;

    public DataRequest(String user, int numRecomendations) {
        this.user = user;
        this.numRecomendations = numRecomendations;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setNumRecomendations(int numRecomendations) {
        this.numRecomendations = numRecomendations;
    }

    public String getUser() {
        return user;
    }

    public int getNumRecomendations() {
        return numRecomendations;
    }

}
