package es.puerto.juego1.model;

import java.util.Objects;

public class Usuario {
    private String usuario;
    private String password;
    private String nombre;
    private String email;
    private Integer puntos;
    private String nivel;

    public Usuario() {
    }

    public Usuario(String usuario) {
        this.usuario = usuario;
    }

    public Usuario(String usuario, String password, String nombre, String email) {
        this.usuario = usuario;
        this.password = password;
        this.nombre = nombre;
        this.email = email;
    }

    public Usuario(String usuario, String password, String nombre, String email, Integer puntos, String nivel) {
        this.usuario = usuario;
        this.password = password;
        this.nombre = nombre;
        this.email = email;
        this.puntos = puntos;
        this.nivel = nivel;
    }

    public String getUsuario() {
        return this.usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPuntos() {
        return this.puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
        if (this.puntos < 0) {
            this.puntos = 0;
        }
    }

    public String getNivel() {
        return this.nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Usuario)) {
            return false;
        }
        Usuario usuarioEquals = (Usuario) o;
        return Objects.equals(usuario, usuarioEquals.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario);
    }

    @Override
    public String toString() {
        return getUsuario() + "," +
                getPassword() + "," +
                getNombre() + "," +
                getEmail() + "," +
                getPuntos() + "," +
                getNivel();
    }

}
