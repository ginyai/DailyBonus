package dev.ginyai.dailybonus.util;

import dev.ginyai.dailybonus.DailyBonusMain;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LoadingIssuesTracker implements AutoCloseable {

    private static LoadingIssuesTracker instance;

    public static LoadingIssuesTracker getInstance() {
        return instance;
    }

    private final DailyBonusMain dailyBonus;
    private boolean terminated = false;
    private final List<IssueEntry> entryList = new ArrayList<>();

    public LoadingIssuesTracker(DailyBonusMain dailyBonus) {
        this.dailyBonus = dailyBonus;
        instance = this;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void markTerminated() {
        terminated = true;
    }

    public void terminated(String msg, Throwable e) throws ReloadFailException {
        this.error(msg, e);
        terminated = true;
        throw new ReloadFailException(this, e);
    }

    public void error(String msg, Throwable e) {
        dailyBonus.getLogger().error(msg);
        e.printStackTrace();
        addEntry(new IssueEntry(Level.SEVERE, msg, e));
    }

    public void warning(String msg, Throwable e) {
        dailyBonus.getLogger().warn(msg);
        e.printStackTrace();
        addEntry(new IssueEntry(Level.WARNING, msg, e));
    }

    public void warning(String msg) {
        dailyBonus.getLogger().warn(msg);
        addEntry(new IssueEntry(Level.WARNING, msg, null));
    }

    public List<IssueEntry> getEntryList() {
        return entryList;
    }

    public boolean isFine() {
        return entryList.isEmpty();
    }

    public long countErrors() {
        return entryList.stream().filter(i -> i.level == Level.SEVERE).count();
    }

    public long countWarnings() {
        return entryList.stream().filter(i -> i.level == Level.WARNING).count();
    }

    protected void addEntry(IssueEntry entry) {
        entryList.add(entry);
    }

    @Override
    public void close() throws ReloadFailException {
        if (instance == this) {
            instance = null;
        }
        if (!terminated && !entryList.isEmpty()) {
            throw new ReloadFailException(this);
        }
    }

    public static class IssueEntry {
        private final Level level;
        private final String msg;
        private final Throwable throwable;

        private IssueEntry(Level level, String msg, Throwable throwable) {
            this.level = level;
            this.msg = msg;
            this.throwable = throwable;
        }
    }
}
