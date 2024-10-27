module edu.miracosta.cs112.globalcustoms.globalcustoms {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens edu.miracosta.cs112.globalcustoms.globalcustoms to javafx.fxml;
    exports edu.miracosta.cs112.globalcustoms.globalcustoms;
}