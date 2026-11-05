package htl.steyr.tetris.controller;

import htl.steyr.tetris.user.UserData;
import htl.steyr.tetris.user.UserSession;
import htl.steyr.tetris.utility.Dialogs;
import htl.steyr.tetris.utility.ViewSwitcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.nio.file.Files;
import java.nio.file.Paths;

public class StartController {

    public Label methodLabel;

    private boolean isRegister = false;

    @FXML private Button closeButton;
    @FXML private Button okButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void initialize() {
        okButton.disableProperty().bind(
                usernameField.textProperty().isEmpty()
                        .or(passwordField.textProperty().isEmpty())
        );
    }

    public void onClosedButtonClicked(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void onOKButtonClicked(ActionEvent actionEvent) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Username wird als Dateiname verwendet -> nur sichere Zeichen erlauben
        if (!username.matches("[A-Za-z0-9_-]{1,20}")) {
            showAlert(
                    "Ungültiger Benutzername",
                    "Der Benutzername darf 1-20 Zeichen lang sein und nur\n" +
                            "Buchstaben, Zahlen, \"_\" und \"-\" enthalten."
            );
            return;
        }

        boolean userExists = Files.exists(Paths.get("users", username.toLowerCase() + ".txt"));

        if (isRegister) {
            if (userExists) {
                showAlert(
                        "Benutzer existiert bereits",
                        "Ein Konto mit diesem Benutzernamen existiert bereits.\n" +
                                "Möchtest du dich stattdessen anmelden? Wechsle den Modus auf \"Anmelden\"."
                );
                return;
            }
            new UserData(username.toLowerCase(), password, true);

        } else {
            if (!userExists) {
                showAlert(
                        "Benutzer nicht gefunden",
                        "Es gibt kein Konto mit diesem Benutzernamen.\n" +
                                "Noch kein Konto? Wechsle den Modus auf \"Registrieren\"."
                );
                return;
            }

            UserData userData = new UserData(username.toLowerCase(), password, false);

            if (userData.getPasswordHash() == null) {
                showAlert("Benutzerdaten beschädigt", "Die Benutzerdaten konnten nicht geladen werden.");
                return;
            }

            if (!userData.getPasswordHash().equals(htl.steyr.tetris.utility.Hasher.hashText(password))) {
                showAlert("Falsches Passwort", "Das eingegebene Passwort ist leider falsch.");
                return;
            }
        }

        UserData ud = UserSession.getUserData();
        if (ud.isDarkMode()) {
            ViewSwitcher.setDarkMode();
        } else {
            ViewSwitcher.setLightMode();
        }
        ViewSwitcher.switchTo("menu.fxml");
    }

    public void onMethodButtonClicked(MouseEvent mouseEvent) {
        if (isRegister) {
            methodLabel.setText("Anmelden");
            isRegister = false;
        } else {
            methodLabel.setText("Registrieren");
            isRegister = true;
        }
    }

    private void showAlert(String title, String message) {
        Dialogs.warning(title, message);
    }

    public void onUserFieldAction(ActionEvent actionEvent) {
        passwordField.requestFocus();
    }

    public void onTextFieldAction(ActionEvent actionEvent) {
        onOKButtonClicked(new ActionEvent());
    }
}