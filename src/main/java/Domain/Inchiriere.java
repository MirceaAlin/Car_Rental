package Domain;

import java.io.Serializable;
import java.util.Date;

public class Inchiriere extends Entitate implements Serializable {
    private Masina masina;
    private Date dataInceput;
    private Date dataSfarsit;

    public Inchiriere(int id, Masina masina, Date dataInceput, Date dataSfarsit) {
        super(id);
        this.masina = masina;
        this.dataInceput = dataInceput;
        this.dataSfarsit = dataSfarsit;
    }

    public Masina getMasina() {
        return masina;
    }

    public Date getDataInceput() {
        return dataInceput;
    }

    public Date getDataSfarsit() {
        return dataSfarsit;
    }

    public void setDataInceput(Date dataInceput) {
        if (dataInceput.after(this.dataSfarsit)) {
            throw new IllegalArgumentException("Data de început trebuie să fie înainte de data de sfârșit!");
        }
        this.dataInceput = dataInceput;
    }

    public void setDataSfarsit(Date dataSfarsit) {
        if (this.dataInceput != null && dataSfarsit.before(this.dataInceput)) {
            throw new IllegalArgumentException("Data de sfârșit trebuie să fie după data de început!");
        }
        this.dataSfarsit = dataSfarsit;
    }

    public boolean Suprapunere(Inchiriere altaInchiriere) {
        return !(this.dataSfarsit.before(altaInchiriere.dataInceput) ||
                this.dataInceput.after(altaInchiriere.dataSfarsit));
    }

    public String toString() {
        return getId() + "," + masina + "," + dataInceput + "," + dataSfarsit;
    }
}

