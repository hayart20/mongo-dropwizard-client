package am.developer.client.restclient;

import am.developer.client.exception.ErrorType;
import am.developer.client.exception.PortalRestClientException;
import am.developer.client.model.VideoEntity;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by haykh.
 */
@Repository
public class VideoRequestClientImpl implements VideoRequestClient {

    protected RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public List<VideoEntity> getAllVideoRequests() {
        final String uri = "http://localhost:8080/videos/videolist";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
        return executeGet(builder.toUriString(), new ParameterizedTypeReference<List<VideoEntity>>() {
                },
                ErrorType.VIDEO_REQUEST_LIST_RETRIEVE_ERROR);
    }
    
    public <T> T executeGet(String uri, ParameterizedTypeReference<T> responseType, ErrorType errorType) {
    	BaseRestClient.RestOperationWrapper<T> wrapper = (rt) -> {
    		URI uri2 = new URI(uri);
    		ResponseEntity<T> response = rt.exchange(uri2, HttpMethod.GET, new HttpEntity<>(prepareHeaders()), responseType);
    		return response.getBody();
    	};
    	return executeRestOperation(wrapper, errorType);
    }
    
    /**
     * Executes provided operation with RestTemplate conveniently translating REST-specific
     * exceptions to locally defined PortalRestClientException with specified error type.
     * 
     * @param operation - wrapped RestTemplate operation, usually in form of lambda
     * @param errorType - value of ErrorType enum to set for PortalRestClientException
     * @return result of REST operation as returned by methods of RestTemplate
     */
    public <T> T executeRestOperation(BaseRestClient.RestOperationWrapper<T> operation, ErrorType errorType) {
    	boolean errorOccurred = false;
    	StringBuilder errMsg = new StringBuilder("REST client error. ");
    	T result = null;
    	try {
    		result = operation.doWithRestTemplate(restTemplate);
    	}
    	catch (HttpServerErrorException | HttpClientErrorException e) {
    		errorOccurred = true;
    		errMsg.append(e.getMessage());
    		errMsg.append(e.getResponseBodyAsString());
        }
        catch (RestClientException e) {
        	errorOccurred = true;
        	errMsg.append(e.getMessage());
        } 
    	catch (URISyntaxException e) {
        	errorOccurred = true;
        	errMsg.append(e.getMessage());
		}
    	
    	if (errorOccurred) {
    		throw new PortalRestClientException(errMsg.toString(), errorType);
    	}
    	
    	return result;
    }
    
    protected HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
/*
    @Override
    public Long addVideoRequest(VideoRequest videoRequest) {
        final String uri = RestURIConstants.REST_API_VIDEO_REQUEST;
        return executePost(uri, videoRequest, Long.class, ErrorType.VIDEO_REQUEST_ADD_ERROR);
    }

    @Override
    public Long editVideoRequest(VideoRequest videoRequest) {
        final String uri = RestURIConstants.REST_API_VIDEO_REQUEST_EDIT;
        return executePost(uri, videoRequest, Long.class, ErrorType.VIDEO_REQUEST_EDIT_ERROR);
    }

    @Override
    public List<VideoRequest> getAllVideoRequests() {
        final String uri = RestURIConstants.REST_API_VIDEO_REQUEST_ALL;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
        return executeGet(builder.toUriString(), new ParameterizedTypeReference<List<VideoRequest>>() {
                },
                ErrorType.VIDEO_REQUEST_LIST_RETRIEVE_ERROR);

    }

    @Override
    public VideoRequest getVideoRequestById(Long id) {
        final String uri = RestURIConstants.REST_API_VIDEO_REQUEST;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
                .pathSegment(Long.toString(id));
        return executeRestOperation(rt -> rt.getForObject(builder.toUriString(), VideoRequest.class),
                ErrorType.VIDEO_REQUEST_RETRIEVE_ERROR);
    }

    @Override
    public List<VideoRequest> getAllVideoRequestsByOpenStatus(Boolean isOpened) {
        final String uri = RestURIConstants.REST_API_VIDEO_REQUEST_NOT_CLOSED;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam("isOpened", isOpened);
        return executeGet(builder.toUriString(), new ParameterizedTypeReference<List<VideoRequest>>() {
                },
                ErrorType.VIDEO_NOT_CLOSED_REQUEST_RETRIEVE_ERROR);
    }

    @Override
    public List<VideoRequest> getAllVideoRequestsByDeletedStatus(Boolean isDeleted) {
        final String uri = RestURIConstants.REST_API_VIDEO_REQUEST_NOT_DELETED;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam("isDeleted", Boolean.toString(isDeleted));
        return executeGet(builder.toUriString(),
                new ParameterizedTypeReference<List<VideoRequest>>() {
                },
                ErrorType.VIDEO_NOT_DELETED_REQUEST_RETRIEVE_ERROR);
    }

    @Override
    public Long replyToVideoRequest(VideoRequestReply videoRequestReply) {
        final String uri = RestURIConstants.REST_API_VIDEO_REQUEST_REPLY;
        return executePost(uri, videoRequestReply, Long.class, ErrorType.VIDEO_REQUEST_REPLY_ERROR);
    }*/
}
