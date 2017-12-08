package Client;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkApp {
    
    public static void main(String[] args) {
        
        String url = "jdbc:postgresql://skierdbinstance.cmt5itoksgaz.us-west-2.rds.amazonaws.com/skierdb";
        SparkSession spark = SparkSession.builder().appName("Simple Application").getOrCreate();
        Dataset<Row> rides = spark
                .read()
                .format("jdbc")
                .option("url", url)
                .option("dbtable", "rides")
                .option("driver", "org.postgresql.Driver")
                .option("user", "tinavivio")
                .option("password", "rahul2016")
                .load();
        
        Dataset<Row> lifts = spark
                .read()
                .format("jdbc")
                .option("url", url)
                .option("dbtable", "lifts")
                .option("driver", "org.postgresql.Driver")
                .option("user", "tinavivio")
                .option("password", "rahul2016")
                .load();
        
        Dataset<Row> countsByLiftNumberAndDayNumber = rides.groupBy("liftNumber", "dayNumber").count();
        Dataset<Row> mostPopularLiftByDay = countsByLiftNumberAndDayNumber.groupBy("dayNumber").agg(org.apache.spark.sql.functions.max("count"));
        Dataset<Row> countsBySkierIdAndDayNumberAndLiftNumber = rides.groupBy("skierId", "dayNumber", "liftNumber").count();
        Dataset<Row> totalHeightBySkierIdAndDayNumberAndLiftNumber = countsBySkierIdAndDayNumberAndLiftNumber
                .join(lifts)
                .where(countsBySkierIdAndDayNumberAndLiftNumber.col("liftNumber").equalTo(lifts.col("liftNumber")))
                .select(countsBySkierIdAndDayNumberAndLiftNumber.col("skierId"), 
                        countsBySkierIdAndDayNumberAndLiftNumber.col("dayNumber"), 
                        countsBySkierIdAndDayNumberAndLiftNumber.col("count").multiply(lifts.col("height")).alias("totalHeightForDayAndLift"));
        Dataset<Row> totalHeightBySkierIdAndDayNumber = totalHeightBySkierIdAndDayNumberAndLiftNumber.groupBy("skierId", "dayNumber").agg(org.apache.spark.sql.functions.sum("totalHeightForDayAndLift").alias("totalHeightForDay"));
        Dataset<Row> mostProlificSkierByDay = totalHeightBySkierIdAndDayNumber.groupBy("dayNumber").agg(org.apache.spark.sql.functions.max("totalHeightForDay"));
        
        countsByLiftNumberAndDayNumber.coalesce(1).write().format("json").save("./results");
        mostPopularLiftByDay.coalesce(1).write().format("json").save("./results-2");
        countsBySkierIdAndDayNumberAndLiftNumber.coalesce(1).write().format("json").save("./results-3");
        totalHeightBySkierIdAndDayNumberAndLiftNumber.coalesce(1).write().format("json").save("./results-4");
        totalHeightBySkierIdAndDayNumber.coalesce(1).write().format("json").save("./results-5");
        mostProlificSkierByDay.coalesce(1).write().format("json").save("./results-6");
        
        spark.stop();
        
    }

}
