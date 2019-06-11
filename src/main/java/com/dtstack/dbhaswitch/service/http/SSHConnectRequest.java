package com.dtstack.dbhaswitch.service.http;

public class SSHConnectRequest {
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String pk;

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "SSHConnectRequest [host=" + host + ", port=" + port + ", user=" + user + ", password=" + password
                + ", pk=" + pk + "]";
    }

}
