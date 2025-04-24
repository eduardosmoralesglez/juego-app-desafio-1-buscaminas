package es.puerto.juego1.controller;

import java.net.URL;
import java.util.ResourceBundle;
import es.puerto.juego1.model.BuscaminasModelo;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

public class JuegoController extends ControladorAbstracto {

    @FXML
    private GridPane tablero;

    @FXML
    private Button buttonVolver;

    @FXML
    private TextField textFieldPuntos;

    private BuscaminasModelo modelo;
    private boolean primerClick = true;

    public void initialize(URL url, ResourceBundle rb) {
        nuevoJuego();
        textFieldPuntos.setText(getUsuarioActivo().getPuntos().toString());
    }

    @FXML
    public void nuevoJuego() {
        modelo = new BuscaminasModelo(8, 8, 10);
        primerClick = true;
        crearTablero();
    }

    private void crearTablero() {
        /**
         * tablero.getChildren().clear();
         * tablero.getRowConstraints().clear();
         * tablero.getColumnConstraints().clear();
         * for (int i = 0; i < modelo.getFilas(); i++) {
         * for (int j = 0; j < modelo.getColumnas(); j++) {
         * int fila = i;
         * int columna = j;
         * Pane celda = new StackPane();
         * celda.getStyleClass().add("celda");
         * celda.setPrefSize(30, 30);
         * celda.setOnMouseClicked(e -> manejarClick(e.getButton(), fila, columna));
         * celda.getStyleClass().add("game-tablero-cell");
         * if (i == 0) {
         * celda.getStyleClass().add("first-column");
         * }
         * if (j == 0) {
         * celda.getStyleClass().add("first-row");
         * }
         * tablero.add(celda, j, i);
         * }
         * }
         */

        for (int i = 0; i < modelo.getColumnas(); i++) {
            ColumnConstraints column = new ColumnConstraints(40);
            tablero.getColumnConstraints().add(column);
        }
        for (int i = 0; i < modelo.getFilas(); i++) {
            RowConstraints row = new RowConstraints(40);
            tablero.getRowConstraints().add(row);
        }
        for (int i = 0; i < modelo.getColumnas(); i++) {
            for (int j = 0; j < modelo.getFilas(); j++) {
                int fila = i;
                int columna = j;
                Pane pane = new Pane();
                pane.setOnMouseReleased(e -> manejarClick(e.getButton(), fila, columna));
                pane.getStyleClass().add("game-tablero-cell");
                if (i == 0) {
                    pane.getStyleClass().add("first-column");
                }
                if (j == 0) {
                    pane.getStyleClass().add("first-row");
                }
                tablero.add(pane, i, j);
            }
        }
    }

    private void manejarClick(MouseButton boton, int fila, int columna) {
        if (modelo.isJuegoPerdido() || modelo.comprobarVictoria())
            return;

        if (boton == MouseButton.PRIMARY) {
            if (primerClick) {
                modelo.colocarMinas(fila, columna);
                primerClick = false;
            }
            descubrirCelda(fila, columna);
        } else if (boton == MouseButton.SECONDARY) {
            alternarBandera(fila, columna);
        }
        actualizarVista();
        comprobarEstadoJuego();
    }

    private void descubrirCelda(int fila, int columna) {
        if (modelo.tieneBandera(fila, columna) || modelo.estaDescubierta(fila, columna))
            return;
        modelo.setDescubierta(fila, columna, true);

        if (modelo.esMina(fila, columna)) {
            modelo.setJuegoPerdido(true);
        } else if (modelo.getAdyacentes(fila, columna) == 0) {
            descubrirCeldasVacias(fila, columna);
        }
    }

    private void descubrirCeldasVacias(int fila, int columna) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nuevaFila = fila + i;
                int nuevaColumna = columna + j;
                if (modelo.esPosicionValida(nuevaFila, nuevaColumna)
                        && !modelo.estaDescubierta(nuevaFila, nuevaColumna)) {
                    descubrirCelda(nuevaFila, nuevaColumna);
                }
            }
        }
    }

    private void alternarBandera(int fila, int columna) {
        if (!modelo.estaDescubierta(fila, columna)) {
            modelo.setBandera(fila, columna, !modelo.tieneBandera(fila, columna));
        }
    }

    private void actualizarVista() {
        for (int i = 0; i < modelo.getFilas(); i++) {
            for (int j = 0; j < modelo.getColumnas(); j++) {
                Pane celda = (Pane) tablero.getChildren().get(i * modelo.getColumnas() + j);
                celda.getStyleClass().removeAll("descubierta", "mina", "bandera");
                celda.getChildren().clear();

                if (modelo.estaDescubierta(i, j)) {
                    celda.getStyleClass().add("descubierta");
                    if (modelo.getAdyacentes(i, j) > 0) {
                        Text texto = new Text(String.valueOf(modelo.getAdyacentes(i, j)));
                        texto.getStyleClass().add("numero");
                        texto.getStyleClass().add("numero-" + modelo.getAdyacentes(i, j));
                        celda.getChildren().add(texto);
                    }
                } else if (modelo.tieneBandera(i, j)) {
                    celda.getStyleClass().add("bandera");
                    celda.getChildren().add(new Text("🚩"));
                }

                if (modelo.isJuegoPerdido() && modelo.esMina(i, j)) {
                    celda.getStyleClass().add("mina");
                    celda.getChildren().add(new Text("💣"));
                }
            }
        }
    }

    private void comprobarEstadoJuego() {
        if (modelo.isJuegoPerdido()) {
            getUsuarioActivo().setPuntos(getUsuarioActivo().getPuntos() - 500);
            textFieldPuntos.setText(getUsuarioActivo().getPuntos().toString());
            mostrarAlerta("¡Has perdido!", "Has pisado una mina", Alert.AlertType.ERROR);
        } else if (modelo.comprobarVictoria()) {
            getUsuarioActivo().setPuntos(getUsuarioActivo().getPuntos() + 500);
            textFieldPuntos.setText(getUsuarioActivo().getPuntos().toString());
            mostrarAlerta("¡Victoria!", "¡Has ganado el juego!", Alert.AlertType.INFORMATION);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(mensaje);
        alerta.getButtonTypes().setAll(ButtonType.OK);
        alerta.showAndWait();
    }

    @FXML
    public void salir() {
        System.exit(0);
    }

    @FXML
    public void buttonVolver() {
        openPantalla(buttonVolver, "perfil.fxml", "Perfil", getUsuarioActivo());
    }

}