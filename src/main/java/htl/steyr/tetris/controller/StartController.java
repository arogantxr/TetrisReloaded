package htl.steyr.tetris.controller;

import htl.steyr.tetris.user.UserData;
import htl.steyr.tetris.user.UserSession;
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
            new UserData(username, password, true);

        } else {
            if (!userExists) {
                showAlert(
                        "Benutzer nicht gefunden",
                        "Es gibt kein Konto mit diesem Benutzernamen.\n" +
                                "Noch kein Konto? Wechsle den Modus auf \"Registrieren\"."
                );
                return;
            }

            UserData userData = new UserData(username, password, false);

            if (!userData.getPasswordHash().equals(htl.steyr.tetris.utility.Hasher.hashText(password))) {
                showAlert("Falsches Passwort", "Das eingegebene Passwort ist leider falsch.");
                return;
            }
        }

        ViewSwitcher.switchTo("menu.fxml");
        UserData ud = new UserData(username.toLowerCase(),password, isRegister);
        UserSession.setUserData(ud);
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onUserFieldAction(ActionEvent actionEvent) {
        passwordField.requestFocus();
    }

    public void onTextFieldAction(ActionEvent actionEvent) {
        onOKButtonClicked(new ActionEvent());
    }
}