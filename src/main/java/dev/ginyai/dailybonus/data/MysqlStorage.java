package dev.ginyai.dailybonus.data;

import dev.ginyai.dailybonus.api.data.DataException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Supplier;

public class MysqlStorage extends SqlStorage {

    public MysqlStorage(String tablePrefix, DataSourceSupplier dataSourceSupplier) {
        super(tablePrefix, dataSourceSupplier);
    }

    @Override
    public void updatePlayerName(UUID uuid, String name) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement update = connection.prepareStatement(String.format(
                "replace into %s (`UUID`, `Name`) values(?, ?)", tablePrefix + TABLE_PLAYER));
            update.setString(1, uuid.toString());
            update.setString(2, name);
            update.executeUpdate();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public void setOnlineTime(UUID uuid, LocalDate date, long time) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement update = connection.prepareStatement(String.format(
                "replace into %s (`UUID`, `Online`, `Date`) values(?, ?, ?)", tablePrefix + TABLE_ONLINE));
            update.setString(1, uuid.toString());
            update.setLong(2, time);
            update.setDate(3, Date.valueOf(date));
            update.executeUpdate();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public boolean checkAndMarkReceived(String bonusId, UUID uuid, Instant instant) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "insert into %s (`UUID`, `Bonus`, `Time`) " +
                    "select ?, ?, ? from DUAL " +
                    "where not exists (select * from %s where `UUID` = ? and `Bonus` = ?)", tablePrefix + TABLE_BONUS, tablePrefix + TABLE_BONUS));
            query.setString(1, uuid.toString());
            query.setString(2, bonusId);
            query.setTimestamp(3, Timestamp.from(instant));
            query.setString(4, uuid.toString());
            query.setString(5, bonusId);
            return query.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public boolean checkAndMarkReceived(String bonusId, UUID uuid, Instant instant, Instant from, Instant to) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "insert into %s (`UUID`, `Bonus`, `Time`) " +
                    "select ?, ?, ? from DUAL " +
                    "where not exists (select * from %s where `UUID` = ? and `Bonus` = ? and (`Time` between ? and ?))", tablePrefix + TABLE_BONUS, tablePrefix + TABLE_BONUS));
            query.setString(1, uuid.toString());
            query.setString(2, bonusId);
            query.setTimestamp(3, Timestamp.from(instant));
            query.setString(4, uuid.toString());
            query.setString(5, bonusId);
            query.setTimestamp(6, Timestamp.from(from));
            query.setTimestamp(7, Timestamp.from(to));
            return query.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }
}
