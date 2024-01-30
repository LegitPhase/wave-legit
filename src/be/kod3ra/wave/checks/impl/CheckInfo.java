/*
 * Decompiled with CFR 0.152.
 */
package be.kod3ra.wave.checks.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface CheckInfo {
    public String name();
}

