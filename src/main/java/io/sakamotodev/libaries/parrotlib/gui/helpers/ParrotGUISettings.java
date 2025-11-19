package io.sakamotodev.libaries.parrotlib.gui.helpers;

import java.util.HashMap;
import java.util.Map;

public class ParrotGUISettings {

    private boolean preventClose = false;
    private boolean disableShiftClick = true;
    private long clickCooldownMs = 0;
    private final Map<Integer, Long> slotCooldowns = new HashMap<>();

    private final int rows;

    public ParrotGUISettings(int rows) {
        this.rows = rows;
    }

    public ParrotGUISettings preventClose(boolean v) {
        this.preventClose = v;
        return this;
    }

    public ParrotGUISettings disableShiftClick(boolean v) {
        this.disableShiftClick = v;
        return this;
    }

    public ParrotGUISettings clickCooldown(long ms) {
        this.clickCooldownMs = ms;
        return this;
    }

    public ParrotGUISettings slotCooldown(int slot, long ms) {
        slotCooldowns.put(slot, ms);
        return this;
    }

    public long clickCooldown() {
        return clickCooldownMs;
    }

    public long slotCooldown(int slot) {
        return slotCooldowns.getOrDefault(slot, 0L);
    }

    public boolean preventClose() {
        return preventClose;
    }

    public boolean shiftClickDisabled() {
        return disableShiftClick;
    }

    public int rows() {
        return rows;
    }
}
