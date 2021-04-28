package org.masterbigdata.batch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

/**
 * This class writes the value of a counter into a file periodically. The generated files are stored
 * in a directory.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class CounterProvider {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception(
                    "Invalid number of arguments. "
                            + "Usage: java org.uma.jmetalsp.externalsources.CounterProvider directory frequency");
        }

        String directory = args[0];

        long frequency = 10000; // Long.valueOf(args[1]) ;

        createDataDirectory(directory);

        new CounterProvider().start(directory, frequency);
    }

    private static void createDataDirectory(String outputDirectoryName) {
        File outputDirectory = new File(outputDirectoryName);

        if (outputDirectory.isDirectory()) {
            System.out.println("The output directory exists. Deleting contents ...");
            for (File file : outputDirectory.listFiles()) {
                file.delete();
            }
            // outputDirectory.delete();
        } else {
            System.out.println("The output directory doesn't exist. Creating ...");
        }

        new File(outputDirectoryName).mkdir();
    }

    private void start(String directory, long frequency) throws InterruptedException, IOException {
        int counter = 0;

        while (true) {
            System.out.println("Counter:" + counter);

            PrintWriter writer = new PrintWriter(directory + "/time." + counter, "UTF-8");
            writer.println("" + counter + ", " + new Timestamp(System.currentTimeMillis()));
            writer.close();

            Thread.sleep(frequency);

            counter++;
        }
    }
}
