package Service;

import Domain.Inchiriere;
import Domain.Masina;
import Exceptions.ItemNotFoundException;
import Exceptions.RepositoryException;
import Repository.RepoInchirieri;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceInchiriere {
    private final RepoInchirieri repoInchirieri;

    public ServiceInchiriere(RepoInchirieri repoInchirieri) {
        this.repoInchirieri = Objects.requireNonNull(repoInchirieri, "Repository-ul nu poate fi null.");
    }

    public void add(Inchiriere inchiriere) throws RepositoryException {
        validateInchiriere(inchiriere);
        repoInchirieri.add(inchiriere);
    }

    public void remove(int id) throws ItemNotFoundException {
        repoInchirieri.remove(id);
    }

    public Inchiriere find(int id) {
        return Optional.ofNullable(repoInchirieri.find(id))
                .orElseThrow(() -> new IllegalArgumentException("Închirierea cu ID-ul " + id + " nu există."));
    }

    public List<Inchiriere> getAllInchirieri() {
        return repoInchirieri.getAll();
    }

    public void update(Inchiriere inchiriere) throws RepositoryException {
        validateInchiriere(inchiriere);
        repoInchirieri.update(inchiriere);
    }

    public List<Map.Entry<Masina, Long>> getMasiniDesInchiriate() {
        return repoInchirieri.getAll().stream()
                .collect(Collectors.groupingBy(Inchiriere::getMasina, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Masina, Long>comparingByValue().reversed())
                .toList();
    }

    public List<Map.Entry<String, Long>> getInchirieriPeLuna() {
        return repoInchirieri.getAll().stream()
                .collect(Collectors.groupingBy(
                        inchiriere -> formatDateToYearMonth(inchiriere.getDataInceput()),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .toList();
    }

    public List<Map.Entry<Masina, Long>> getMasiniZileInchiriere() {
        return repoInchirieri.getAll().stream()
                .collect(Collectors.groupingBy(
                        Inchiriere::getMasina,
                        Collectors.summingLong(this::calculateRentalDays)))
                .entrySet().stream()
                .sorted(Map.Entry.<Masina, Long>comparingByValue().reversed())
                .toList();
    }


    private void validateInchiriere(Inchiriere inchiriere) {
        if (inchiriere == null || inchiriere.getMasina() == null) {
            throw new IllegalArgumentException("Închirierea sau mașina asociată nu poate fi null.");
        }
    }

    private String formatDateToYearMonth(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Data nu poate fi null.");
        }
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
        return String.format("%04d-%02d", localDate.getYear(), localDate.getMonthValue());
    }

    private long calculateRentalDays(Inchiriere inchiriere) {
        if (inchiriere.getDataInceput() == null || inchiriere.getDataSfarsit() == null) {
            System.err.println("Date invalide pentru închirierea cu ID " + inchiriere.getId());
            return 0L;
        }
        long differenceMillis = inchiriere.getDataSfarsit().getTime() - inchiriere.getDataInceput().getTime();
        return differenceMillis / (1000 * 60 * 60 * 24);
    }
}
