package org.example.mircea_alin_lab4;

import Domain.Inchiriere;
import Domain.Masina;
import Exceptions.RepositoryException;
import Repository.RepoInchirieri;
import Repository.RepoMasini;
import Service.ServiceInchiriere;
import Service.ServiceMasina;
import Settings.Settings;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class HelloApplication extends Application {
    private ServiceMasina serviceMasina;
    private ServiceInchiriere serviceInchiriere;

    @Override
    public void start(Stage stage) throws RepositoryException {
        Settings settings = Settings.getInstance();
        String masinaDbUrl = settings.getMasinaDbUrl();
        String inchiriereDbUrl = settings.getInchiriereDbUrl();
        String repoType = settings.getRepoType();

        RepoMasini masinaRepo = new RepoMasini(masinaDbUrl);
        RepoInchirieri inchiriereRepo = new RepoInchirieri(inchiriereDbUrl);

        serviceMasina = new ServiceMasina(masinaRepo);
        serviceInchiriere = new ServiceInchiriere(inchiriereRepo);

        ListView<Masina> masinaListView = new ListView<>();
        ObservableList<Masina> masini = FXCollections.observableArrayList(serviceMasina.getAllMasini());
        masinaListView.setItems(masini);

        ListView<Inchiriere> inchiriereListView = new ListView<>();
        ObservableList<Inchiriere> inchirieri = FXCollections.observableArrayList(serviceInchiriere.getAllInchirieri());
        inchiriereListView.setItems(inchirieri);

        Button adaugaMasinaButton = new Button("Adauga o Masina ");
        Button stergeMasinaButton = new Button("Șterge o Mașină");
        Button actualizeazaMasinaButton = new Button("Modifica o Mașină");

        HBox masinaButtonBox = new HBox(10, adaugaMasinaButton, stergeMasinaButton, actualizeazaMasinaButton);
        masinaButtonBox.setAlignment(Pos.TOP_LEFT);

        Label modelLabel = new Label("Introdu Modelul");
        Label marcaLabel = new Label("Introdu Marca");
        TextField modelTextField = new TextField();
        TextField marcaTextField = new TextField();

        modelTextField.setPrefWidth(300);
        marcaTextField.setPrefWidth(300);

        GridPane masinaInputPane = new GridPane();
        masinaInputPane.add(marcaLabel, 0, 0);
        masinaInputPane.add(marcaTextField, 1, 0);
        masinaInputPane.add(modelLabel, 0, 1);
        masinaInputPane.add(modelTextField, 1, 1);
        masinaInputPane.setHgap(10);
        masinaInputPane.setVgap(10);

        Button adaugaInchiriereButton = new Button("Adauga o Inchiriere");
        Button stergeInchiriereButton = new Button("Sterge o Inchiriere");
        Button actualizeazaInchiriereButton = new Button("Modifica o Inchiriere");

        HBox inchiriereButtonBox = new HBox(10, adaugaInchiriereButton, stergeInchiriereButton, actualizeazaInchiriereButton);
        inchiriereButtonBox.setAlignment(Pos.TOP_LEFT);

        Label dataInceputLabel = new Label("Intrudu data initiala (yyyy-MM-dd)");
        Label dataSfarsitLabel = new Label("Intrudu data finala (yyyy-MM-dd)");
        TextField dataInceputTextField = new TextField();
        TextField dataSfarsitTextField = new TextField();

        dataInceputTextField.setPrefWidth(300);
        dataSfarsitTextField.setPrefWidth(300);

        GridPane inchiriereInputPane = new GridPane();
        inchiriereInputPane.add(dataInceputLabel, 0, 0);
        inchiriereInputPane.add(dataInceputTextField, 1, 0);
        inchiriereInputPane.add(dataSfarsitLabel, 0, 1);
        inchiriereInputPane.add(dataSfarsitTextField, 1, 1);
        inchiriereInputPane.setHgap(10);
        inchiriereInputPane.setVgap(10);

        Button raport1 = new Button("Masinile cele mai des inchiriate");
        Button raport2 = new Button("Inchirieri efectuate pe luna");
        Button raport3 = new Button("Mașinile închiriate cea mai lunga bucata de timp");

        String buttonStyle = "-fx-background-color: #6C6C6C; -fx-text-fill: white; -fx-font-family: Arial; -fx-font-size: 14px; -fx-pref-height: 100px;";
        adaugaMasinaButton.setStyle(buttonStyle);
        stergeMasinaButton.setStyle(buttonStyle);
        actualizeazaMasinaButton.setStyle(buttonStyle);
        adaugaInchiriereButton.setStyle(buttonStyle);
        stergeInchiriereButton.setStyle(buttonStyle);
        actualizeazaInchiriereButton.setStyle(buttonStyle);
        raport1.setStyle(buttonStyle);
        raport2.setStyle(buttonStyle);
        raport3.setStyle(buttonStyle);

        HBox raportButtonBox = new HBox(10, raport1, raport2, raport3);
        raportButtonBox.setAlignment(Pos.TOP_LEFT);

        TableView<Object> raportTableView = new TableView<>();
        raportTableView.setPrefHeight(300);

        raport1.setOnAction(event -> {
            try {
                List<Map.Entry<Masina, Long>> raport = serviceInchiriere.getMasiniDesInchiriate();
                raportTableView.getColumns().clear();
                TableColumn<Object, String> masinaColumn = new TableColumn<>("Masina");
                TableColumn<Object, String> countColumn = new TableColumn<>("Număr Închirieri");

                masinaColumn.setCellValueFactory(data -> {
                    Map.Entry<Masina, Long> entry = (Map.Entry<Masina, Long>) data.getValue();
                    return new SimpleStringProperty(entry.getKey().toString());
                });

                countColumn.setCellValueFactory(data -> {
                    Map.Entry<Masina, Long> entry = (Map.Entry<Masina, Long>) data.getValue();
                    return new SimpleStringProperty(entry.getValue().toString());
                });

                raportTableView.getColumns().addAll(masinaColumn, countColumn);

                ObservableList<Object> raportData = FXCollections.observableArrayList(raport);
                raportTableView.setItems(raportData);
            } catch (Exception e) {
                showErrorAlert("Eroare la generarea raportului: " + e.getMessage());
            }
        });

        raport2.setOnAction(event -> {
            try {
                List<Map.Entry<String, Long>> raport = serviceInchiriere.getInchirieriPeLuna();
                raportTableView.getColumns().clear();  // Ștergerea coloanelor anterioare
                TableColumn<Object, String> lunaColumn = new TableColumn<>("Luna");
                TableColumn<Object, String> countColumn = new TableColumn<>("Număr Închirieri");

                lunaColumn.setCellValueFactory(data -> {
                    Map.Entry<String, Long> entry = (Map.Entry<String, Long>) data.getValue();
                    return new SimpleStringProperty(entry.getKey());
                });

                countColumn.setCellValueFactory(data -> {
                    Map.Entry<String, Long> entry = (Map.Entry<String, Long>) data.getValue();
                    return new SimpleStringProperty(entry.getValue().toString());
                });

                raportTableView.getColumns().addAll(lunaColumn, countColumn);

                ObservableList<Object> raportData = FXCollections.observableArrayList(raport);
                raportTableView.setItems(raportData);
            } catch (Exception e) {
                showErrorAlert("Eroare la generarea raportului: " + e.getMessage());
            }
        });

        raport3.setOnAction(event -> {
            try {
                List<Map.Entry<Masina, Long>> raport = serviceInchiriere.getMasiniZileInchiriere();
                raportTableView.getColumns().clear();

                TableColumn<Object, String> masinaColumn = new TableColumn<>("Mașină");
                masinaColumn.setCellValueFactory(data -> {
                    Map.Entry<Masina, Long> entry = (Map.Entry<Masina, Long>) data.getValue();
                    return new SimpleStringProperty(entry.getKey().toString());  // Afișează mașina
                });

                TableColumn<Object, String> zileColumn = new TableColumn<>("Total Zile Închiriere");
                zileColumn.setCellValueFactory(data -> {
                    Map.Entry<Masina, Long> entry = (Map.Entry<Masina, Long>) data.getValue();
                    return new SimpleStringProperty(entry.getValue().toString());  // Afișează numărul de zile
                });

                raportTableView.getColumns().addAll(masinaColumn, zileColumn);

                ObservableList<Object> raportData = FXCollections.observableArrayList(raport);
                raportTableView.setItems(raportData);
            } catch (Exception e) {
                showErrorAlert("Eroare la generarea raportului: " + e.getMessage());
                e.printStackTrace();
            }
        });

        adaugaMasinaButton.setOnAction(event -> {
            try {
                String marca = marcaTextField.getText().trim();
                String model = modelTextField.getText().trim();
                if (marca.isEmpty() || model.isEmpty()) {
                    throw new Exception("Marca și modelul trebuie completate!");
                }
                Masina masina = new Masina(0, marca, model);
                serviceMasina.add(masina);
                masini.setAll(serviceMasina.getAllMasini());
            } catch (Exception e) {
                showErrorAlert("Eroare la adăugare: " + e.getMessage());
            }
        });

        stergeMasinaButton.setOnAction(event -> {
            try {
                Masina masina = masinaListView.getSelectionModel().getSelectedItem();
                if (masina == null) throw new Exception("Selectați o mașină!");
                serviceMasina.remove(masina.getId());
                masini.setAll(serviceMasina.getAllMasini());
            } catch (Exception e) {
                showErrorAlert("Eroare la ștergere: " + e.getMessage());
            }
        });

        actualizeazaMasinaButton.setOnAction(event -> {
            try {
                Masina masina = masinaListView.getSelectionModel().getSelectedItem();
                if (masina == null) throw new Exception("Selectați o mașină!");
                masina.setMarca(marcaTextField.getText());
                masina.setModel(modelTextField.getText());
                serviceMasina.update(masina);
                masini.setAll(serviceMasina.getAllMasini());
            } catch (Exception e) {
                showErrorAlert("Eroare la actualizare: " + e.getMessage());
            }
        });

        adaugaInchiriereButton.setOnAction(event -> {
            try {
                Masina masina = masinaListView.getSelectionModel().getSelectedItem();
                if (masina == null) throw new Exception("Selectați o mașină pentru închiriere!");
                String datainceput = dataInceputTextField.getText().trim();
                String datasfarsit = dataSfarsitTextField.getText().trim();
                if (datainceput.isEmpty() || datasfarsit.isEmpty()) {
                    throw new Exception("Datele de început și sfârșit trebuie completate!");
                }
                Inchiriere inchiriere = new Inchiriere(
                        0,
                        masina,
                        Date.valueOf(datainceput),
                        Date.valueOf(datasfarsit)
                );
                serviceInchiriere.add(inchiriere);
                inchirieri.setAll(serviceInchiriere.getAllInchirieri());
            } catch (IllegalArgumentException e) {
                showErrorAlert("Format invalid pentru date! Folosiți formatul yyyy-MM-dd.");
            } catch (Exception e) {
                showErrorAlert("Eroare la adăugare închiriere: " + e.getMessage());
            }
        });


        stergeInchiriereButton.setOnAction(event -> {
            try {
                Inchiriere inchiriere = inchiriereListView.getSelectionModel().getSelectedItem();
                if (inchiriere == null) throw new Exception("Selectați o închiriere!");
                serviceInchiriere.remove(inchiriere.getId());
                inchirieri.setAll(serviceInchiriere.getAllInchirieri());
            } catch (Exception e) {
                showErrorAlert("Eroare la ștergere închiriere: " + e.getMessage());
            }
        });

        actualizeazaInchiriereButton.setOnAction(event -> {
            try {
                Inchiriere inchiriere = inchiriereListView.getSelectionModel().getSelectedItem();
                if (inchiriere == null) {
                    throw new Exception("Selectați o închiriere pentru actualizare!");
                }
                String dataInceputNoua = dataInceputTextField.getText();
                String dataSfarsitNoua = dataSfarsitTextField.getText();
                inchiriere.setDataInceput(Date.valueOf(dataInceputNoua));
                inchiriere.setDataSfarsit(Date.valueOf(dataSfarsitNoua));
                serviceInchiriere.update(inchiriere);
                inchirieri.setAll(serviceInchiriere.getAllInchirieri());
            } catch (Exception e) {
                showErrorAlert("Eroare la actualizarea închirierii: " + e.getMessage());
            }
        });



        VBox masinaManagementBox = new VBox(10, masinaButtonBox, masinaInputPane, inchiriereButtonBox, inchiriereInputPane, raportButtonBox);
        masinaManagementBox.setAlignment(Pos.TOP_LEFT);

        HBox rightColumn = new HBox(10, masinaListView, inchiriereListView, raportTableView);
        rightColumn.setAlignment(Pos.CENTER);

        HBox.setHgrow(masinaListView, Priority.ALWAYS);
        HBox.setHgrow(inchiriereListView, Priority.ALWAYS);
        HBox.setHgrow(raportTableView, Priority.ALWAYS);

        VBox finalLayout = new VBox(20, masinaManagementBox, rightColumn);
        finalLayout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(finalLayout, 1200, 600);
        stage.setTitle("Gestionare Închirieri Mașini");
        stage.setScene(scene);
        stage.show();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setContentText(message);
        alert.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
