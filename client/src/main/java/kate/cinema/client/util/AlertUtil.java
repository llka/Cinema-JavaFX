package kate.cinema.client.util;

import javafx.scene.control.Alert;

public class AlertUtil {
    public static void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void alert(Alert.AlertType alertType, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void alertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ups!");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
