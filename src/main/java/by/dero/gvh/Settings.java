package by.dero.gvh;

public class Settings {
    private String mode;
    private boolean cristalix;
    private boolean overrideLang;
    private boolean stopAfterGame;
    private String locale;
    private String serverName;
    private String dataStorageType;
    private String dataMongodbConnection;
    private String dataMongodbDatabase;
    private String playerDataMongodbConnection;
    private String playerDataMongodbDatabase;
    private String lobbyDataStorageType;
    private String lobbyDataMongodbConnection;
    private String lobbyDataMongodbDatabase;
    private String serverDataMongodbConnection;
    private String serverDataMongodbDatabase;
    private String reportDataStorageType;
    private String reportDataMongodbConnection;
    private String reportDataMongodbDatabase;
    private String resourcePackUrl;
    private String resourcePackSha1Sum;

    public String getResourcePackUrl() {
        return resourcePackUrl;
    }

    public void setResourcePackUrl(String resourcePackUrl) {
        this.resourcePackUrl = resourcePackUrl;
    }

    public String getResourcePackSha1Sum() {
        return resourcePackSha1Sum;
    }

    public void setResourcePackSha1Sum(String resourcePackSha1Sum) {
        this.resourcePackSha1Sum = resourcePackSha1Sum;
    }

    public String getReportDataStorageType() {
        return reportDataStorageType;
    }

    public void setReportDataStorageType(String reportDataStorageType) {
        this.reportDataStorageType = reportDataStorageType;
    }

    public String getReportDataMongodbConnection() {
        return reportDataMongodbConnection;
    }

    public void setReportDataMongodbConnection(String reportDataMongodbConnection) {
        this.reportDataMongodbConnection = reportDataMongodbConnection;
    }

    public String getReportDataMongodbDatabase() {
        return reportDataMongodbDatabase;
    }

    public void setReportDataMongodbDatabase(String reportDataMongodbDatabase) {
        this.reportDataMongodbDatabase = reportDataMongodbDatabase;
    }

    public boolean isStopAfterGame() {
        return stopAfterGame;
    }

    public void setStopAfterGame(boolean stopAfterGame) {
        this.stopAfterGame = stopAfterGame;
    }

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

    public boolean isOverrideLang() {
        return overrideLang;
    }

    public void setOverrideLang(boolean overrideLang) {
        this.overrideLang = overrideLang;
    }
}
