package by.dero.gvh;

public class Settings {
    private String mode;
    private String dataStorageType;
    private String dataMongodbConnection;
    private String dataMongodbDatabase;
    private String playerDataStorageType;
    private String playerDataMongodbConnection;
    private String playerDataMongodbDatabase;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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
}
