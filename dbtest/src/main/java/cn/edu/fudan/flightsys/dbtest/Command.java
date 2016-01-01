package cn.edu.fudan.flightsys.dbtest;

import org.apache.commons.cli.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by junfeng on 12/10/15.
 */
public class Command {
    private static final List<String> ACTIONS =
            Arrays.asList("login", "searchTicket",
                    "orderPayTicket", "searchHistory", "all");

    public static void doTest(String DBClassname, int threadsNumber,
                              long target, long totalCount,
                              EnumAction action, LocalDate startDate, int days) throws Exception {
        long countPerThread = (totalCount / threadsNumber) + 1;
        long targetPerThread = (target / threadsNumber) + 1;
        System.out.println("action=" + action);
        System.out.println("threadsNumber=" + threadsNumber);
        System.out.println("targetPerThread=" + targetPerThread);
        System.out.println("countPerThread=" + countPerThread);

        List<Client> clients = new ArrayList<>(threadsNumber);
        for (int i = 0; i < threadsNumber; ++i) {
            DB db = (DB) Class.forName(DBClassname).getConstructor().newInstance();
            clients.add(new Client(db, targetPerThread, countPerThread,
                    action, startDate, days));
        }
        long st = System.nanoTime();
        for (Client client : clients) {
            client.start();
        }
        for (Client client : clients) {
            client.join();
        }
        long ed = System.nanoTime();
        long used = ed - st;
        long allTime = threadsNumber * used - RecordSleep.getInstance().sum();
        totalCount = countPerThread * threadsNumber;
        System.out.format("total operations=%d, time used(nano)=%d%n", totalCount, used);
        System.out.format("throughput(per second)=%f, averaged delay time(nano)=%f%n",
                (double) totalCount * Client.SECOND / used, (double) allTime / totalCount);
    }


    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(Option.builder()
                .longOpt("db")
                .argName("classname")
                .desc("test DB's full classname")
                .hasArg().build()
        );
        options.addOption("h", "help", false, "print help message");

        options.addOption(Option.builder()
                .longOpt("threads")
                .desc("number of threads (default 1)")
                .hasArg()
                .argName("number").build()
        );

        options.addOption(Option.builder("a")
                .longOpt("action")
                .desc("action to test (login|searchTicket|orderPayTicket|searchHistory|all)")
                .hasArg()
                .argName("action").build()
        );

        options.addOption(Option.builder("t")
                .longOpt("target")
                .desc("target ops/sec (default: unlimited)")
                .hasArg()
                .argName("number").build()
        );

        options.addOption(Option.builder("c")
                .longOpt("count")
                .desc("total operation count (default: 100,000)")
                .hasArg()
                .argName("number").build()
        );

        options.addOption(Option.builder()
                .longOpt("start")
                .desc("start date for test")
                .hasArg()
                .argName("year-month-day").build()
        );

        options.addOption(Option.builder()
                .longOpt("days")
                .desc("how long from start date")
                .hasArg()
                .argName("day number").build()
        );


        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("dbtest", options);
                System.exit(0);
            }
            if (!line.hasOption("db")) {
                System.err.println("need specify test DB's classname");
                System.exit(1);
            }

            String action = line.getOptionValue("action");
            if (!ACTIONS.contains(action)) {
                System.err.println("need a valid action "
                        + "(login|searchTicket|orderPayTicket|searchHistory|all)");
                System.exit(1);
            }
            EnumAction enumAction = EnumAction.valueOf(action);
            String DBClassname = line.getOptionValue("db");
            int threadsNumber = Integer.valueOf(line.getOptionValue("threads", "1"));
            long target = Long.valueOf(line.getOptionValue("target", "0"));
            long totalCount = Long.valueOf(line.getOptionValue("count", "100000"));
            if (totalCount <= 0 || target < 0) {
                System.err.println("total count and target need > 0");
                System.exit(1);
            }
            if (0 == target || target > totalCount) {
                System.err.println("set target equals to totalCount");
                target = totalCount;
            }
            LocalDate startDate = LocalDate.now();
            int days = 0;

            if (enumAction != EnumAction.login) {
                if (!line.hasOption("start") || !line.hasOption("days")) {
                    System.err.println("need specify start date and days number");
                    System.exit(1);
                }
                startDate = LocalDate.parse(line.getOptionValue("start"));
                days = Integer.valueOf(line.getOptionValue("days"));
                if (days < 0) {
                    System.err.println("days number need > 0");
                    System.exit(1);
                }
            }

            doTest(DBClassname, threadsNumber, target, totalCount,
                    enumAction, startDate, days);

        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            System.exit(1);
        }
    }
}
