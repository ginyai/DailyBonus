package dev.ginyai.dailybonus.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@ConfigSerializable
public class StorageSettings {

    @Setting(value = "Type", comment = "Support: MySql, MariaDB, H2, SQLite, Default: H2")
    private String storageType = "H2";

    @Setting(value = "TablePrefix", comment = "For resolving table name conflict, Default: DailyBonus_")
    private String tablePrefix = "DailyBonus_";

    @Setting(value = "File", comment = "For H2 & SQLite Database, Placeholders: %data_dir% %mc_dir%, Default: %data_dir%/database.db")
    private String dataSourceFile = "%data_dir%/database";

    @Setting(value = "Address", comment = "For Mysql & MariaDB, Default: localhost")
    private String address = "localhost";

    @Setting(value = "Port", comment = "For Mysql & MariaDB, Default: 3306")
    private int port = 3306;

    @Setting(value = "Database", comment = "For Mysql & MariaDB.")
    private String database = "database";

    @Setting("User")
    private String username = "";

    @Setting("Password")
    private String password = "";

    @Setting(value = "JdbcUrl", comment = "Usable when you want to set the url directly, Default: \"\"")
    private String jdbcUlr = "";

    public StorageSettings() {}

    public String getStorageType() {
        return storageType;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public String getDataSourceFile() {
        return dataSourceFile;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getJdbcUlr() {
        return jdbcUlr;
    }
}
