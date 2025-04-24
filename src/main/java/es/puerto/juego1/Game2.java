package es.puerto.juego1;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import es.puerto.juego1.model.BuscaminasModelo;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
// https://stackoverflow.com/questions/32892646/adding-borders-to-gridpane-javafx

public class Game2 extends Application {

    @FXML
    private GridPane tablero;

    @FXML
    private Button buttonVolver;

    @FXML
    private TextField textFieldPuntos;

    private BuscaminasModelo modelo;
    private boolean primerClick = true;

        private void crearTablero() {
        tablero.getChildren().clear();
        tablero.getRowConstraints().clear();
        tablero.getColumnConstraints().clear();
        for (int i = 0; i < modelo.getFilas(); i++) {
            for (int j = 0; j < modelo.getColumnas(); j++) {
                StackPane celda = new StackPane();
                celda.getStyleClass().add("celda");
                celda.setPrefSize(30, 30);
                int fila = i;
                int columna = j;
                celda.setOnMouseClicked(e -> manejarClick(e.getButton(), fila, columna));
                tablero.add(celda, j, i);
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
                StackPane celda = (StackPane) tablero.getChildren().get(i * modelo.getColumnas() + j);
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
            mostrarAlerta("¡Has perdido!", "Has pisado una mina", Alert.AlertType.ERROR);
        } else if (modelo.comprobarVictoria()) {
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


    @Override
    public void start(final Stage stage) throws Exception {
        int rows = 8;
        int columns = 8;

        stage.setTitle("Enjoy your game");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("game-grid");

        for (int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints(40);
            grid.getColumnConstraints().add(column);
        }

        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints(40);
            grid.getRowConstraints().add(row);
        }

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                Pane pane = new Pane();
                pane.setOnMouseReleased(e -> {
                    pane.getChildren().add(Anims.getAtoms(1));
                });
                pane.getStyleClass().add("game-grid-cell");
                if (i == 0) {
                    pane.getStyleClass().add("first-column");
                }
                if (j == 0) {
                    pane.getStyleClass().add("first-row");
                }
                grid.add(pane, i, j);
            }

        }
        for (int i = 0; i < columns; i++) {
            
            byte[] emojiByteCode = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x81};
            String emoji = new String(emojiByteCode, StandardCharsets.UTF_8);
            Pane pane = new Pane(new Text(emoji));
            grid.add(pane, 5, i);
            //celda.getChildren().add(new Text("🚩"));
            
        }

        Scene scene = new Scene(grid, (columns * 40) + 100, (rows * 40) + 100, Color.WHITE);
        scene.getStylesheets().add("game.css");
        stage.setScene(scene);
        stage.show();
    }

    public static class Anims {

        public static Node getAtoms(final int number) {
            Circle circle = new Circle(20, 20f, 7);
            circle.setFill(Color.RED);
            Group group = new Group();
            group.getChildren().add(circle);
            // SubScene scene = new SubScene(group, 40, 40);
            // scene.setFill(Color.TRANSPARENT);
            return group;
        }
    }

    public static void main(final String[] arguments) {
        Application.launch(arguments);
    }
}