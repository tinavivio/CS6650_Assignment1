package Server;

import Server.DAO.MetricsDAO;
import Server.Model.Metrics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;


public class ScheduledTask {
    
    @Autowired
    private MetricsDAO metricsDAO;
    
    private static final String QUEUEURL = "https://sqs.us-west-2.amazonaws.com/689430559734/DistributedSystems";
    
    @Scheduled(fixedRate = 50)
    public void receiveAndPersistMetrics(){
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        ReceiveMessageRequest receive_request = new ReceiveMessageRequest()
            .withQueueUrl(QUEUEURL)
            .withMaxNumberOfMessages(10);
        List<Message> messages = sqs.receiveMessage(receive_request).getMessages();
        List<Metrics> metrics = new ArrayList<>();
        if (!messages.isEmpty()) {
            for (Message m : messages) {
                String message = m.getBody();
                String[] messageTokenized = message.split(" ");
                metrics.add(new Metrics(messageTokenized[0], Long.parseLong(messageTokenized[1]), Long.parseLong(messageTokenized[2]), Long.parseLong(messageTokenized[3]), Long.parseLong(messageTokenized[4]), Long.parseLong(messageTokenized[5])));
                sqs.deleteMessage(QUEUEURL, m.getReceiptHandle());
            }
            this.metricsDAO.insertNewBatchOfMetrics(metrics);
        }
    }
    
}
