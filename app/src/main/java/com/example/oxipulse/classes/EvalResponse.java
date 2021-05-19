package com.example.oxipulse.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EvalResponse {

        @SerializedName("Codigo")
        @Expose
        private String codigo;
        @SerializedName("Grado_de_urgencia")
        @Expose
        private float gradoDeUrgencia;
        @SerializedName("Limite")
        @Expose
        private String limite;
        @SerializedName("Triage")
        @Expose
        private String triage;

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public float getGradoDeUrgencia() {
            return gradoDeUrgencia;
        }

        public void setGradoDeUrgencia(float gradoDeUrgencia) {
            this.gradoDeUrgencia = gradoDeUrgencia;
        }

        public String getLimite() {
            return limite;
        }

        public void setLimite(String limite) {
            this.limite = limite;
        }

        public String getTriage() {
            return triage;
        }

        public void setTriage(String triage) {
            this.triage = triage;
        }

    public void getEval(){

    }

}
