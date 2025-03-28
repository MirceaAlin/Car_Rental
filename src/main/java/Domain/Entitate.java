package Domain;

import java.io.Serializable;

public class Entitate implements Serializable {
    private int id;

    public Entitate(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
