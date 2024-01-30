/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableClassToInstanceMap
 *  com.google.common.collect.ImmutableClassToInstanceMap$Builder
 */
package be.kod3ra.wave.checks;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.impl.combat.*;
import be.kod3ra.wave.checks.impl.movement.*;
import be.kod3ra.wave.checks.impl.player.*;
import com.google.common.collect.ImmutableClassToInstanceMap;

public final class CheckManager {
    private final ImmutableClassToInstanceMap<Check> classToInstanceMap = new ImmutableClassToInstanceMap.Builder().put(AimAssistA.class, new AimAssistA()).put(AutoClickerA.class, new AutoClickerA()).put(AutoClickerB.class, new AutoClickerB()).put(CriticalsA.class, new CriticalsA()).put(KillAuraA.class, new KillAuraA()).put(ReachA.class, new ReachA()).put(FlightA.class, new FlightA()).put(FlightB.class, new FlightB()).put(FlightC.class, new FlightC()).put(FlightD.class, new FlightD()).put(JesusA.class, new JesusA()).put(NoFallA.class, new NoFallA()).put(SpeedA.class, new SpeedA()).put(SpeedA2.class, new SpeedA2()).put(StepA.class, new StepA()).put(BadPacketsA.class, new BadPacketsA()).put(BadPacketsB.class, new BadPacketsB()).put(BadPacketsC.class, new BadPacketsC()).put(FastBowA.class, new FastBowA()).put(TimerA.class, new TimerA()).build();

    public CheckManager(Wave wave) {
    }

    public ImmutableClassToInstanceMap<Check> getClassToInstanceMap() {
        return this.classToInstanceMap;
    }
}

