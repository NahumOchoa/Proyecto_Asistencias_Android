package com.upv.pm_2022.sep_dic.lab27;

import java.util.ArrayList;

public class Estudiantes {
    private int Id;
    private String NombreCompleto;
    private int Asistencia;
    private int Retardos;
    private int Faltas;
    private ArrayList<String> fechasAsistencias = new ArrayList<>();
    public String[] FechaAsistencia= null;




    public Estudiantes(int id, String nombreCompleto, int asistencia, int retardos, int faltas) {
        this.Id = id;
        this.NombreCompleto = nombreCompleto;
        this.Asistencia = asistencia;
        this.Retardos = retardos;
        this.Faltas = faltas;

    }
    public String getNombreCompleto() {
        return this.NombreCompleto;
    }
    public void setNombreCompleto(String nombreCompleto) {
        this.NombreCompleto = nombreCompleto;
    }
    public int getAsistencia() {
        return this.Asistencia;
    }
    public void setAsistencia(int asistencia) {
        this.Asistencia = asistencia;
    }
    public int getRetardos() {
        return this.Retardos;
    }
    public void setRetardos(int retardos) {
        this.Retardos = retardos;
    }
    public int getFaltas() {
        return this.Faltas;
    }
    public void setFaltas(int faltas) {
        this.Faltas = faltas;
    }
    public int getId() {
        return this.Id;
    }
    public void setId(int id) {
        this.Id = id;
    }
    public String[] getFechaAsistencia() {
        return this.FechaAsistencia;
    }
    public void setfechasAsistencias(String fechaAsistencia) {
        this.fechasAsistencias.add(fechaAsistencia);
    }
    public ArrayList<String> getFechasAsistencias() {
        return this.fechasAsistencias;
    }
    public void setFechaAsistencia(String[] fechaAsistencia) {
        this.FechaAsistencia = fechaAsistencia;
    }
    public String toString(){
        return this.NombreCompleto;
    }


}
