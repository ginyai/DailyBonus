package dev.ginyai.dailybonus.api.data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface IStorage {

    void setup() throws DataException;

    Optional<String> getPlayerName(UUID uuid) throws DataException;

    void updatePlayerName(UUID uuid, String name) throws DataException;

    boolean checkPunched(String group, UUID uuid, LocalDate date) throws DataException;

    int countPunchPoints(String group, UUID uuid, LocalDate from, LocalDate to) throws DataException;

    Map<LocalDate, Instant> getPunchPoints(String group, UUID uuid, LocalDate from, LocalDate to) throws DataException;

    void punch(String group, UUID uuid, LocalDate date, Instant instant) throws DataException;

    long getOnlineTime(UUID uuid, LocalDate localDate) throws DataException;

    long getTotalOnlineTime(UUID uuid) throws DataException;

    long getTotalOnlineTime(UUID uuid, LocalDate from, LocalDate to) throws DataException;

    Map<LocalDate, Integer> getOnlineTimeByDay(UUID uuid, LocalDate from, LocalDate to) throws DataException;

    void setOnlineTime(UUID uuid, LocalDate date, long time) throws DataException;

    boolean checkReceived(String id, UUID uuid) throws DataException;

    boolean checkReceived(String id, UUID uuid, Instant from, Instant to) throws DataException;

    boolean checkAndMarkReceived(String id, UUID uuid, Instant instant) throws DataException;

    boolean checkAndMarkReceived(String id, UUID uuid, Instant instant, Instant from, Instant to) throws DataException;
}
