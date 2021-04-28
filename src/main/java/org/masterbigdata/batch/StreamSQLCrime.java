package org.masterbigdata.batch;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


import java.util.Arrays;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.sources.CsvTableSource;

public class StreamSQLCrime {

    public static void main(String[] args) throws Exception {
        if(args !=null && args.length>0){
            String path = args[0];
            // set up execution environment
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

            CsvTableSource tableSource = CsvTableSource.builder()
                    .path(path)
                    .field("city", DataTypes.STRING())
                    .field("cluster",DataTypes.INT())
                    .field("murder",DataTypes.DOUBLE())
                    .field("assault",DataTypes.INT())
                    .field("urbanPop",DataTypes.INT())
                    .field("rape",DataTypes.DOUBLE())
                    .fieldDelimiter(",")
                    .ignoreFirstLine()
                    .ignoreParseErrors()
                    .lineDelimiter("\n").build();
            tEnv.createTemporaryView("crime", tableSource.getDataStream(env));
            Table table = tEnv.from("crime");
            table.printSchema();
            //Select
            Table result = tEnv.sqlQuery("SELECT * FROM " + table + " WHERE rape > 20");
            tEnv.toAppendStream(result, Crime.class).print();

            env.execute("Crime");
        }
    }

    // *************************************************************************
    //     USER DATA TYPES
    // *************************************************************************

    /**
     * Simple POJO.
     */
    public static class Crime {
        private  String city;
        private  int cluster;
        private  double murder;
        private  int assault;
        private int urbanPop;
        private double rape;

        public Crime(){

        }

        public Crime(String city, int cluster, double murder, int assault, int urbanPop, double rape) {
            this.city = city;
            this.cluster = cluster;
            this.murder = murder;
            this.assault = assault;
            this.urbanPop = urbanPop;
            this.rape = rape;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getCluster() {
            return cluster;
        }

        public void setCluster(int cluster) {
            this.cluster = cluster;
        }

        public double getMurder() {
            return murder;
        }

        public void setMurder(double murder) {
            this.murder = murder;
        }

        public int getAssault() {
            return assault;
        }

        public void setAssault(int assault) {
            this.assault = assault;
        }

        public int getUrbanPop() {
            return urbanPop;
        }

        public void setUrbanPop(int urbanPop) {
            this.urbanPop = urbanPop;
        }

        public double getRape() {
            return rape;
        }

        public void setRape(double rape) {
            this.rape = rape;
        }

        @Override
        public String toString() {
            return "Crime \n"+
                    "City: "+getCity()+"\n"+
                    "Cluster:  "+getCluster()+"\n" +
                    "Murder:  "+getMurder()+"\n" +
                    "Assault:  "+getAssault()+"\n" +
                    "Urban Pop:  "+getUrbanPop()+"\n" +
                    "Rape:  "+getRape()+"\n" ;

        }
    }

}