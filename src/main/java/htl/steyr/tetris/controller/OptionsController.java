package htl.steyr.tetris.controller;

import htl.steyr.tetris.user.UserData;
import htl.steyr.tetris.user.UserSession;
import htl.steyr.tetris.utility.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

// Controller für den Options-Screen. Verwaltet Lautstärke, Tastenbelegung,
// Dark/Light Mode und das Zurücksetzen von Daten/Highscore.
public class OptionsController {

    public Button white_darkmode_button;

    @FXML
    private Slider musicSlider;
    @FXML
    private Slider soundSlider;

    @FXML
    private Button saveButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button clearDataButton;
    @FXML
    private CheckBox showNextPieceCheckBox;

    // Buttons für die Tastenbelegung der einzelnen Spielaktionen
    @FXML
    private Button leftKeyButton;
    @FXML
    private Button rightKeyButton;

    @FXML
    private Button rotateLeftButton;
    @FXML
    private Button rotateRightButton;

    @FXML
    private Button holdButton;
    @FXML
    private Button softdropButton;
    @FXML
    private Button harddropButton;

    private Button currentKeyButton; // merkt sich, welcher Button gerade auf Tasteneingabe wartet

    private UserData ud;

    @FXML
    public void initialize() {
        ud = UserSession.getUserData();

        // Slider mit den gespeicherten Werten initialisieren
        musicSlider.setValue(ud.getVolumeMusic());
        // Lautstärke live anpassen, während der Slider bewegt wird (noch ohne zu speichern)
        musicSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                htl.steyr.tetris.Music.getInstance().setVolume(newVal.doubleValue())
        );
        soundSlider.setValue(ud.getVolumeSfx());

        showNextPieceCheckBox.setSelected(ud.isShowNextPiece());

        // Für jede Spielaktion den passenden Button mit der aktuellen Taste beschriften
        // und Klick-Listener zum Neubelegen einrichten

        setupKeyButton(leftKeyButton, "left", ud.getSetting("left"));
        setupKeyButton(rightKeyButton, "right", ud.getSetting("right"));
        setupKeyButton(rotateLeftButton, "rotate_left", ud.getSetting("rotate_left"));
        setupKeyButton(rotateRightButton, "rotate_right", ud.getSetting("rotate_right"));
        setupKeyButton(holdButton, "hold", ud.getSetting("hold"));
        setupKeyButton(softdropButton, "softdrop", ud.getSetting("softdrop"));
        setupKeyButton(harddropButton, "harddrop", ud.getSetting("harddrop"));

        white_darkmode_button.setText(ViewSwitcher.isDarkMode() ? "Dark Mode" : "Light Mode");
        white_darkmode_button.setOnAction(e -> toggleTheme());
        saveButton.setOnAction(e -> saveSettings());
        closeButton.setOnAction(e -> returnToMenu());
        clearDataButton.setOnAction(e -> clearAllData());
        // Speichern erst erlauben, wenn wirklich jeder Taste eine Belegung zugewiesen ist
        saveButton.setDisable(!allKeysAssigned());

    }

    private void returnToMenu() {
        ud.save();
        ViewSwitcher.switchTo("menu.fxml");
    }

    // Musik-Steuerung: zum vorherigen/nächsten Song wechseln
    public void prevSongButtonClicked(ActionEvent actionEvent) {
        htl.steyr.tetris.Music.getInstance().previous();
    }

    public void nextSongButtonClicked(ActionEvent actionEvent) {
        htl.steyr.tetris.Music.getInstance().next();
    }

    // Setzt den persönlichen Highscore in UserData zurück (nicht die globale Liste!)
    public void resetHighscoreClicked(ActionEvent actionEvent) {
        ud.setHighscore(0);
        ud.save();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Highscore zurückgesetzt");
        alert.setHeaderText(null);
        alert.setContentText("Dein Highscore wurde zurückgesetzt!");
        alert.showAndWait();
    }


    // Übernimmt die aktuellen Sliderwerte dauerhaft in UserData und speichert sie ab
    private void saveSettings() {
        UserData ud = UserSession.getUserData();
        ud.setVolumeMusic((int) musicSlider.getValue());
        ud.setVolumeSfx((int) soundSlider.getValue());
        ud.setShowNextPiece(showNextPieceCheckBox.isSelected());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Gespeichert");
        alert.setHeaderText(null);
        alert.setContentText("Einstellungen wurden gespeichert!");
        alert.showAndWait();
        ud.save();
    }

    private void loadTheme() {
        applyTheme();
    }

    // Wechselt zwischen Dark- und Lightmode hin und her
    private void toggleTheme() {
        if (ViewSwitcher.isDarkMode()) {
            ViewSwitcher.setLightMode();
        } else {
            ViewSwitcher.setDarkMode();
        }
        applyTheme();
    }

    // Wendet das passende Stylesheet auf die aktuelle Scene an und passt den Button-Text an
    private void applyTheme() {
        Scene scene = white_darkmode_button.getScene();
        if (scene == null) return;

        scene.getStylesheets().clear();

        if (ViewSwitcher.isDarkMode()) {
            scene.getStylesheets().add(
                    getClass().getResource("/htl/steyr/tetris/stylesheets/darkmode.css").toExternalForm()
            );
            white_darkmode_button.setText("Dark Mode");
        } else {
            scene.getStylesheets().add(
                    getClass().getResource("/htl/steyr/tetris/stylesheets/whitemode.css").toExternalForm()
            );
            white_darkmode_button.setText("Light Mode");
        }
    }

    private void closeWindow() {
        ud.save();
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    // Setzt ALLE Einstellungen (Lautstärke + Tastenbelegung) auf die Standardwerte zurück,
    // nach vorheriger Bestätigung durch den User
    private void clearAllData() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Warnung");
        confirm.setHeaderText("Alles Änderungen zurücksetzen?");
        confirm.setContentText("Diese Änderung kann nicht mehr rückgängig gemacht werden!");

        confirm.showAndWait().ifPresent(response -> {
            if (response != ButtonType.OK) return; // Abbrechen -> nichts tun

            UserData ud = UserSession.getUserData();
            ud.setVolumeMusic(50);
            ud.setVolumeSfx(50);
            ud.setShowNextPiece(true);
            musicSlider.setValue(50);
            soundSlider.setValue(50);
            showNextPieceCheckBox.setSelected(true);

            // Standard-Tastenbelegung wiederherstellen
            ud.setSetting("left", KeyCode.S);
            ud.setSetting("right", KeyCode.D);
            ud.setSetting("rotate_left", KeyCode.Z);
            ud.setSetting("rotate_right", KeyCode.X);
            ud.setSetting("hold", KeyCode.C);
            ud.setSetting("softdrop", KeyCode.S);
            ud.setSetting("harddrop", KeyCode.SPACE);
            ud.save();

            // Buttons in der UI ebenfalls aktualisieren
            leftKeyButton.setText(KeyCode.S.getName());
            rightKeyButton.setText(KeyCode.D.getName());
            rotateLeftButton.setText(KeyCode.Z.getName());
            rotateRightButton.setText(KeyCode.X.getName());
            holdButton.setText(KeyCode.C.getName());
            softdropButton.setText(KeyCode.S.getName());
            harddropButton.setText(KeyCode.SPACE.getName());
        });
    }

    // Beschriftet einen Tasten-Button mit der aktuellen Belegung und richtet den Klick-Listener
    // ein, der das "Tasten-Neubelegen" startet
    private void setupKeyButton(Button button, String action, KeyCode currentKey) {
        button.setText(currentKey.getName());
        button.setOnAction(e -> {
            currentKeyButton = button;
            button.setText("Press Key..."); // visuelles Feedback, dass jetzt eine Taste erwartet wird
            setAllKeyButtonsDisabled(true, button); // alle anderen Buttons währenddessen sperren
            listenForKey(button, action);
        });
        ud.save();
    }

    // Wartet auf den nächsten Tastendruck und weist ihn der gegebenen Aktion zu,
    // sofern die Taste nicht schon für eine andere Aktion verwendet wird
    private void listenForKey(Button button, String action) {
        Scene scene = button.getScene();
        if (scene == null) return;

        scene.setOnKeyPressed(event -> {
            String newKey = event.getCode().toString();

            if (isKeyAlreadyUsed(action, newKey) || newKey.equals("ESCAPE")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Taste bereits vergeben");
                alert.setHeaderText(null);
                alert.setContentText("Die Taste \"" + newKey + "\" wird bereits verwendet!");
                alert.showAndWait();
                button.setText(UserSession.getUserData().getSetting(action).getName()); // alten Wert wiederherstellen
            } else {
                button.setText(newKey);
                UserSession.getUserData().setSetting(action, event.getCode());
                UserSession.getUserData().save();
            }

            setAllKeyButtonsDisabled(false, null); // Buttons wieder freigeben
            saveButton.setDisable(!allKeysAssigned());
            scene.setOnKeyPressed(null); // Listener wieder entfernen, sonst würde jede Taste danach reagieren
        });
    }

    // Prüft, ob eine bestimmte Taste schon einer anderen Aktion zugewiesen ist
    private boolean isKeyAlreadyUsed(String skipAction, String newKey) {
        String[] actions = { "left", "right", "rotate_left", "rotate_right", "hold", "softdrop", "harddrop"};
        UserData ud = UserSession.getUserData();
        for (String a : actions) {
            if (a.equals(skipAction)) continue; // die eigene Aktion überspringen (man darf die gleiche Taste behalten)
            if (ud.getSetting(a).toString().equals(newKey)) return true;
        }
        return false;
    }

    // Prüft, ob wirklich jede Aktion eine Taste zugewiesen hat (kein Button zeigt noch "Press Key...")
    private boolean allKeysAssigned() {
        Button[] all = { leftKeyButton, rightKeyButton,
                rotateLeftButton, rotateRightButton, holdButton, softdropButton, harddropButton};
        for (Button b : all)
            if (b.getText().equals("Press Key...")) return false;
        return true;
    }

    // Sperrt/entsperrt alle Tasten-Buttons (außer einem optionalen "except"-Button),
    // damit während des Neubelegens nicht mehrere Buttons gleichzeitig aktiv sein können
    private void setAllKeyButtonsDisabled(boolean disabled, Button except) {

        Button[] all = {
                leftKeyButton, rightKeyButton,
                rotateLeftButton, rotateRightButton,
                holdButton, softdropButton, harddropButton, saveButton
        };

        for (Button b : all) {
            if (b != except) b.setDisable(disabled);
        }
    }
}