package htl.steyr.tetris.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

// Zeigt Meldungen und Rückfragen im selben Design wie die restliche App (Dark/Light Mode)
// statt im Standard-Windows-Fenster. Aussehen siehe ".app-dialog" in den Stylesheets.
public class Dialogs {

    public static void info(String title, String message) {
        create(Alert.AlertType.INFORMATION, title, message).showAndWait();
    }

    // Wie info(), aber mit roter Überschrift
    public static void warning(String title, String message) {
        Alert alert = create(Alert.AlertType.WARNING, title, message);
        alert.getDialogPane().getStyleClass().add("warning");
        alert.showAndWait();
    }

    // Gibt true zurück, wenn der User mit OK bestätigt hat
    public static boolean confirm(String title, String message) {
        return create(Alert.AlertType.CONFIRMATION, title, message)
                .showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }

    private static Alert create(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(ViewSwitcher.getStage()); // über dem Hauptfenster zentrieren
        alert.initStyle(StageStyle.TRANSPARENT);  // ohne Windows-Titelleiste, runde Ecken möglich
        alert.setTitle(title);
        alert.setHeaderText(title); // Titelleiste fehlt -> Titel als Überschrift im Dialog
        alert.setContentText(message);
        alert.setGraphic(null);     // kein Standard-Icon

        DialogPane pane = alert.getDialogPane();
        pane.getStylesheets().add(ViewSwitcher.getStylesheet());
        pane.getStyleClass().add("app-dialog");
        pane.getScene().setFill(Color.TRANSPARENT);
        return alert;
    }
}
