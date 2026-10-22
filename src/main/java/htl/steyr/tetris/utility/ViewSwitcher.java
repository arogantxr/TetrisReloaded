package htl.steyr.tetris.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

// Zentrale Klasse zum Wechseln zwischen den verschiedenen FXML-Screens (Start, Menü, Spiel, ...).
// Hält die einzige Stage der App und wendet bei jedem Wechsel das passende Stylesheet an.
public class ViewSwitcher {

    private static Stage stage; // die eine Stage der gesamten Anwendung

    // Startgröße des Fensters und kleinste erlaubte Größe beim Verkleinern
    private static final double DEFAULT_WIDTH = 900;
    private static final double DEFAULT_HEIGHT = 700;
    private static final double MIN_WIDTH = 700;
    private static final double MIN_HEIGHT = 550;

    // Dark/Light Mode speichern
    private static boolean darkMode = true;

    // Wird einmalig beim App-Start aufgerufen (TetrisApplication), um die Stage zu hinterlegen
    public static void setStage(Stage s) {
        stage = s;
    }

    // Darkmode aktivieren
    public static void setDarkMode() {
        darkMode = true;
    }

    // Lightmode aktivieren
    public static void setLightMode() {
        darkMode = false;
    }

    // Abfragen falls nötig
    public static boolean isDarkMode() {
        return darkMode;
    }

    // Lädt ein neues FXML, baut daraus eine neue Scene und zeigt sie auf der bestehenden Stage an.
    // Wird von allen Controllern verwendet, um zwischen Screens zu wechseln (z.B. "menu.fxml").
    public static void switchTo(String fxml) {

        try {
            String fxmlFilePath = "/htl/steyr/tetris/fxml/" + fxml;
            Parent root = FXMLLoader.load(Objects.requireNonNull(ViewSwitcher.class.getResource(fxmlFilePath)));

            // Beim ersten Screen Standardgröße, danach die aktuelle (evtl. vom User geänderte) Größe übernehmen
            Scene oldScene = stage.getScene();
            Scene scene = (oldScene == null)
                    ? new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT)
                    : new Scene(root, oldScene.getWidth(), oldScene.getHeight());

            // Stylesheet setzen (Dark- oder Lightmode), passend zum aktuellen Zustand
            scene.getStylesheets().clear();

            if (darkMode) {
                scene.getStylesheets().add(
                        ViewSwitcher.class.getResource("/htl/steyr/tetris/stylesheets/darkmode.css").toExternalForm()
                );
            } else {
                scene.getStylesheets().add(
                        ViewSwitcher.class.getResource("/htl/steyr/tetris/stylesheets/whitemode.css").toExternalForm()
                );
            }

            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(MIN_WIDTH);
            stage.setMinHeight(MIN_HEIGHT);

            // Nur beim allerersten Anzeigen zentrieren, sonst springt das Fenster bei jedem Wechsel
            if (oldScene == null) {
                stage.show();
                stage.centerOnScreen();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gibt einen FXMLLoader zurück, ohne ihn direkt zu laden (für Sonderfälle, falls man
    // z.B. noch vor dem Laden Zugriff auf den Controller braucht)
    public static FXMLLoader getLoader(String fxml) {
        return new FXMLLoader(ViewSwitcher.class.getResource("/htl/steyr/tetris/fxml/" + fxml));
    }
}