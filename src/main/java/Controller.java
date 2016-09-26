import org.apache.commons.cli.*;

import org.apache.commons.cli.ParseException;

import java.io.*;

/**
 * Created by Sander on 21-9-2016.
 */
public class Controller {
    private static final String INCORRECT_FILENAME_O = "incorrect filename for option -o";
    private static final String INCORRECT_NUMBER_OF_ARGUMENTS = "incorrect number of arguments";
    private static final String INCORRECT_FILENAME_I = "incorrect filename for option -i";
    private CommandLine commandLine;
    private final Options options;

    Controller(String[] args) throws Exception {
        this.options = getOptions();
        CommandLineParser parser = new BasicParser();

        try {
            this.commandLine = parser.parse(this.options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println("Incorrect arguments: " + e.getMessage());
            printHelpPage();
            throw new Exception("Incorrect input");
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "Display this help page");
        options.addOption("i", "in-file", true, "Take input from given file");
        options.addOption("o", "out-file", true, "Output to given file location");
        return options;
    }

    private void printHelpPage() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("[-i <file name>] [-o <file name>] [h] <key>",
                "En/Decrypts stdin to stdout. using the rc4 encryption method",
                options, "");
    }


    public void run() throws ParseException, IOException {
        if (handleHelpOption()) return;

        checkNumberOfArguments();

        InputStream in = handleInputRedirect();
        OutputStream out = handleOutputRedirect();


        String key = commandLine.getArgs()[0];
        new RC4(key).run();

        handleStreamClosing(in, out);
    }

    private void checkNumberOfArguments() throws ParseException {
        if (commandLine.getArgs().length != 1) {
            System.out.println(INCORRECT_NUMBER_OF_ARGUMENTS);
            printHelpPage();
            throw new ParseException(INCORRECT_NUMBER_OF_ARGUMENTS);
        }
    }

    private boolean handleHelpOption() {
        if (commandLine.hasOption("h")) {
            printHelpPage();
            return true;
        }
        return false;
    }

    private OutputStream handleOutputRedirect() throws ParseException {
        OutputStream out = null;
        if (commandLine.hasOption("o")) {
            out = System.out;
            try {
                System.setOut(new PrintStream(new FileOutputStream(commandLine.getOptionValue("o"))));
            } catch (FileNotFoundException e) {
                System.out.println(INCORRECT_FILENAME_O);
                System.out.close();
                System.setOut(new PrintStream(out));
                throw new ParseException(INCORRECT_FILENAME_O);
            }
        }
        return out;
    }

    private InputStream handleInputRedirect() throws IOException, ParseException {
        InputStream in = null;
        if (commandLine.hasOption("i")) {
            in = System.in;
            try {
                System.setIn(new FileInputStream(commandLine.getOptionValue("i")));
            } catch (FileNotFoundException e) {
                System.out.println(INCORRECT_FILENAME_I);
                System.in.close();
                System.setIn(in);
                throw new ParseException(INCORRECT_FILENAME_I);
            }
        }
        return in;
    }

    private void handleStreamClosing(InputStream in, OutputStream out) throws IOException {
        if (in != null) {
            System.in.close();
            System.setIn(in);
        }
        if (out != null) {
            System.out.close();
            System.setOut(new PrintStream(out));
        }
    }

}
