package am.developer.client.controller;




import am.developer.client.model.VideoEntity;
import am.developer.client.restclient.VideoRequestClient;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import sun.net.www.http.HttpClient;

/**
 * Created by haykh.
 */
@Controller
@RequestMapping(value = "/videos")
public class VideosController {
    private static final Logger log = LogManager.getLogger();

    @Autowired
    VideoRequestClient videoRequestClient;

    @RequestMapping(method = RequestMethod.GET)
    public String showLoginForm(Model model) {
        //videoRequestClient.getAllVideoRequests();
        getVideos();
        model.addAttribute("videos", "fff");
        return "videos";
    }
    
    
    private static void getVideos()
{
    final String uri = "http://localhost:8080/videos/videolist";
     
    RestTemplate restTemplate = new RestTemplate();
     
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
     
    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
     
    System.out.println(result);
}
    
 /*   
    private void f(){
        List<VideoEntity> list = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://localhost:8080/article/fetch/"+query);
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();

            ObjectMapper mapper = new ObjectMapper();
           
            list = mapper.readValue(EntityUtils.toString(entity),List.class);
       
        } catch (Exception e) {

            e.printStackTrace();

        }
        return list;

    }*/
}
