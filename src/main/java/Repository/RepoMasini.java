package Repository;

import Domain.Masina;
import Exceptions.DuplicateEntityException;
import Exceptions.ItemNotFoundException;
import Exceptions.RepositoryException;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class RepoMasini extends Repository<Masina> {
    private Connection connection;

    public RepoMasini(String Db_Url) throws RepositoryException {
        ConnectionOpening(Db_Url);
        CreateTable();
    }

    private void CreateTable() throws RepositoryException {
        String create_table = """
        CREATE TABLE IF NOT EXISTS masini (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            marca TEXT NOT NULL,
            model TEXT NOT NULL
        );
        """;
        try (Statement createStatement = connection.createStatement()) {
            createStatement.execute(create_table);
        } catch (SQLException e) {
            throw new RepositoryException("A aparut o eroare la crearea tabelei 'masini': " + e.getMessage());
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
            throw new RepositoryException("A aparut o eroare la deschiderea conexiunii la baza de date");
        }
    }

    public void add(Masina masina) throws RepositoryException {
        String insert_masina = "INSERT INTO masini (marca, model) VALUES (?, ?);";
        try (PreparedStatement insertStatement = connection.prepareStatement(insert_masina)) {
            insertStatement.setString(2, masina.getMarca());
            insertStatement.setString(1, masina.getModel());
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("A aparut o eroare la adăugarea mașinii: " + e.getMessage());
        }
    }

    public void remove(int id) throws ItemNotFoundException {
        Masina toDelete = find(id);
        if (toDelete == null) {
            throw new ItemNotFoundException("Mașină nu există!");
        }
        String delete_masina = "DELETE FROM masini WHERE id = ?;";

        try (PreparedStatement deleteStatement = connection.prepareStatement(delete_masina)) {
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            try {
                throw new RepositoryException("A aparut o eroare la ștergerea mașinii cu id-ul " + id);
            } catch (RepositoryException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    public Masina find(int id) {
        String find_masina = "SELECT * FROM masini WHERE id = ?;";
        try (PreparedStatement findStatement = connection.prepareStatement(find_masina)) {
            findStatement.setInt(1, id);
            ResultSet result = findStatement.executeQuery();
            if (result.next()) {
                String marca = result.getString("marca");
                String model = result.getString("model");
                return new Masina(id, marca, model);
            }
        } catch (SQLException e) {
            try {
                throw new RepositoryException("A aparut o eroare la găsirea mașinii cu id-ul " + id);
            } catch (RepositoryException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }

    public void update(Masina masina) throws RepositoryException {
        if (masina == null) {
            throw new IllegalArgumentException("Mașina nu poate fi null!");
        }
        String update_masina = "UPDATE masini SET marca = ?, model = ? WHERE id = ?;";
        try (PreparedStatement updateStatement = connection.prepareStatement(update_masina)) {
            updateStatement.setString(2, masina.getMarca());
            updateStatement.setString(1, masina.getModel());
            updateStatement.setInt(3, masina.getId());
            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RepositoryException("Mașina nu există!");
            }
        } catch (SQLException e) {
            throw new RepositoryException("A aparut o eroare la actualizarea mașinii cu ID-ul " + masina.getId());
        }
    }


    @Override
    public List<Masina> getAll() {
        List<Masina> masini = new ArrayList<>();
        String select_masini = "SELECT * FROM masini";
        try (PreparedStatement selectStatement = connection.prepareStatement(select_masini);
             ResultSet result = selectStatement.executeQuery()) {
            while (result.next()) {
                int id = result.getInt("id");
                String marca = result.getString("marca");
                String model = result.getString("model");
                masini.add(new Masina(id, marca, model));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("A aparut o eroare la preluarea mașinilor: " + e.getMessage());
        }
        return masini;
    }

    public void populateMasini() throws RepositoryException, DuplicateEntityException {
        String[] brands = {"Toyota", "Mazda", "BMW", "Tesla", "Renault", "Lexus", "Hyundai", "Honda", "Audi", "Porsche",
                "Volkswagen", "Chevrolet", "Ford", "Nissan", "Mercedes", "Peugeot", "Fiat", "Land Rover", "Subaru", "Jaguar",
                "Volvo", "Mitsubishi", "Suzuki", "Kia", "Jeep", "Dodge", "Chrysler", "Mini", "Infiniti", "Acura",
                "Cadillac", "Buick", "GMC", "Lincoln", "Citroen", "Skoda", "Seat", "Opel", "Saab", "Ferrari",
                "Lamborghini", "Bentley", "Rolls Royce", "Bugatti", "McLaren", "Pagani", "Lotus", "Maserati", "Hummer",
                "Pontiac", "Oldsmobile", "Daihatsu", "Isuzu", "Ram", "Smart", "Genesis", "Rivian", "Lucid Motors",
                "Polestar", "Chery", "Geely", "BYD", "Great Wall", "Mahindra", "Tata", "Scania", "MAN", "Iveco",
                "Freightliner", "Kenworth", "Peterbilt", "Datsun", "MG", "Roewe", "Baojun", "Daewoo", "Proton", "Perodua",
                "Maruti Suzuki", "Lada", "UAZ", "GAZ", "ZAZ", "FAW", "Dongfeng", "Wuling", "Changan", "Haval",
                "Zotye", "Haima", "Foton", "JAC", "King Long", "Yutong", "Zhongtong", "Maxus", "Aston Martin", "Alfa Romeo"
        };
        String[] models = {"Phantom", "Armada", "G70", "Civic", "Tucson", "Model 3", "Navara", "Rogue", "R1T",
                "Outback", "XC90", "TLX", "Defender", "C-Class", "Kona", "Pathfinder", "Corsa", "Leaf",
                "Lancer", "Cooper", "Altima", "RX 350", "GT-R", "Maxima", "Chiron", "Golf", "RAV4",
                "Clio", "Focus", "Land Cruiser", "Continental", "911", "A4", "Hilux", "Quattroporte",
                "Evora", "F-Type", "GTO", "Venue", "Giulia", "Sorento", "300", "Charger", "Navigator",
                "Superb", "Murano", "C3", "H1", "Rio", "Nano", "Silverado", "Santa Fe", "9-3", "Sonata",
                "Escalade", "Air", "Enclave", "Kicks", "Terios", "Swift", "Lancer", "Altima", "RAV4",
                "Micra", "Elantra", "Focus", "X5", "Murano", "Navara", "Sorento", "Octavia", "Qashqai",
                "Scala", "Fabia", "Seltos", "Coolray", "Armada", "Chiron", "Camry", "Navigator", "Sportage",
                "Outback", "RX 350", "720S", "R1T", "Kona", "Rogue", "Pathfinder", "Santa Fe", "Sonata",
                "Leaf", "Micra", "Versa", "Altima", "Maxima", "Murano", "Evora", "370Z", "Kicks", "Terios"
        };

        for (int i = 0; i < 100; i++) {
            String randomBrand = brands[(int) (Math.random() * brands.length)];
            String randomModel = models[(int) (Math.random() * models.length)];
            Masina masina = new Masina(0, randomBrand, randomModel);
            this.add(masina);
        }
    }
}
