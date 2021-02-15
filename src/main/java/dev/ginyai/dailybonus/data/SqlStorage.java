package dev.ginyai.dailybonus.data;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.api.data.DataException;
import dev.ginyai.dailybonus.api.data.IStorage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SqlStorage implements IStorage {
    protected static final String TABLE_PLAYER = "Player";
    protected static final String TABLE_ONLINE = "Online";
    protected static final String TABLE_PUNCH = "Punch";
    protected static final String TABLE_BONUS = "Bonus";
    private static final Lock updatePlayerNameLock = new ReentrantLock();
    private static final Lock setOnlineTimeLock = new ReentrantLock();
    private static final Lock checkAndMarkReceivedLock = new ReentrantLock();
    protected final String tablePrefix;
    private final DataSourceSupplier dataSourceSupplier;

    public SqlStorage(String tablePrefix, DataSourceSupplier dataSourceSupplier) {
        this.tablePrefix = tablePrefix;
        this.dataSourceSupplier = dataSourceSupplier;
    }

    protected Connection getConnection() throws SQLException {
        return dataSourceSupplier.get().getConnection();
    }

    @Override
    public void setup() throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement playerTable = connection.prepareStatement(String.format(
                "create table if not exists %s (" +
                    "`UUID` char(36) not null, " +
                    "`Name` varchar(256) not null, primary key(UUID))"
                , tablePrefix + TABLE_PLAYER));
            playerTable.execute();
            PreparedStatement onlineTime = connection.prepareStatement(String.format(
                "create table if not exists %s (" +
                    "`UUID` char(36) not null, " +
                    "`Online` long not null," +
                    "`Date` date not null, primary key(UUID, Date))"
                , tablePrefix + TABLE_ONLINE));
            onlineTime.execute();
            PreparedStatement punchTable = connection.prepareStatement(String.format(
                "create table if not exists %s (" +
                    "`ID` int unsigned not null auto_increment, " +
                    "`UUID` char(36) not null, " +
                    "`Group` varchar(256) not null, " +
                    "`Date` date not null, " +
                    "`Time` timestamp not null, primary key(ID))"
                , tablePrefix + TABLE_PUNCH));
            punchTable.execute();
            PreparedStatement bonusTable = connection.prepareStatement(String.format(
                "create table if not exists %s (" +
                    "`ID` int unsigned not null auto_increment, " +
                    "`UUID` char(36) not null, " +
                    "`Bonus` varchar(256) not null, " +
                    "`Time` timestamp not null, primary key(ID))"
                , tablePrefix + TABLE_BONUS));
            bonusTable.execute();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public Optional<String> getPlayerName(UUID uuid) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(String.format(
                "select * from %s where `UUID` = ?", tablePrefix + TABLE_PLAYER));
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getString("Name"));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataException("GetPlayerName: ", e);
        }
    }

    @Override
    public void updatePlayerName(UUID uuid, String name) throws DataException {
        try {
            updatePlayerNameLock.lock();
            try (Connection connection = getConnection()) {
                PreparedStatement query = connection.prepareStatement(String.format(
                    "select * from %s where `UUID` = ?", tablePrefix + TABLE_PLAYER));
                query.setString(1, uuid.toString());
                ResultSet resultSet = query.executeQuery();
                PreparedStatement update;
                if (resultSet.next()) {
                    update = connection.prepareStatement(String.format(
                        "update %s set `Name` = ? where `UUID` = ?", tablePrefix + TABLE_PLAYER));
                } else {
                    update = connection.prepareStatement(String.format(
                        "insert into %s (`Name`, `UUID`) values(?, ?)", tablePrefix + TABLE_PLAYER));
                }
                update.setString(1, name);
                update.setString(2, uuid.toString());
                update.executeUpdate();
            } catch (SQLException e) {
                throw new DataException(e);
            }
        } finally {
            updatePlayerNameLock.unlock();
        }
    }

    @Override
    public boolean checkPunched(String group, UUID uuid, LocalDate date) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "select count(*) as count from %s where `UUID` = ? and `Group` = ? and `Date` = ?)", tablePrefix + TABLE_PUNCH));
            query.setString(1, uuid.toString());
            query.setString(2, group);
            query.setDate(3, Date.valueOf(date));
            ResultSet resultSet = query.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public int countPunchPoints(String group, UUID uuid, LocalDate from, LocalDate to) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "select count(distinct Date) as count from %s where `UUID` = ? and `Group` = ? and (`Date` between ? and ?)", tablePrefix + TABLE_PUNCH));
            query.setString(1, uuid.toString());
            query.setString(2, group);
            query.setDate(3, Date.valueOf(from));
            query.setDate(4, Date.valueOf(to));
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            } else {
                //todo: exception?
                return 0;
            }
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public Map<LocalDate, Instant> getPunchPoints(String group, UUID uuid, LocalDate from, LocalDate to) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "select * from %s where `UUID` = ? and `Group` = ? and (`Date` between ? and ?)", tablePrefix + TABLE_PUNCH));
            query.setString(1, uuid.toString());
            query.setString(2, group);
            query.setDate(3, Date.valueOf(from));
            query.setDate(4, Date.valueOf(to));
            ResultSet resultSet = query.executeQuery();
            ImmutableMap.Builder<LocalDate, Instant> builder = ImmutableMap.builder();
            while (resultSet.next()) {
                builder.put(resultSet.getDate("Date").toLocalDate(), resultSet.getTimestamp("Time").toInstant());
            }
            return builder.build();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public void punch(String group, UUID uuid, LocalDate date, Instant instant) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                String.format("insert into %s (`UUID`, `Group`, `Date`, `Time`) values (?, ?, ?, ?)", tablePrefix + TABLE_PUNCH));
            statement.setString(1, uuid.toString());
            statement.setString(2, group);
            statement.setDate(3, Date.valueOf(date));
            statement.setTimestamp(4, Timestamp.from(instant));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataException(e);
        }

    }

    @Override
    public long getOnlineTime(UUID uuid, LocalDate localDate) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "select * from %s where `UUID` = ? and `Date` = ?", tablePrefix + TABLE_ONLINE));
            query.setString(1, uuid.toString());
            query.setDate(2, Date.valueOf(localDate));
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("Online");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public long getTotalOnlineTime(UUID uuid) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "select * from %s where `UUID` = ?", tablePrefix + TABLE_ONLINE));
            query.setString(1, uuid.toString());
            ResultSet resultSet = query.executeQuery();
            long count = 0;
            while (resultSet.next()) {
                count += resultSet.getInt("Online");
            }
            return count;
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public long getTotalOnlineTime(UUID uuid, LocalDate from, LocalDate to) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "select * from %s where `UUID` = ? and (`Date` between ? and ?)", tablePrefix + TABLE_ONLINE));
            query.setString(1, uuid.toString());
            query.setDate(2, Date.valueOf(from));
            query.setDate(3, Date.valueOf(to));
            ResultSet resultSet = query.executeQuery();
            long count = 0;
            while (resultSet.next()) {
                count += resultSet.getInt("Online");
            }
            return count;
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public Map<LocalDate, Integer> getOnlineTimeByDay(UUID uuid, LocalDate from, LocalDate to) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "select * from %s where `UUID` = ? and (`Date` between ? and ?)", tablePrefix + TABLE_ONLINE));
            query.setString(1, uuid.toString());
            query.setDate(2, Date.valueOf(from));
            query.setDate(3, Date.valueOf(to));
            ResultSet resultSet = query.executeQuery();
            ImmutableMap.Builder<LocalDate, Integer> builder = ImmutableMap.builder();
            while (resultSet.next()) {
                builder.put(resultSet.getDate("Date").toLocalDate(), resultSet.getInt("Online"));
            }
            return builder.build();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public void setOnlineTime(UUID uuid, LocalDate date, long time) throws DataException {
        try {
            setOnlineTimeLock.lock();
            try (Connection connection = getConnection()) {
                PreparedStatement query = connection.prepareStatement(String.format(
                    "select * from %s where `UUID` = ? and `Date` = ?", tablePrefix + TABLE_ONLINE));
                query.setString(1, uuid.toString());
                query.setDate(2, Date.valueOf(date));
                ResultSet resultSet = query.executeQuery();
                PreparedStatement update;
                if (resultSet.next()) {
                    update = connection.prepareStatement(String.format(
                        "update %s set `Online` = ? where `UUID` = ? and `Date` = ?", tablePrefix + TABLE_ONLINE));
                } else {
                    update = connection.prepareStatement(String.format(
                        "insert into %s (`Online`, `UUID`, `Date`) values(?, ?, ?)", tablePrefix + TABLE_ONLINE));
                }
                update.setLong(1, time);
                update.setString(2, uuid.toString());
                update.setDate(3, Date.valueOf(date));
                update.executeUpdate();
            } catch (SQLException e) {
                throw new DataException(e);
            }
        } finally {
            setOnlineTimeLock.unlock();
        }
    }

    @Override
    public boolean checkReceived(String bonusId, UUID uuid) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "select * from %s where `UUID` = ? and `Bonus` = ?", tablePrefix + TABLE_BONUS));
            query.setString(1, uuid.toString());
            query.setString(2, bonusId);
            ResultSet resultSet = query.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public boolean checkReceived(String bonusId, UUID uuid, Instant from, Instant to) throws DataException {
        try (Connection connection = getConnection()) {
            PreparedStatement query = connection.prepareStatement(String.format(
                "select * from %s where `UUID` = ? and `Bonus` = ? and (`Time` between ? and ?)", tablePrefix + TABLE_BONUS));
            query.setString(1, uuid.toString());
            query.setString(2, bonusId);
            query.setTimestamp(3, Timestamp.from(from));
            query.setTimestamp(4, Timestamp.from(to));
            ResultSet resultSet = query.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public boolean checkAndMarkReceived(String bonusId, UUID uuid, Instant instant) throws DataException {
        try {
            checkAndMarkReceivedLock.lock();
            if (!checkReceived(bonusId, uuid)) {
                try (Connection connection = getConnection()) {
                    PreparedStatement query = connection.prepareStatement(String.format(
                        "insert into %s (`UUID`, `Bonus`, `Time`) values (?, ?, ?)", tablePrefix + TABLE_BONUS));
                    query.setString(1, uuid.toString());
                    query.setString(2, bonusId);
                    query.setTimestamp(3, Timestamp.from(instant));
                    return query.executeUpdate() > 0;
                } catch (SQLException e) {
                    throw new DataException(e);
                }
            }
            return false;
        } finally {
            checkAndMarkReceivedLock.unlock();
        }
    }

    @Override
    public boolean checkAndMarkReceived(String bonusId, UUID uuid, Instant instant, Instant from, Instant to) throws DataException {
        try {
            checkAndMarkReceivedLock.lock();
            if (!checkReceived(bonusId, uuid, from, to)) {
                try (Connection connection = getConnection()) {
                    PreparedStatement query = connection.prepareStatement(String.format(
                        "insert into %s (`UUID`, `Bonus`, `Time`) values (?, ?, ?)", tablePrefix + TABLE_BONUS));
                    query.setString(1, uuid.toString());
                    query.setString(2, bonusId);
                    query.setTimestamp(3, Timestamp.from(instant));
                    return query.executeUpdate() > 0;
                } catch (SQLException e) {
                    throw new DataException(e);
                }
            }
            return false;
        } finally {
            checkAndMarkReceivedLock.unlock();
        }
    }
}
