package Client;

import java.util.Properties;
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
        
        Dataset<Row> countsByLiftNumberAndDayNumber = rides
                .groupBy("liftNumber", "dayNumber")
                .count();
        Dataset<Row> maxCountByDay = countsByLiftNumberAndDayNumber
                .groupBy("dayNumber")
                .agg(org.apache.spark.sql.functions.max("count"));
        Dataset<Row> mostPopularLiftsByDay = countsByLiftNumberAndDayNumber
                .join(maxCountByDay, "dayNumber")
                .where(maxCountByDay.col("max(count)")
                        .equalTo(countsByLiftNumberAndDayNumber.col("count")))
                .select(countsByLiftNumberAndDayNumber.col("dayNumber"), 
                        countsByLiftNumberAndDayNumber.col("liftNumber"), 
                        countsByLiftNumberAndDayNumber.col("count"));
        Dataset<Row> countsBySkierIdAndDayNumberAndLiftNumber = rides
                .groupBy("skierId", "dayNumber", "liftNumber")
                .count();
        Dataset<Row> totalHeightBySkierIdAndDayNumberAndLiftNumber = countsBySkierIdAndDayNumberAndLiftNumber
                .join(lifts)
                .where(countsBySkierIdAndDayNumberAndLiftNumber.col("liftNumber")
                        .equalTo(lifts.col("liftNumber")))
                .select(countsBySkierIdAndDayNumberAndLiftNumber.col("skierId"), 
                        countsBySkierIdAndDayNumberAndLiftNumber.col("dayNumber"), 
                        countsBySkierIdAndDayNumberAndLiftNumber.col("count")
                                .multiply(lifts.col("height"))
                                .alias("totalHeightForDayAndLift"));
        Dataset<Row> totalHeightBySkierIdAndDayNumber = totalHeightBySkierIdAndDayNumberAndLiftNumber
                .groupBy("skierId", "dayNumber")
                .agg(org.apache.spark.sql.functions.sum("totalHeightForDayAndLift")
                        .alias("totalHeightForDay"));
        Dataset<Row> maxHeightByDay = totalHeightBySkierIdAndDayNumber
                .groupBy("dayNumber")
                .agg(org.apache.spark.sql.functions.max("totalHeightForDay"));
        Dataset<Row> mostProlificSkiersByDay = totalHeightBySkierIdAndDayNumber
                .join(maxHeightByDay, "dayNumber")
                .where(maxHeightByDay.col("max(totalHeightForDay)")
                        .equalTo(totalHeightBySkierIdAndDayNumber.col("totalHeightForDay")))
                .select(totalHeightBySkierIdAndDayNumber.col("dayNumber"), 
                        totalHeightBySkierIdAndDayNumber.col("skierId"), 
                        totalHeightBySkierIdAndDayNumber.col("totalHeightForDay"));
        
        /*countsByLiftNumberAndDayNumber.coalesce(1).write().format("json").save("./results");
        maxCountByDay.coalesce(1).write().format("json").save("./results-2");
        mostPopularLiftsByDay.coalesce(1).write().format("json").save("./results-3");
        countsBySkierIdAndDayNumberAndLiftNumber.coalesce(1).write().format("json").save("./results-4");
        totalHeightBySkierIdAndDayNumberAndLiftNumber.coalesce(1).write().format("json").save("./results-5");
        totalHeightBySkierIdAndDayNumber.coalesce(1).write().format("json").save("./results-6");
        maxHeightByDay.coalesce(1).write().format("json").save("./results-7");
        mostProlificSkiersByDay.coalesce(1).write().format("json").save("./results-8");*/
        
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", "tinavivio");
        connectionProperties.put("password", "rahul2016");
        connectionProperties.put("driver", "org.postgresql.Driver");
        mostPopularLiftsByDay.coalesce(1).write().jdbc(url, "mostpopularlifts", connectionProperties);
        mostProlificSkiersByDay.coalesce(1).write().jdbc(url, "mostprolificskiers", connectionProperties);
        
        spark.stop();
        
    }

}