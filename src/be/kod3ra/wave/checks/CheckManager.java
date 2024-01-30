/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableClassToInstanceMap
 *  com.google.common.collect.ImmutableClassToInstanceMap$Builder
 */
package be.kod3ra.wave.checks;

import be.kod3ra.wave.Wave;
import be.kod3ra.wave.checks.Check;
import be.kod3ra.wave.checks.impl.combat.AimAssistA;
import be.kod3ra.wave.checks.impl.combat.AutoClickerA;
import be.kod3ra.wave.checks.impl.combat.AutoClickerB;
import be.kod3ra.wave.checks.impl.combat.CriticalsA;
import be.kod3ra.wave.checks.impl.combat.KillAuraA;
import be.kod3ra.wave.checks.impl.combat.ReachA;
import be.kod3ra.wave.checks.impl.movement.FlightA;
import be.kod3ra.wave.checks.impl.movement.FlightB;
import be.kod3ra.wave.checks.impl.movement.FlightC;
import be.kod3ra.wave.checks.impl.movement.FlightD;
import be.kod3ra.wave.checks.impl.movement.JesusA;
import be.kod3ra.wave.checks.impl.movement.NoFallA;
import be.kod3ra.wave.checks.impl.movement.SpeedA;
import be.kod3ra.wave.checks.impl.movement.SpeedA2;
import be.kod3ra.wave.checks.impl.movement.StepA;
import be.kod3ra.wave.checks.impl.player.BadPacketsA;
import be.kod3ra.wave.checks.impl.player.BadPacketsB;
import be.kod3ra.wave.checks.impl.player.BadPacketsC;
import be.kod3ra.wave.checks.impl.player.FastBowA;
import be.kod3ra.wave.checks.impl.player.TimerA;
import com.google.common.collect.ImmutableClassToInstanceMap;

public final class CheckManager {
    private final ImmutableClassToInstanceMap<Check> classToInstanceMap = new ImmutableClassToInstanceMap.Builder().put(AimAssistA.class, (Object)new AimAssistA()).put(AutoClickerA.class, (Object)new AutoClickerA()).put(AutoClickerB.class, (Object)new AutoClickerB()).put(CriticalsA.class, (Object)new CriticalsA()).put(KillAuraA.class, (Object)new KillAuraA()).put(ReachA.class, (Object)new ReachA()).put(FlightA.class, (Object)new FlightA()).put(FlightB.class, (Object)new FlightB()).put(FlightC.class, (Object)new FlightC()).put(FlightD.class, (Object)new FlightD()).put(JesusA.class, (Object)new JesusA()).put(NoFallA.class, (Object)new NoFallA()).put(SpeedA.class, (Object)new SpeedA()).put(SpeedA2.class, (Object)new SpeedA2()).put(StepA.class, (Object)new StepA()).put(BadPacketsA.class, (Object)new BadPacketsA()).put(BadPacketsB.class, (Object)new BadPacketsB()).put(BadPacketsC.class, (Object)new BadPacketsC()).put(FastBowA.class, (Object)new FastBowA()).put(TimerA.class, (Object)new TimerA()).build();

    public CheckManager(Wave wave) {
    }

    public ImmutableClassToInstanceMap<Check> getClassToInstanceMap() {
        return this.classToInstanceMap;
    }
}

