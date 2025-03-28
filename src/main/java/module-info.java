module org.example.mircea_alin_lab4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.xerial.sqlitejdbc;

    opens org.example.mircea_alin_lab4 to javafx.fxml;
    exports org.example.mircea_alin_lab4;
    exports Repository;
    opens Repository to javafx.fxml;
}