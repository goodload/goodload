package org.divsgaur.goodload;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.divsgaur.goodload.exceptions.InvalidSimulationConfigFileException;
import org.divsgaur.goodload.userconfig.SimulationConfig;
import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class GoodloadApplication implements CommandLineRunner {

    @Resource
    private UserArgs userArgs;

    public static void main(String... args) {
        System.exit(
                SpringApplication.exit(
                        SpringApplication.run(GoodloadApplication.class, args)
                )
        );
    }

    @Override
    public void run(String... args) throws ParseException, InvalidSimulationConfigFileException {
        log.debug("Current path: {}", System.getProperty("user.dir"));
        parseArguments(args);

        loadExecutionConfiguration();
    }

    private void loadExecutionConfiguration() throws InvalidSimulationConfigFileException {
        var mapper = new ObjectMapper(new YAMLFactory());
        try {
            var config = mapper.readValue(new File(userArgs.getConfigFilePath()), SimulationConfig.class);
            userArgs.setConfiguration(config);
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

    /**
     * Parses the arguments and returns error messages if the arguments are invalid.
     * The values of the parsed args are put in UserArgs bean.
     * @param args The args passed by the user.
     */
    private void parseArguments(@NonNull String[] args) throws ParseException {
        var options = addAllDefinedOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            String footer = "Report issues at https://github.com/divyanshshekhar/goodload/issues";
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
        Options options = new Options();

        Arrays.stream(CommandLineOptions.class.getDeclaredFields()).sequential()
                .filter((field) -> field.getType().equals(Option.class)
                        && java.lang.reflect.Modifier.isStatic(field.getModifiers()))
                .map((field) -> {
                    try {
                        return (Option) field.get(this);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
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
