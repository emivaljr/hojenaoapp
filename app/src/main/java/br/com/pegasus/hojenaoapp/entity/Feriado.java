package br.com.pegasus.hojenaoapp.entity;

/**
 * Created by emival on 6/9/15.
 */
public class Feriado {

    private String data;

    private String nome;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feriado feriado = (Feriado) o;

        return !(data != null ? !data.equals(feriado.data) : feriado.data != null);

    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }
}
