package Repository;

import Domain.Inchiriere;
import Domain.Masina;
import Exceptions.ItemNotFoundException;
import Exceptions.RepositoryException;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepoInchirieri extends Repository<Inchiriere> {
    private Connection connection;

    public RepoInchirieri(String Db_Url) throws RepositoryException {
        ConnectionOpening(Db_Url);
        CreateTable();
    }

    private void CreateTable() throws RepositoryException {
        String create_table = """
        CREATE TABLE IF NOT EXISTS inchirieri (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            masina_id INTEGER NOT NULL,
            dataInceput DATE NOT NULL,
            dataSfarsit DATE NOT NULL,
            FOREIGN KEY (masina_id) REFERENCES masini(id) ON DELETE CASCADE
        );
        """;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(create_table);
        } catch (SQLException e) {
            throw new RepositoryException("A aparut o eroare la crearea tabelului 'inchirieri': " + e.getMessage());
        }
    }

    private void ConnectionOpening(String Db_Url) throws RepositoryException {
        SQLiteDataSource data_source = new SQLiteDataSource();
        data_source.setUrl(Db_Url);
        try {
            if (connection == null || connection.isClosed()) {
                connection = data_source.getConnection();
            }
        } catch (SQLException e) {
            throw new RepositoryException("A aparut o eroare la deschiderea conexiunii la baza de date!");
        }
    }

    public void add(Inchiriere inchiriere) throws RepositoryException {
        if (inchiriere.getDataInceput().after(inchiriere.getDataSfarsit())) {
            throw new RepositoryException("Data initiala trebuie sa fie inaitea celei finale!");
        }
        if (Suprapunere_Inchirieri(inchiriere)) {
            throw new RepositoryException("Inchirierea se suprapune!");
        }
        String insert_inchirieri = "INSERT INTO inchirieri (masina_id, dataInceput, dataSfarsit) VALUES (?, ?, ?);";
        try (PreparedStatement insertStatement = connection.prepareStatement(insert_inchirieri)) {
            insertStatement.setInt(1, inchiriere.getMasina().getId());
            insertStatement.setDate(2, new Date(inchiriere.getDataInceput().getTime()));
            insertStatement.setDate(3, new Date(inchiriere.getDataSfarsit().getTime()));
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la adăugarea închirierii: " + e.getMessage());
        }

    }


    public void remove(int id) throws ItemNotFoundException {
        Inchiriere toDelete = find(id);
        if (toDelete == null) {
            throw new ItemNotFoundException("Această închiriere nu există!");
        }
        String delete_inchiriere = "DELETE FROM inchirieri WHERE id = ?;";
        try (PreparedStatement deleteStatement = connection.prepareStatement(delete_inchiriere)) {
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            try {
                throw new RepositoryException("A aparut o eroare la ștergerea închirierii cu id-ul " + id);
            } catch (RepositoryException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    public Inchiriere find(int id) {
        String find_inchiriere = "SELECT * FROM inchirieri WHERE id = ?;";
        try (PreparedStatement findStatement = connection.prepareStatement(find_inchiriere)) {
            findStatement.setInt(1, id);
            ResultSet result = findStatement.executeQuery();
            if (result.next()) {
                int masinaId = result.getInt("masina_id");
                Masina masina = getMasinaById(masinaId);
                if (masina == null) {
                    throw new RepositoryException("Mașina asociată cu id-ul " + masinaId + " nu există!");
                }
                Date dataInceput = result.getDate("dataInceput");
                Date dataSfarsit = result.getDate("dataSfarsit");
                return new Inchiriere(id, masina, dataInceput, dataSfarsit);
            }
        } catch (SQLException | RepositoryException e) {
            throw new RuntimeException("Eroare la găsirea închirierii cu id-ul " + id, e);
        }
        return null;
    }

    public void update(Inchiriere inchiriere) throws RepositoryException {
        if (inchiriere == null) {
            throw new IllegalArgumentException("Închirierea nu poate fi null!");
        }
        if (Suprapunere_Inchirieri(inchiriere)) {
            throw new RepositoryException("Actualizarea nu poate fi efectuată, deoarece există o suprapunere cu altă închiriere!");
        }
        String update_inchiriere = "UPDATE inchirieri SET masina_id = ?, dataInceput = ?, dataSfarsit = ? WHERE id = ?;";
        try (PreparedStatement updateStatement = connection.prepareStatement(update_inchiriere)) {
            updateStatement.setInt(1, inchiriere.getMasina().getId());
            updateStatement.setDate(2, new Date(inchiriere.getDataInceput().getTime()));
            updateStatement.setDate(3, new Date(inchiriere.getDataSfarsit().getTime()));
            updateStatement.setInt(4, inchiriere.getId());
            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RepositoryException("Închirierea nu există!");
            }
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la actualizarea închirierii: " + e.getMessage());
        }
    }


    private Masina getMasinaById(int id) throws RepositoryException {
        String masinaDb_Url = "jdbc:sqlite:src/main/java/masina.db";
        Connection masinaConnection = null;
        try {
            masinaConnection = DriverManager.getConnection(masinaDb_Url);
            String findSQL = "SELECT * FROM masini WHERE id = ?;";
            try (PreparedStatement findStatement = masinaConnection.prepareStatement(findSQL)) {
                findStatement.setInt(1, id);
                ResultSet result = findStatement.executeQuery();
                if (result.next()) {
                    String marca = result.getString("marca");
                    String model = result.getString("model");
                    return new Masina(id, marca, model);
                } else {
                    throw new RepositoryException("Mașina cu ID-ul " + id + " nu există în masina.db.");
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la accesarea mașinii cu id-ul " + id + ": " + e.getMessage());
        } finally {
            try {
                if (masinaConnection != null) masinaConnection.close();
            } catch (SQLException e) {
                System.err.println("Eroare la închiderea conexiunii pentru masina.db: " + e.getMessage());
            }
        }
    }




    public List<Inchiriere> getAll() {
        List<Inchiriere> inchirieri = new ArrayList<>();
        String select_inchirieri = "SELECT * FROM inchirieri;";
        try (PreparedStatement selectStatement = connection.prepareStatement(select_inchirieri);
             ResultSet result = selectStatement.executeQuery()) {

            while (result.next()) {
                int id = result.getInt("id");
                int masinaId = result.getInt("masina_id");
                Date dataInceput = result.getDate("dataInceput");
                Date dataSfarsit = result.getDate("dataSfarsit");

                try {
                    Masina masina = getMasinaById(masinaId);

                    if (masina != null) {
                        Inchiriere inchiriere = new Inchiriere(id, masina, dataInceput, dataSfarsit);
                        inchirieri.add(inchiriere);
                    } else {
                        System.err.println("Închirierea cu ID " + id + " nu are o masina ce exista (ID mașină: " + masinaId + ").");
                    }
                } catch (RepositoryException e) {
                    System.err.println("A aparut o eroare la obținerea mașinii cu ID " + masinaId + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("A aparut o eroare la preluarea tuturor închirierilor: " + e.getMessage());
        }
        return inchirieri;
    }



    public boolean Suprapunere_Inchirieri(Inchiriere alta_inchiriere) {
        String query = "SELECT COUNT(*) FROM inchirieri WHERE masina_id = ? AND NOT (dataSfarsit < ? OR dataInceput > ?);";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, alta_inchiriere.getMasina().getId());
            stmt.setDate(2, new Date(alta_inchiriere.getDataInceput().getTime()));
            stmt.setDate(3, new Date(alta_inchiriere.getDataSfarsit().getTime()));
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt(1) > 0;
            }
        } catch (SQLException e) {
            try {
                throw new RepositoryException("Eroare la verificarea suprapunerilor: " + e.getMessage());
            } catch (RepositoryException ex) {
                throw new RuntimeException(ex);
            }
        }
        return false;
    }

    public void populateInchirieri() throws RepositoryException {
        RepoMasini repoMasini = new RepoMasini("jdbc:sqlite:masini.db");
        List<Masina> masini = repoMasini.getAll();
        if (masini.isEmpty()) {
            throw new RepositoryException("Nu există mașini în baza de date! Populați întâi tabelul 'masini'.");
        }

        for (int i = 0; i < 100; i++) {
            Masina randomMasina = masini.get((int) (Math.random() * masini.size()));

            long startDateMillis = System.currentTimeMillis() + (long) (Math.random() * 30L * 24L * 60L * 60L * 1000L); // maxim 30 zile în viitor
            long endDateMillis = startDateMillis + (long) (Math.random() * 10L * 24L * 60L * 60L * 1000L); // între 1-10 zile

            Date startDate = new Date(startDateMillis);
            Date endDate = new Date(endDateMillis);

            Inchiriere inchiriere = new Inchiriere(0, randomMasina, startDate, endDate);
            try {
                this.add(inchiriere);
            } catch (RepositoryException e) {
                System.err.println("Închirierea nu a putut fi adăugată: " + e.getMessage());
            }
        }
    }

}

