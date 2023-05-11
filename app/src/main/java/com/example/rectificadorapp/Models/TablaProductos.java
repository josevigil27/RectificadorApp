package com.example.rectificadorapp.Models;

import android.graphics.Bitmap;

public class TablaProductos {

    private Integer PRODUCTO;
    private String DESCRIPCIO;
    private String LINEA;
    private String PRECIO;
    private Bitmap IMAGEN;

    public TablaProductos(Integer producto, String descripcio, String linea, String precio, Bitmap imagen) {
        this.PRODUCTO = producto;
        this.DESCRIPCIO = descripcio;
        this.LINEA = linea;
        this.PRECIO = precio;
        this.IMAGEN = imagen;
    }

    public TablaProductos() { };

    public Integer getPRODUCTO() {return PRODUCTO;}

    public void setPRODUCTO(Integer producto) {this.PRODUCTO = producto;}

    public String getDESCRIPCIO() {return DESCRIPCIO;}

    public void setDESCRIPCIO(String descripcio) {this.DESCRIPCIO = descripcio;}

    public String getLINEA() {return LINEA;}

    public void setLINEA(String linea) {this.LINEA = linea;}

    public String getPRECIO() {return PRECIO;}

    public void setPRECIO(String precio) {this.PRECIO = precio;}

    public Bitmap getIMAGEN() {return IMAGEN;}

    public void setIMAGEN(Bitmap imagen) {this.IMAGEN = imagen;}
}
