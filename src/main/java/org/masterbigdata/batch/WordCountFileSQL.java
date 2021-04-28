package org.masterbigdata.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;

import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.util.Collector;

public class WordCountFileSQL {

    public static void main(String[] args) throws Exception {

        // set up execution environment
        String path = args[0];
        ExecutionEnvironment env
                = ExecutionEnvironment.getExecutionEnvironment();

        DataSource<String> text =env.readTextFile(path);
        BatchTableEnvironment tEnv = BatchTableEnvironment.create(env);

        DataSet<WC> input = text.flatMap(new Tokenizer());

        // register the DataSet as table "WordCount"
        tEnv.createTemporaryView("WordCount",  input);

        // run a SQL query on the Table and retrieve the result as a new Table
        Table table = tEnv.sqlQuery(
                "SELECT word, SUM(frequency) as frequency FROM WordCount GROUP BY word");

        DataSet<WC> result = tEnv.toDataSet(table, WC.class);

        result.print();
    }

    // *************************************************************************
    //     USER DATA TYPES
    // *************************************************************************

    /**
     * Simple POJO containing a word and its respective count.
     */
    public static class WC {
        public String word;
        public long frequency;

        // public constructor to make it a Flink POJO
        public WC() {}

        public WC(String word, long frequency) {
            this.word = word;
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return "WC " + word + " " + frequency;
        }
    }
    public static class Tokenizer implements FlatMapFunction<String, WC> {

        @Override
        public void flatMap(String value, Collector<WC> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new WC(token,1));
                }
            }
        }
    }
}
