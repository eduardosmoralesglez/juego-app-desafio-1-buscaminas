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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * Clase controlador del juego Buscaminas
 * 
 * @author eduardoSerafin
 * @version 1.0.0 200425
 */
public class JuegoController extends ControladorAbstracto {

    private static final int codigoBandera = 0x1F6A9;
    private static final String emojiBandera = new String(Character.toChars(codigoBandera));
    private static final int codigoBomba = 0x1F4A3;
    private static final String emojiBomba = new String(Character.toChars(codigoBomba));

    @FXML
    private GridPane tablero;

    @FXML
    private StackPane stackPaneContenedor;

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

    /**
     * Funcion para iniciar un nuevo juego
     */
    @FXML
    public void nuevoJuego() {
        primerClick = true;
        stackPaneContenedor.getChildren().removeIf(node -> node instanceof GridPane);
        modelo = new BuscaminasModelo(8, 8, 10);
        tablero = crearTablero();
        stackPaneContenedor.getChildren().add(tablero);
    }

    /**
     * Funcion para crear el tablero de juego
     * 
     * @return GridPane con el tablero
     */
    private GridPane crearTablero() {
        GridPane gridtablero = new GridPane();
        double celtaAncho = stackPaneContenedor.getWidth() / modelo.getColumnas();
        double celdaAltura = stackPaneContenedor.getHeight() / modelo.getFilas();
        double tamanioCelda = Math.min(celtaAncho, celdaAltura);
        for (int i = 0; i < modelo.getColumnas(); i++) {
            for (int j = 0; j < modelo.getFilas(); j++) {
                int fila = i;
                int columna = j;
                Button boton = new Button();
                boton.setPrefSize(tamanioCelda, tamanioCelda);
                boton.getStyleClass().add("celda");
                boton.setOnMouseReleased(e -> manejarClick(e.getButton(), fila, columna));
                gridtablero.add(boton, i, j);
            }
        }
        return gridtablero;
    }

    /**
     * Funcion para manejar los clicks del juego
     * 
     * @param boton   del raton
     * @param fila    del tablero
     * @param columna del tablero
     */
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

    /**
     * Funcion para descubrir las celdas
     * 
     * @param fila    del tablero
     * @param columna del tablero
     */
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

    /**
     * Funcion para descubrir las celdas vacias
     * 
     * @param fila    del tablero
     * @param columna del tablero
     */
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

    /**
     * Funcion para poner o quitar vandera
     * 
     * @param fila    del tablero
     * @param columna del tablero
     */
    private void alternarBandera(int fila, int columna) {
        if (!modelo.estaDescubierta(fila, columna)) {
            modelo.setBandera(fila, columna, !modelo.tieneBandera(fila, columna));
        }
    }

    /**
     * Funcion para actualizar el tablero
     */
    private void actualizarVista() {
        for (int i = 0; i < modelo.getFilas(); i++) {
            for (int j = 0; j < modelo.getColumnas(); j++) {
                Button celda = (Button) tablero.getChildren().get(i * modelo.getColumnas() + j);
                celda.getStyleClass().removeAll("descubierta", "mina", "bandera");
                if (modelo.estaDescubierta(i, j)) {
                    if (modelo.getAdyacentes(i, j) > 0) {
                        String texto = new String(String.valueOf(modelo.getAdyacentes(i, j)));
                        celda.setText(texto);
                    }
                } else if (modelo.tieneBandera(i, j)) {
                    celda.setText(emojiBandera);
                }
                if (modelo.isJuegoPerdido() && modelo.esMina(i, j)) {
                    celda.setText(emojiBomba);
                }
            }
        }
    }

    /**
     * Funcion para comprobar el estado del juego
     */
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

    /**
     * Funcion para mostrar pantallas de notificacion
     * 
     * @param titulo  titulo de la pantalla
     * @param mensaje mensaje de la pantalla
     * @param tipo    de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(mensaje);
        alerta.getButtonTypes().setAll(ButtonType.OK);
        alerta.showAndWait();
    }

    /**
     * Funcion para cerrar la aplicación
     */
    @FXML
    public void salir() {
        System.exit(0);
    }

    /**
     * Funcion para volver al perfil
     */
    @FXML
    public void buttonVolver() {
        openPantalla(buttonVolver, "perfil.fxml", "Perfil", getUsuarioActivo());
    }

}