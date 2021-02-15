package dev.ginyai.dailybonus.data;

import javax.sql.DataSource;
import java.sql.SQLException;

@FunctionalInterface
public interface DataSourceSupplier {
    DataSource get() throws SQLException;
}
