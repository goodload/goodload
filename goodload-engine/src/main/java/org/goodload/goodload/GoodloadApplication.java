/*
 * Copyright (C) 2021 Goodload
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
package org.goodload.goodload;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.goodload.goodload.criteria.MinimumFailCountCriteria;
import org.goodload.goodload.criteria.PercentFailCriteria;
import org.goodload.goodload.exceptions.GoodloadRuntimeException;
import org.goodload.goodload.exceptions.InvalidSimulationConfigFileException;
import org.goodload.goodload.exceptions.JarFileNotFoundException;
import org.goodload.goodload.exceptions.UnsupportedCriteriaException;
import org.goodload.goodload.execution.Simulator;
import org.goodload.goodload.reporting.ReportExporter;
import org.goodload.goodload.reporting.reports.aggregate.AggregateSimulationReport;
import org.goodload.goodload.userconfig.GoodloadUserConfigurationProperties;
import org.goodload.goodload.userconfig.ParsedUserArgs;
import org.goodload.goodload.userconfig.SimulationConfiguration;
import org.goodload.goodload.userconfig.UserArgs;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * The main class that starts the simulation.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@SpringBootApplication
@Slf4j
@ConfigurationPropertiesScan
public class GoodloadApplication implements CommandLineRunner {

    @Resource
    private UserArgs userArgs;

    @Resource
    private ParsedUserArgs parsedUserArgs;

    @Resource
    private Simulator simulator;

    @Resource
    private ReportExporter reportExporter;

    public static void main(String... args) {
        System.out.println("Goodload Engine Copyright (C) 2021  Goodload\n" +
                "This program has been distributed under GNU General Public License " +
                "and comes with ABSOLUTELY NO WARRANTY. " +
                "You should have received a copy of the GNU General Public License along with this program. " +
                "If not, see <https://www.gnu.org/licenses/>.");

        System.exit(
                SpringApplication.exit(
                        SpringApplication.run(GoodloadApplication.class, args)
                )
        );
    }

    /**
     * Read the user's arguments and run the simulations.
     * @param args Command line arguments.
     * @throws Exception Any exception that occurs while starting or running simulations.
     */
    @Override
    public void run(String... args) throws Exception {

        log.debug("Current path: {}", System.getProperty("user.dir"));

        parseArguments(args);

        loadExecutionConfiguration();

        loadSimulationJar();

        createSimulationExecutionThreadPool();

        var reports = new ArrayList<AggregateSimulationReport>();

        for(SimulationConfiguration simulation: userArgs.getYamlConfiguration().getSimulations()) {
            var report = simulator.execute(simulation);
            if(report != null) {
                reports.add(report);
            }
        }

        reportExporter.export(reports);
    }

    /**
     * Creates thread pool to execute simulations.
     * Uses the maximum value of concurrency across simulations as the size of the pool
     */
    private void createSimulationExecutionThreadPool() {
        int maxConcurrency = userArgs.getYamlConfiguration().getSimulations().stream()
                .map(SimulationConfiguration::getConcurrency)
                .max(Comparator.comparingInt(o -> o))
                .orElseThrow(NoSuchElementException::new);

        parsedUserArgs.setSimulationExecutorService(Executors.newFixedThreadPool(maxConcurrency));
    }

    /**
     * Loads the simulation jar file from the path passed as command line argument.
     * @throws JarFileNotFoundException If the jar file couldn't be found or opened.
     */
    private void loadSimulationJar() throws JarFileNotFoundException {
        try {
            var jarFile = new File(userArgs.getJarFilePath());
            if(!jarFile.exists() || !jarFile.canRead()) {
                throw new JarFileNotFoundException(
                        String.format("Could not open jar file %s. Make sure that the file exists and is readable.",
                        jarFile.getAbsolutePath()));
            }

            parsedUserArgs.setUserSimulationsClassLoader(new URLClassLoader(
                    new URL[] { new File(userArgs.getJarFilePath()).toURI().toURL() },
                    ClassLoader.getSystemClassLoader()));
        } catch (MalformedURLException e) {
            throw new JarFileNotFoundException(
                    String.format("The path to jar file (%s) is invalid",
                            userArgs.getJarFilePath()),
                    e);
        }
    }

    /**
     * Loads/parsed the execution configuration from the config file path passed s command line
     * argumnent.
     * @throws InvalidSimulationConfigFileException If the config file couldn't be parsed properly.
     */
    private void loadExecutionConfiguration() throws InvalidSimulationConfigFileException {
        var mapper = new ObjectMapper(new YAMLFactory());
        try {
            var config = mapper.readValue(new File(userArgs.getConfigFilePath()), GoodloadUserConfigurationProperties.class);
            userArgs.setYamlConfiguration(config);

            parseCriteria(config);

        } catch(UnsupportedCriteriaException e) {
          throw new InvalidSimulationConfigFileException(e.getMessage(), e);
        } catch(JsonParseException | JsonMappingException e) {
            throw new InvalidSimulationConfigFileException(
                    String.format("The configuration file %s is not well-formed." +
                            "Make sure that the syntax is correct and all the required fields (if any) " +
                            "have been provided.", userArgs.getConfigFilePath()),
                    e);
        } catch(IOException e) {
            throw new InvalidSimulationConfigFileException(
                    String.format("Failed to read the configuration file %s. " +
                            "Make sure that the file is present and accessible.",
                            userArgs.getConfigFilePath()),
                    e);
        }
    }

    private void parseCriteria(GoodloadUserConfigurationProperties config) throws UnsupportedCriteriaException {
        final var percentFailCriteriaPattern =
                Pattern.compile("([0-9]*)(%)( )+(failure)(s)?", Pattern.CASE_INSENSITIVE);
        final var minimumFailCriteriaPattern =
                Pattern.compile("atleast ([0-9]*)( )+(failure)(s)?", Pattern.CASE_INSENSITIVE);

        for(var criteriaStr: config.getFailPassCriteria()) {
            if(criteriaStr.matches(percentFailCriteriaPattern.pattern())) {
                parsedUserArgs.getFailPassCriteria().add(new PercentFailCriteria(
                        Long.parseLong(percentFailCriteriaPattern.matcher(criteriaStr).group(0))
                ));
            } else if(criteriaStr.matches(minimumFailCriteriaPattern.pattern())) {
                parsedUserArgs.getFailPassCriteria().add(new MinimumFailCountCriteria(
                        Long.parseLong(percentFailCriteriaPattern.matcher(criteriaStr).group(1))
                ));
            } else {
                throw new UnsupportedCriteriaException(String.format(
                        "The fail-when criteria '%s' is invalid. Make sure the syntax is correct.",
                        criteriaStr));
            }
        }
        if(parsedUserArgs.getFailPassCriteria().isEmpty()) {
            parsedUserArgs.getFailPassCriteria().add(new MinimumFailCountCriteria(1));
        }
    }

    /**
     * Parses the arguments and returns error messages if the arguments are invalid.
     * The values of the parsed args are put in UserArgs bean.
     * @param args The args passed by the user.
     */
    private void parseArguments(@NonNull String[] args) throws ParseException {
        var options = addAllDefinedOptions();

        CommandLineParser parser = new DefaultParser();
        var formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            var footer = "Report issues at https://github.com/divyanshshekhar/goodload/issues";
            formatter.printHelp("goodload", null, options, footer, true);

            throw e;
        }

        userArgs.setConfigFilePath(cmd.getOptionValue(CommandLineOptions.CONFIG_FILE_OPTION.getLongOpt()));
        userArgs.setJarFilePath(cmd.getOptionValue(CommandLineOptions.JAR_FILE_OPTION.getLongOpt()));
        userArgs.setSimulationsToExecute(cmd.getOptionValues(CommandLineOptions.SIMULATION_OPTION.getLongOpt()));
    }

    /**
     * Add all the options that are defined in the {@link GoodloadApplication.CommandLineOptions} class
     * @return Options object that has all the options defined in the {@code CommandLineOptions} class
     */
    @NonNull
    private Options addAllDefinedOptions() {
        var options = new Options();

        Arrays.stream(CommandLineOptions.class.getDeclaredFields()).sequential()
                .filter(field -> field.getType().equals(Option.class)
                        && java.lang.reflect.Modifier.isStatic(field.getModifiers()))
                .map(field -> {
                    try {
                        return (Option) field.get(this);
                    } catch (IllegalAccessException e) {
                        throw new GoodloadRuntimeException(
                                "Error occurred while adding options to command line parser.", e);
                    }
                })
                .forEach(options::addOption);
        return options;
    }

    /**
     * Define all the available options as {@code static final org.apache.commons.cli.Option <identifier>}.
     * The identifier can be anything.
     * Any option that is defined here will be automatically picked up by the CommandLineParser and hence,
     * no need to add them manually to a {@link org.apache.commons.cli.Options} object.
     * You only need to process the args once they have been parsed using {@code CommandLine.getOptionValue(String)}
     */
    private static class CommandLineOptions {
        static final Option CONFIG_FILE_OPTION = Option
                .builder("c")
                .longOpt("config")
                .argName("path to config YAML file")
                .desc("simulation configuration yaml file")
                .required()
                .hasArg()
                .build();

        static final Option JAR_FILE_OPTION = Option
                .builder("j")
                .longOpt("jar")
                .argName("path to jar file")
                .desc("jar file that contains definitions of your simulations")
                .hasArg()
                .required()
                .build();

        static final Option SIMULATION_OPTION = Option
                .builder("s")
                .longOpt("simulation")
                .argName("simulation names")
                .desc("the names of simulations to be executed. " +
                        "If not provided, then all the simulations defined in config yaml will be run.")
                .optionalArg(true)
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .build();
    }
}
