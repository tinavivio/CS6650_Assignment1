package Client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.UniformInterfaceException;
import javax.ws.rs.core.MediaType;

public class JerseyClient {

    private final Client client;
    private final WebResource webResource;

    public JerseyClient(String ipAddress, String portNumber) {
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource("http://" + ipAddress + ":" + portNumber + "/Assignment1_Vivio/webresources").path("myresource");
    }

    public String postText(Object requestEntity) throws UniformInterfaceException {
        return this.webResource.type(MediaType.TEXT_PLAIN).post(String.class, requestEntity);
    }

    public String getIt() throws UniformInterfaceException {
        WebResource resource = this.webResource;
        return resource.accept(MediaType.TEXT_PLAIN).get(String.class);
    }

    public void close() {
        this.client.destroy();
    }

}
