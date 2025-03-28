package Service;

import Domain.Masina;
import Exceptions.ItemNotFoundException;
import Exceptions.RepositoryException;
import Repository.RepoMasini;

import java.util.List;
import java.util.Objects;

public class ServiceMasina {
    private final RepoMasini repoMasini;

    public ServiceMasina(RepoMasini repoMasini) {
        this.repoMasini = Objects.requireNonNull(repoMasini, "Repository-ul nu poate fi null.");
    }

    public void add(Masina masina) throws RepositoryException {
        validateMasina(masina);
        repoMasini.add(masina);
    }

    public void remove(int id) throws ItemNotFoundException {
        repoMasini.remove(id);
    }

    public Masina find(int id) {
        Masina masina = repoMasini.find(id);
        if (masina == null) {
            throw new IllegalArgumentException("Mașina cu ID-ul " + id + " nu există.");
        }
        return masina;
    }

    public List<Masina> getAllMasini() {
        return repoMasini.getAll();
    }

    public void update(Masina masina) throws RepositoryException {
        if (masina == null) {
            throw new IllegalArgumentException("Mașina nu e valida.");
        }
        repoMasini.update(masina);
    }

    private void validateMasina(Masina masina) {
        if (masina == null) {
            throw new IllegalArgumentException("Mașina nu poate fi null.");
        }
        if (masina.getMarca() == null || masina.getMarca().isBlank()) {
            throw new IllegalArgumentException("Marca mașinii nu poate fi null sau goală.");
        }
        if (masina.getModel() == null || masina.getModel().isBlank()) {
            throw new IllegalArgumentException("Modelul mașinii nu poate fi null sau gol.");
        }
    }
}

