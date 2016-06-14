package am.developer.client.restclient;

import am.developer.client.model.VideoEntity;
import java.util.List;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * Created by haykh.
 */
public interface VideoRequestClient {

    List<VideoEntity> getAllVideoRequests();

}
