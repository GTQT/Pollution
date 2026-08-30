package meowmel.pollution.common.warpevent;

import meowmel.pollution.POConfig;
import meowmel.pollution.POConfig.WarpEventSwitch;
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
import meowmel.pollution.common.warpevent.events.WarpWither;
import meowmel.pollution.common.warpevent.events.WarpZombieSiege;

import java.util.function.Function;

/**
 * 扭曲事件注册表。每个常量携带独立的配置访问器，
 * 对应 POConfig.WarpEventSwitch 中该事件的独立开关与扭曲阈值字段。
 */
public enum WarpEvents {
    POISON("poison", 65, WarpPoison::new, c -> c.poisonEnabled, c -> c.poisonMinWarp),
    NAUSEA("nausea", 45, WarpNausea::new, c -> c.nauseaEnabled, c -> c.nauseaMinWarp),
    BLIND("blind", 60, WarpBlind::new, c -> c.blindEnabled, c -> c.blindMinWarp),
    WITHER("wither", 100, WarpWither::new, c -> c.witherEnabled, c -> c.witherMinWarp),
    JUMP("jump", 55, WarpJump::new, c -> c.jumpEnabled, c -> c.jumpMinWarp),
    LIGHTNING("lightning", 80, WarpLightning::new, c -> c.lightningEnabled, c -> c.lightningMinWarp),
    RAIN("rain", 90, WarpRain::new, c -> c.rainEnabled, c -> c.rainMinWarp),
    WIND("wind", 70, WarpWind::new, c -> c.windEnabled, c -> c.windMinWarp),
    MUSHROOMS("mushrooms", 40, WarpMushrooms::new, c -> c.mushroomsEnabled, c -> c.mushroomsMinWarp),
    SWAMP("swamp", 85, WarpSwamp::new, c -> c.swampEnabled, c -> c.swampMinWarp),
    FAKE_RAIN("fakerain", 50, WarpFakeRain::new, c -> c.fakerainEnabled, c -> c.fakerainMinWarp),
    BLINK("blink", 95, WarpBlink::new, c -> c.blinkEnabled, c -> c.blinkMinWarp),
    BLOOD("blood", 75, WarpBlood::new, c -> c.bloodEnabled, c -> c.bloodMinWarp),
    OBSIDIAN("obsidian", 110, WarpObsidian::new, c -> c.obsidianEnabled, c -> c.obsidianMinWarp),
    FALL("fall", 100, WarpFall::new, c -> c.fallEnabled, c -> c.fallMinWarp),
    FAKE_EXPLOSION("fakeexplosion", 60, WarpFakeExplosion::new, c -> c.fakeexplosionEnabled, c -> c.fakeexplosionMinWarp),
    SIEGE("siege", 120, WarpZombieSiege::new, c -> c.siegeEnabled, c -> c.siegeMinWarp),
    COUNTDOWN_BOMB("countdownbomb", 90, WarpCountdownBomb::new, c -> c.countdownbombEnabled, c -> c.countdownbombMinWarp),
    JUNK("junk", 30, WarpJunk::new, c -> c.junkEnabled, c -> c.junkMinWarp),
    INVENTORY_SCRAMBLE("inventoryscramble", 105, WarpInventoryScramble::new,
            c -> c.inventoryscrambleEnabled, c -> c.inventoryscrambleMinWarp);

    private final String name;
    private final int defaultMinWarp;
    private final Function<Integer, IEventWarp> constructor;
    private final Function<WarpEventSwitch, Boolean> enabledGetter;
    private final Function<WarpEventSwitch, Integer> minWarpGetter;

    WarpEvents(String name, int defaultMinWarp, Function<Integer, IEventWarp> constructor,
               Function<WarpEventSwitch, Boolean> enabledGetter, Function<WarpEventSwitch, Integer> minWarpGetter) {
        this.name = name;
        this.defaultMinWarp = defaultMinWarp;
        this.constructor = constructor;
        this.enabledGetter = enabledGetter;
        this.minWarpGetter = minWarpGetter;
    }

    public String getName() {
        return name;
    }

    public int getDefaultMinWarp() {
        return defaultMinWarp;
    }

    /** 配置覆盖后的最低扭曲值（-1 表示用默认） */
    public int getMinWarp() {
        int override = minWarpGetter.apply(POConfig.WarpEventSwitch);
        return override >= 0 ? override : defaultMinWarp;
    }

    public boolean isEnabled() {
        return enabledGetter.apply(POConfig.WarpEventSwitch);
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
