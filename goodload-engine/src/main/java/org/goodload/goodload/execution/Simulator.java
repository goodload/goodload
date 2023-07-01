/*
 * Copyright (C) 2021 Divyansh Shekhar Gaur
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.goodload.goodload.execution;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.goodload.goodload.config.GoodloadConfigurationProperties;
import org.goodload.goodload.dsl.Action;
import org.goodload.goodload.dsl.Simulation;
import org.goodload.goodload.exceptions.SimulatorInterruptedException;
import org.goodload.goodload.internal.Util;
import org.goodload.goodload.reporting.data.ActionReport;
import org.goodload.goodload.reporting.data.SimulationTree;
import org.goodload.goodload.reporting.data.StepSkeletonData;
import org.goodload.goodload.reporting.datasink.Sink;
import org.goodload.goodload.userconfig.ParsedUserArgs;
import org.goodload.goodload.userconfig.SimulationConfiguration;
import org.goodload.goodload.userconfig.UserArgs;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

/**
 * As the name suggests,
 * it is responsible for running a simulation and generating report for that simulation.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Slf4j
@Component
public class Simulator {

    @Resource
    private UserArgs userArgs;

    @Resource
    private ParsedUserArgs parsedUserArgs;

    @Resource
    private GoodloadConfigurationProperties goodloadConfigurationProperties;

    @Resource
    private Sink sink;

    /**
     * Takes a simulation configuration and executes it.
     * Also generates the report for that simulation.
     *
     * @param simulationConfig The simulation to execute.
     */
    public void execute(SimulationConfiguration simulationConfig, int simulationId) throws
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
        if (!simulationConfig.isEnabled()) {
            log.info("Simulation `{}` ignored as it is disabled.", simulationConfig.getName());
            return;
        }

        log.info("Starting simulation `{}`", simulationConfig.getName());

        var simulationClass = Class.forName(
                simulationConfig.getFullClassName(),
                true,
                parsedUserArgs.getUserSimulationsClassLoader()).asSubclass(Simulation.class);

        // DO NOT REMOVE VARIABLE simulationInstance EVEN IF UNUSED
        // Creating an instance here is a verification that an instance can be created and the user's simulation class
        // is not invalid.
        // It will be harder to properly report the error if this verification is left for the runner threads.
        // It will also prevent the same errors from being thrown by every runner thread because the error will be
        // detected and handled before the runners are even started.
        var simulationInstance = simulationClass.getDeclaredConstructor().newInstance();

        var simulationMetadata = prepareSimulationMetadata(simulationInstance);

        sink.registerSimulationSkeletonData(simulationMetadata);

        long maxHoldFor = Util.parseDurationToMillis(goodloadConfigurationProperties.getMaxHoldFor());
        long simulationHoldFor = Util.parseDurationToMillis(simulationConfig.getHoldFor());

        if (maxHoldFor < simulationHoldFor) {
            log.warn("The hold-for duration {} is greater than max allowed value of {}, " +
                            "hence the simulation will be run only for {} duration.",
                    simulationConfig.getHoldFor(),
                    goodloadConfigurationProperties.getMaxHoldFor(),
                    goodloadConfigurationProperties.getMaxHoldFor());
        }

        long holdForMillis = Math.min(maxHoldFor, simulationHoldFor);

        // Forcibly terminate the runner threads if some long-running step in simulation is causing
        // it to run for more than 120% of the hold for value.
        // This prevents Denial of Service attacks due to infinite recursions or loops in simulation.
        long forceEndAfterDuration = (long)
                ((100.0 + goodloadConfigurationProperties.getGracePeriodPercentage()) / 100 * maxHoldFor);

        var runners = new ArrayList<SimulationRunner>(simulationConfig.getConcurrency());

        try (var actionReportPublisher = new SubmissionPublisher<ActionReport>()) {

            sink.registerPublisher(actionReportPublisher);

            for (var runnerId = 0; runnerId < simulationConfig.getConcurrency(); runnerId++) {
                var runner = new SimulationRunner(
                        runnerId,
                        0,
                        simulationConfig,
                        simulationClass,
                        actionReportPublisher,
                        holdForMillis,
                        userArgs);
                runners.add(runner);
            }

            var futures = parsedUserArgs.getSimulationExecutorService().invokeAll(
                    runners,
                    forceEndAfterDuration,
                    TimeUnit.MILLISECONDS);
            for (var future : futures) {
                future.get();
            }

            sink.close();
        } catch (CancellationException e) {
            throw new SimulatorInterruptedException(
                    String.format(
                            "The simulation %s was cancelled forcefully because it exceeded max duration " +
                                    "(including %d grace period) of %d milliseconds.",
                            simulationConfig.getName(),
                            goodloadConfigurationProperties.getGracePeriodPercentage(),
                            forceEndAfterDuration),
                    e);
        } catch (ExecutionException e) {
            log.error("An exception occurred while executing the simulation", e.getCause());
        } catch (InterruptedException e) {
            log.error(String.format(
                            "The simulation `%s` was interrupted before completion.",
                            simulationConfig.getName()),
                    e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("Simulation `{}` completed.", simulationConfig.getName());

        log.info("Simulation `{}`: Generating aggregate report...", simulationConfig.getName());
        // TODO: Generate aggregate report
//
//        return reportAggregator.aggregate(
//                simulationConfig.getName(),
//                simulationReport,
//                simulationEndTime - simulationStartTime);
    }

    private static SimulationTree prepareSimulationMetadata(Simulation simulationInstance) {
        var simulationTree = new SimulationTree();
        simulationTree.setSimulationId(UUID.randomUUID().toString());
        simulationTree.setSimulationName(simulationInstance.getClass().getCanonicalName());

        var steps = new LinkedList<StepSkeletonData>();

        for (var action : simulationInstance.init()) {
            steps.add(mapActionToStepSkeletonData(action));
        }

        simulationTree.setSteps(steps);

        return simulationTree;
    }

    private static StepSkeletonData mapActionToStepSkeletonData(Action sourceAction) {
        var subSteps = sourceAction
                .getExecutionSequence()
                .stream()
                .filter(sequenceElement -> sequenceElement instanceof Action)
                .map(sequenceElement -> (Action) sequenceElement)
                .toList();

        var skeletonSubSteps = new LinkedList<StepSkeletonData>();

        for (var subStep : subSteps) {
            skeletonSubSteps.add(mapActionToStepSkeletonData(subStep));
        }

        var targetSkeletonData = new StepSkeletonData();
        targetSkeletonData.setStepName(sourceAction.getName());
        targetSkeletonData.setStepId(sourceAction.getId());
        targetSkeletonData.setSubSteps(skeletonSubSteps);

        return targetSkeletonData;
    }
}
