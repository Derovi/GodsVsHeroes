package by.dero.gvh;

import lombok.Getter;
import lombok.Setter;

public class Settings {
    @Getter @Setter
    private String mode;
    @Getter @Setter
    private boolean cristalix;
    @Getter @Setter
    private boolean overrideLang;
    @Getter @Setter
    private boolean stopAfterGame;
    @Getter @Setter
    private String locale;
    @Getter @Setter
    private String serverName;
    @Getter @Setter
    private String dataStorageType;
    @Getter @Setter
    private String dataMongodbConnection;
    @Getter @Setter
    private String dataMongodbDatabase;
    @Getter @Setter
    private String playerDataMongodbConnection;
    @Getter @Setter
    private String playerDataMongodbDatabase;
    @Getter @Setter
    private String lobbyDataStorageType;
    @Getter @Setter
    private String lobbyDataMongodbConnection;
    @Getter @Setter
    private String lobbyDataMongodbDatabase;
    @Getter @Setter
    private String serverDataMongodbConnection;
    @Getter @Setter
    private String serverDataMongodbDatabase;
    @Getter @Setter
    private String reportDataStorageType;
    @Getter @Setter
    private String reportDataMongodbConnection;
    @Getter @Setter
    private String reportDataMongodbDatabase;
    @Getter @Setter
    private String resourcePackUrl;
    @Getter @Setter
    private String resourcePackSha1Sum;
}
