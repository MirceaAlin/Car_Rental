package Domain;

import java.io.Serializable;

public class Masina extends Entitate implements Serializable {
    private String marca;
    private String model;

    public Masina(int id, String marca, String model) {
        super(id);
        this.marca = marca;
        this.model = model;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return getId() + "," + marca + "," +  model;
    }
}
