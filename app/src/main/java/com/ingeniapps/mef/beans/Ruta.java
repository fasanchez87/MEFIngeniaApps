package com.ingeniapps.mef.beans;

/**
 * Created by Ingenia Applications on 2/02/2018.
 */

public class Ruta
{
    private String codRuta;
    private String codUsuario;
    private String desRuta;
    private String desOrigen;

    public String getPorRuta() {
        return porRuta;
    }

    public void setPorRuta(String porRuta) {
        this.porRuta = porRuta;
    }

    private String porRuta;

    public String getColRuta() {
        return colRuta;
    }

    public void setColRuta(String colRuta) {
        this.colRuta = colRuta;
    }

    private String colRuta;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public String getCodRuta() {
        return codRuta;
    }

    public void setCodRuta(String codRuta) {
        this.codRuta = codRuta;
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getDesRuta() {
        return desRuta;
    }

    public void setDesRuta(String desRuta) {
        this.desRuta = desRuta;
    }

    public String getDesOrigen() {
        return desOrigen;
    }

    public void setDesOrigen(String desOrigen) {
        this.desOrigen = desOrigen;
    }

    public String getDesDestino() {
        return desDestino;
    }

    public void setDesDestino(String desDestino) {
        this.desDestino = desDestino;
    }

    public String getFecCreacion() {
        return fecCreacion;
    }

    public void setFecCreacion(String fecCreacion) {
        this.fecCreacion = fecCreacion;
    }

    private String desDestino;
    private String fecCreacion;


}
