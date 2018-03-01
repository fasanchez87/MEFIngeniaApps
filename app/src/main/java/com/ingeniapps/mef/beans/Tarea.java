package com.ingeniapps.mef.beans;

/**
 * Created by Ingenia Applications on 6/02/2018.
 */

public class Tarea
{
    private String codRuta;
    private String codMeta;
    private String codTarea;
    private String desTarea;

    public String getCodRuta() {
        return codRuta;
    }

    public void setCodRuta(String codRuta) {
        this.codRuta = codRuta;
    }

    public String getCodMeta() {
        return codMeta;
    }

    public void setCodMeta(String codMeta) {
        this.codMeta = codMeta;
    }

    public String getCodTarea() {
        return codTarea;
    }

    public void setCodTarea(String codTarea) {
        this.codTarea = codTarea;
    }

    public String getDesTarea() {
        return desTarea;
    }

    public void setDesTarea(String desTarea) {
        this.desTarea = desTarea;
    }

    public String getFecTarea() {
        return fecTarea;
    }

    public void setFecTarea(String fecTarea) {
        this.fecTarea = fecTarea;
    }

    public String getCodEstado() {
        return codEstado;
    }

    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }

    public String getFecFinal() {
        return fecFinal;
    }

    public void setFecFinal(String fecFinal) {
        this.fecFinal = fecFinal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIndCheck() {
        return indCheck;
    }

    public void setIndCheck(boolean indCheck) {
        this.indCheck = indCheck;
    }

    private String fecTarea;
    private String codEstado;
    private String fecFinal;
    private String type;
    private boolean indCheck;
}
