package com.example.oxipulse.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EvalResponse implements Serializable {

    //Modelo de EvalResponse
    //Este es la respuesta que se obtendra y contendra una lista Data el cual contiene los datos
    @SerializedName("data")
    private List<Data> data= null;

    //getter and setter
    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
