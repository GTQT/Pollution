package meowmel.pollution.common.warpevent;

import meowmel.pollution.POConfig;
import meowmel.pollution.common.warpevent.events.WarpBlind;
import meowmel.pollution.common.warpevent.events.WarpBlink;
import meowmel.pollution.common.warpevent.events.WarpBlood;
import meowmel.pollution.common.warpevent.events.WarpCountdownBomb;
import meowmel.pollution.common.warpevent.events.WarpFakeExplosion;
import meowmel.pollution.common.warpevent.events.WarpFakeRain;
import meowmel.pollution.common.warpevent.events.WarpFall;
import meowmel.pollution.common.warpevent.events.WarpInventoryScramble;
import meowmel.pollution.common.warpevent.events.WarpJump;
import meowmel.pollution.common.warpevent.events.WarpJunk;
import meowmel.pollution.common.warpevent.events.WarpLightning;
import meowmel.pollution.common.warpevent.events.WarpMushrooms;
import meowmel.pollution.common.warpevent.events.WarpNausea;
import meowmel.pollution.common.warpevent.events.WarpObsidian;
import meowmel.pollution.common.warpevent.events.WarpPoison;
import meowmel.pollution.common.warpevent.events.WarpRain;
import meowmel.pollution.common.warpevent.events.WarpSwamp;
import meowmel.pollution.common.warpevent.events.WarpWind;
import meowmel.pollution.common.warpevent.events.WarpZombieSiege;
import meowmel.pollution.common.warpevent.events.WarpWither;

import java.util.function.Function;

/**
 * 扭曲事件注册表。枚举常量的顺序同时也是 POConfig.WarpEventSwitch 中
 * eventEnabled / eventMinWarp 数组的下标，改动顺序需同步更新配置注释。
 */
public enum WarpEvents {
    POISON("poison", 65, WarpPoison::new),
    NAUSEA("nausea", 45, WarpNausea::new),
    BLIND("blind", 60, WarpBlind::new),
    WITHER("wither", 100, WarpWither::new),
    JUMP("jump", 55, WarpJump::new),
    LIGHTNING("lightning", 80, WarpLightning::new),
    RAIN("rain", 90, WarpRain::new),
    WIND("wind", 70, WarpWind::new),
    MUSHROOMS("mushrooms", 40, WarpMushrooms::new),
    SWAMP("swamp", 85, WarpSwamp::new),
    FAKE_RAIN("fakerain", 50, WarpFakeRain::new),
    BLINK("blink", 95, WarpBlink::new),
    BLOOD("blood", 75, WarpBlood::new),
    OBSIDIAN("obsidian", 110, WarpObsidian::new),
    FALL("fall", 100, WarpFall::new),
    FAKE_EXPLOSION("fakeexplosion", 60, WarpFakeExplosion::new),
    SIEGE("siege", 120, WarpZombieSiege::new),
    COUNTDOWN_BOMB("countdownbomb", 90, WarpCountdownBomb::new),
    JUNK("junk", 30, WarpJunk::new),
    INVENTORY_SCRAMBLE("inventoryscramble", 105, WarpInventoryScramble::new);

    private final String name;
    private final int defaultMinWarp;
    private final Function<Integer, IEventWarp> constructor;

    WarpEvents(String name, int defaultMinWarp, Function<Integer, IEventWarp> constructor) {
        this.name = name;
        this.defaultMinWarp = defaultMinWarp;
        this.constructor = constructor;
    }

    public String getName() {
        return name;
    }

    public int getDefaultMinWarp() {
        return defaultMinWarp;
    }

    /** 配置覆盖后的最低扭曲值（-1 表示用默认） */
    public int getMinWarp() {
        int override = POConfig.WarpEventSwitch.eventMinWarp[ordinal()];
        return override >= 0 ? override : defaultMinWarp;
    }

    public boolean isEnabled() {
        return POConfig.WarpEventSwitch.eventEnabled[ordinal()];
    }

    public IEventWarp instantiate() {
        return constructor.apply(getMinWarp());
    }

    /** 按事件名实例化（用于从 NBT 队列出队时重建事件） */
    public static IEventWarp byName(String name) {
        for (WarpEvents entry : values()) {
            if (entry.name.equals(name)) {
                return entry.instantiate();
            }
        }
        return null;
    }
}
