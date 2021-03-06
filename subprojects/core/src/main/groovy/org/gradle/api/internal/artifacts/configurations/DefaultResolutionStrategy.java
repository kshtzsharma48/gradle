/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.configurations;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ConflictResolution;
import org.gradle.api.artifacts.DependencyResolveDetails;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.api.artifacts.ResolutionStrategy;
import org.gradle.api.artifacts.cache.ResolutionRules;
import org.gradle.api.internal.Actions;
import org.gradle.api.internal.artifacts.configurations.conflicts.LatestConflictResolution;
import org.gradle.api.internal.artifacts.configurations.conflicts.StrictConflictResolution;
import org.gradle.api.internal.artifacts.configurations.dynamicversion.CachePolicy;
import org.gradle.api.internal.artifacts.configurations.dynamicversion.DefaultCachePolicy;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.gradle.util.GUtil.flattenElements;

/**
 * by Szczepan Faber, created at: 10/7/11
 */
public class DefaultResolutionStrategy implements ResolutionStrategyInternal {

    private Set<ModuleVersionSelector> forcedModules = new LinkedHashSet<ModuleVersionSelector>();
    private Set<Action<? super DependencyResolveDetails>> dependencyResolveActions = new LinkedHashSet<Action<? super DependencyResolveDetails>>();

    private ConflictResolution conflictResolution = new LatestConflictResolution();
    private final DefaultCachePolicy cachePolicy = new DefaultCachePolicy();

    public Set<ModuleVersionSelector> getForcedModules() {
        return forcedModules;
    }

    public ResolutionStrategy failOnVersionConflict() {
        this.conflictResolution = new StrictConflictResolution();
        return this;
    }

    public ConflictResolution getConflictResolution() {
        return this.conflictResolution;
    }

    public ResolutionRules getResolutionRules() {
        return cachePolicy;
    }

    public DefaultResolutionStrategy force(Object... forcedModuleNotations) {
        assert forcedModuleNotations != null : "forcedModuleNotations cannot be null";
        Set<ModuleVersionSelector> modules = new ForcedModuleNotationParser().parseNotation(forcedModuleNotations);
        this.forcedModules.addAll(modules);
        return this;
    }

    public ResolutionStrategy eachDependency(Action<? super DependencyResolveDetails> action) {
        dependencyResolveActions.add(action);
        return this;
    }

    public Action<DependencyResolveDetails> getDependencyResolveAction() {
        Collection allActions = flattenElements(new ModuleForcingResolveAction(forcedModules), dependencyResolveActions);
        return Actions.composite(allActions);
    }

    public DefaultResolutionStrategy setForcedModules(Object ... forcedModuleNotations) {
        Set<ModuleVersionSelector> forcedModules = new ForcedModuleNotationParser().parseNotation(forcedModuleNotations);
        this.forcedModules = forcedModules;
        return this;
    }

    public CachePolicy getCachePolicy() {
        return cachePolicy;
    }

    public void cacheDynamicVersionsFor(int value, String units) {
        TimeUnit timeUnit = TimeUnit.valueOf(units.toUpperCase());
        cacheDynamicVersionsFor(value, timeUnit);
    }

    public void cacheDynamicVersionsFor(int value, TimeUnit units) {
        this.cachePolicy.cacheDynamicVersionsFor(value, units);
    }

    public void cacheChangingModulesFor(int value, String units) {
        TimeUnit timeUnit = TimeUnit.valueOf(units.toUpperCase());
        cacheChangingModulesFor(value, timeUnit);
    }

    public void cacheChangingModulesFor(int value, TimeUnit units) {
        this.cachePolicy.cacheChangingModulesFor(value, units);
    }
}