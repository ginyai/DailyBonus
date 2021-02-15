package dev.ginyai.dailybonus.data;

import dev.ginyai.dailybonus.DailyBonusPlugin;
import dev.ginyai.dailybonus.api.data.IStorage;
import dev.ginyai.dailybonus.config.StorageSettings;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import java.nio.file.Path;
import java.util.Locale;

public class StorageManager {

    //todo: mcdir
    public IStorage createStorage(StorageSettings settings, Path dataDir) {
        String dataType = settings.getStorageType();
        boolean isMysql;
        String jdbcUrl;
        if (settings.getJdbcUlr().isEmpty()) {
            switch (dataType.toLowerCase(Locale.ROOT)) {
                case "h2":
                    jdbcUrl = "jdbc:h2:file:" + settings.getDataSourceFile().replaceAll("%data_dir%", dataDir.toFile().getAbsolutePath().replace('\\', '/'));
                    isMysql = false;
                    break;
                case "sqlite":
                    jdbcUrl = "jdbc:sqlite:" + settings.getDataSourceFile().replaceAll("%data_dir%", dataDir.toString());
                    isMysql = false;
                    break;
                case "mysql":
                case "mariadb":
                    jdbcUrl = "jdbc:mysql://" + settings.getUsername() + ":" + settings.getPassword() + "@" + settings.getAddress() + ":" + settings.getPort() + "/" + settings.getDatabase();
                    isMysql = true;
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported data type " + dataType);
            }
        } else {
            isMysql = dataType.equalsIgnoreCase("mysql") || dataType.equalsIgnoreCase("mariadb");
            jdbcUrl = settings.getJdbcUlr();
        }
        DataSourceSupplier supplier = () -> Sponge.getServiceManager().provideUnchecked(SqlService.class)
            .getDataSource(DailyBonusPlugin.getInstance(), jdbcUrl);
        if (isMysql) {
            return new MysqlStorage(settings.getTablePrefix(), supplier);
        } else {
            return new SqlStorage(settings.getTablePrefix(), supplier);
        }
    }

    public void onClose() {
    }
}
