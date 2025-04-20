package es.puerto.juego1.controller;

import es.puerto.juego1.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class RecuperarController extends ControladorAbstracto{

    @FXML
    public void initialize() {
        cambiarIdioma();
    }

    @FXML
    private TextField emailTextRecuperar;

    @FXML
    private Button onRecuperarButton;

    @FXML
    private Text textMensaje;

    @FXML
    private Button onRecuperarBackToLoginButton;

    @FXML
    private void onRecuperarClick() {
        if (emailTextRecuperar.getText() == null || emailTextRecuperar.getText().isEmpty()) {
            textMensaje.setText("El Correo no puede ser vacio o nulo.");
            return;
        }
        Usuario email = getUsuarioServiceModel().obtenerUsuarioPorEmailOrUsuario(emailTextRecuperar.getText());
        if (email == null) {
            textMensaje.setText("¡El Email no esta registrado!");
        }

        textMensaje.setText("Mensaje mandado correctamente.");
    }

    @FXML
    protected void onClickBackToLogin() {
        openPantalla(onRecuperarBackToLoginButton, "login.fxml", "Pantalla de Login");
    }
}