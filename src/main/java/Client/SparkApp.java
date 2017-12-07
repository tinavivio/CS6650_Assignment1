package Client;

import java.util.Properties;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions.*;

public class SparkApp {
    
    public static void main(String[] args) {
        
        /*String logFile = "../../spark-2.2.0-bin-hadoop2.7/README.md"; // Should be some file on your system
        SparkSession spark = SparkSession.builder().appName("Simple Application").getOrCreate();
        Dataset<String> logData = spark.read().textFile(logFile).cache();

        long numAs = logData.filter(s -> s.contains("a")).count();
        long numBs = logData.filter(s -> s.contains("b")).count();

        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);

        spark.stop();*/
        
        String url = "jdbc:postgresql://skierdbinstance.cmt5itoksgaz.us-west-2.rds.amazonaws.com/skierdb";
        SparkSession spark = SparkSession.builder().appName("Simple Application").getOrCreate();
        Dataset<Row> df = spark
                .read()
                .format("jdbc")
                .option("url", url)
                .option("dbtable", "rides")
                .option("driver", "org.postgresql.Driver")
                .option("user", "tinavivio")
                .option("password", "rahul2016")
                .load();
        
        df.printSchema();
        
        Dataset<Row> countsByLiftId = df.groupBy("liftNumber", "dayNumber").count();
        Dataset<Row> mostPopularLift = countsByLiftId.agg(org.apache.spark.sql.functions.max(countsByLiftId.col("count")));
        countsByLiftId.show();
        mostPopularLift.show();
        
        countsByLiftId.coalesce(1).write().format("json").save("./results");
        mostPopularLift.write().format("json").save("./results-2");
        
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", "tinavivio");
        connectionProperties.put("password", "rahul2016");
        connectionProperties.put("driver", "org.postgresql.Driver");
        mostPopularLift.coalesce(1).write().jdbc(url, "results", connectionProperties);
        
        spark.stop();
        
    }

}
