package by.dero.gvh;

public class Settings {
    private String mode;
    private boolean cristalix;
    private String locale;
    private String serverName;
    private String dataStorageType;
    private String dataMongodbConnection;
    private String dataMongodbDatabase;
    private String playerDataStorageType;
    private String playerDataMongodbConnection;
    private String playerDataMongodbDatabase;
    private String lobbyDataStorageType;
    private String lobbyDataMongodbConnection;
    private String lobbyDataMongodbDatabase;
    private String serverDataStorageType;
    private String serverDataMongodbConnection;
    private String serverDataMongodbDatabase;

    public boolean isCristalix() {
        return cristalix;
    }

    public void setCristalix(boolean cristalix) {
        this.cristalix = cristalix;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getDataStorageType() {
        return dataStorageType;
    }

    public void setDataStorageType(String dataStorageType) {
        this.dataStorageType = dataStorageType;
    }

    public String getDataMongodbConnection() {
        return dataMongodbConnection;
    }

    public void setDataMongodbConnection(String dataMongodbConnection) {
        this.dataMongodbConnection = dataMongodbConnection;
    }

    public String getDataMongodbDatabase() {
        return dataMongodbDatabase;
    }

    public void setDataMongodbDatabase(String dataMongodbDatabase) {
        this.dataMongodbDatabase = dataMongodbDatabase;
    }

    public String getPlayerDataStorageType() {
        return playerDataStorageType;
    }

    public void setPlayerDataStorageType(String playerDataStorageType) {
        this.playerDataStorageType = playerDataStorageType;
    }

    public String getPlayerDataMongodbConnection() {
        return playerDataMongodbConnection;
    }

    public void setPlayerDataMongodbConnection(String playerDataMongodbConnection) {
        this.playerDataMongodbConnection = playerDataMongodbConnection;
    }

    public String getPlayerDataMongodbDatabase() {
        return playerDataMongodbDatabase;
    }

    public void setPlayerDataMongodbDatabase(String playerDataMongodbDatabase) {
        this.playerDataMongodbDatabase = playerDataMongodbDatabase;
    }

    public String getLobbyDataStorageType() {
        return lobbyDataStorageType;
    }

    public void setLobbyDataStorageType(String lobbyDataStorageType) {
        this.lobbyDataStorageType = lobbyDataStorageType;
    }

    public String getLobbyDataMongodbConnection() {
        return lobbyDataMongodbConnection;
    }

    public void setLobbyDataMongodbConnection(String lobbyDataMongodbConnection) {
        this.lobbyDataMongodbConnection = lobbyDataMongodbConnection;
    }

    public String getLobbyDataMongodbDatabase() {
        return lobbyDataMongodbDatabase;
    }

    public void setLobbyDataMongodbDatabase(String lobbyDataMongodbDatabase) {
        this.lobbyDataMongodbDatabase = lobbyDataMongodbDatabase;
    }

    public String getServerDataStorageType() {
        return serverDataStorageType;
    }

    public void setServerDataStorageType(String serverDataStorageType) {
        this.serverDataStorageType = serverDataStorageType;
    }

    public String getServerDataMongodbConnection() {
        return serverDataMongodbConnection;
    }

    public void setServerDataMongodbConnection(String serverDataMongodbConnection) {
        this.serverDataMongodbConnection = serverDataMongodbConnection;
    }

    public String getServerDataMongodbDatabase() {
        return serverDataMongodbDatabase;
    }

    public void setServerDataMongodbDatabase(String serverDataMongodbDatabase) {
        this.serverDataMongodbDatabase = serverDataMongodbDatabase;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
