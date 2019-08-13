package AAADEVRECORDV3.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author umansilla
 */
public class Usuario {

    private int id;
    private String name;
    private String verbiouser;
    private String username;
    private String password;
    private String fecha;
    private String hora;
    private String phone;
    private String train;
    private String country;
    /////////////////////////////////////////////
    private String cuenta;
    private String saldo;
    private ArrayList<String> historicoList;
    ////////////////////////////////////////////
    public Usuario() {
    }

    public Usuario(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public Usuario(int id, String name, String verbiouser, String username, String fecha, String hora, String phone, String train, String country) {
        this.id = id;
        this.name = name;
        this.verbiouser = verbiouser;
        this.username = username;
        this.fecha = fecha;
        this.hora = hora;
        this.phone = phone;
        this.train = train;
        this.country = country;
    }

    //CREADO EL 10 DE JULIO 2019
    public Usuario(int id, String name, String verbiouser, String username, String fecha, String hora, String phone, String train, String country, String cuenta, ArrayList<String> historicoList, String saldo) {
        this.id = id;
        this.name = name;
        this.verbiouser = verbiouser;
        this.username = username;
        this.fecha = fecha;
        this.hora = hora;
        this.phone = phone;
        this.train = train;
        this.country = country;
        this.cuenta = cuenta;
        this.historicoList = historicoList;
        this.saldo = saldo;
    }
    ///////////////////////////////////////////
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerbiouser() {
        return verbiouser;
    }

    public void setVerbiouser(String verbiouser) {
        this.verbiouser = verbiouser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTrain() {
        return train;
    }

    public void setTrain(String train) {
        this.train = train;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
/////////////////////////////////////////////////
    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public List<String> getHistóricoList() {
        return historicoList;
    }

    public void setHistóricoList(ArrayList<String> históricoList) {
        this.historicoList = históricoList;
    }
    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }
    /////////////////////////////////////////////////
}