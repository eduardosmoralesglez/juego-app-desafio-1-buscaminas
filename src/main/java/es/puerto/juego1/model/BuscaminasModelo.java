package es.puerto.juego1.model;

import java.util.Random;

public class BuscaminasModelo {

    private boolean[][] minas;
    private int[][] adyacentes;
    private boolean[][] descubiertas;
    private boolean[][] banderas;
    private int filas, columnas, totalMinas;
    private boolean juegoPerdido;

    public BuscaminasModelo(int filas, int columnas, int minas) {
        this.filas = filas;
        this.columnas = columnas;
        this.totalMinas = minas;
        inicializarTablero();
    }

    private void inicializarTablero() {
        minas = new boolean[filas][columnas];
        adyacentes = new int[filas][columnas];
        descubiertas = new boolean[filas][columnas];
        banderas = new boolean[filas][columnas];
        juegoPerdido = false;
    }

    public void colocarMinas(int filaInicial, int columnaInicial) {
        Random rand = new Random();
        int minasColocadas = 0;

        while (minasColocadas < totalMinas) {
            int fila = rand.nextInt(filas);
            int columna = rand.nextInt(columnas);

            if (!(fila == filaInicial && columna == columnaInicial) && !minas[fila][columna]) {
                minas[fila][columna] = true;
                minasColocadas++;
            }
        }
        calcularAdyacentes();
    }

    private void calcularAdyacentes() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (!minas[i][j]) {
                    adyacentes[i][j] = contarMinasAdyacentes(i, j);
                }
            }
        }
    }

    private int contarMinasAdyacentes(int fila, int columna) {
        int contador = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nuevaFila = fila + i;
                int nuevaColumna = columna + j;
                if (esPosicionValida(nuevaFila, nuevaColumna) && minas[nuevaFila][nuevaColumna]) {
                    contador++;
                }
            }
        }
        return contador;
    }

    public boolean esPosicionValida(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }

    public boolean esMina(int fila, int columna) {
        return minas[fila][columna];
    }

    public int getAdyacentes(int fila, int columna) {
        return adyacentes[fila][columna];
    }

    public boolean estaDescubierta(int fila, int columna) {
        return descubiertas[fila][columna];
    }

    public void setDescubierta(int fila, int columna, boolean valor) {
        descubiertas[fila][columna] = valor;
    }

    public boolean tieneBandera(int fila, int columna) {
        return banderas[fila][columna];
    }

    public void setBandera(int fila, int columna, boolean valor) {
        banderas[fila][columna] = valor;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public boolean isJuegoPerdido() {
        return juegoPerdido;
    }

    public void setJuegoPerdido(boolean valor) {
        juegoPerdido = valor;
    }

    public boolean comprobarVictoria() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (!minas[i][j] && !descubiertas[i][j])
                    return false;
            }
        }
        return true;
    }
}
