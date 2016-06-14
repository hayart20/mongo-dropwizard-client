package am.developer.client.restclient;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import am.developer.client.exception.ErrorType;
import am.developer.client.exception.PortalRestClientException;

/**
 * Base class for REST client code. Contains convenience and utility methods 
 * for performing REST operations.
 */
public class BaseRestClient {
	private static final Logger log = LogManager.getLogger();
	protected RestTemplate restTemplate = new RestTemplate();
	
	public BaseRestClient() {
		//restTemplate.setInterceptors(Arrays.asList(new ClientRequestInterceptor()));
	}
	
    /**
     * Executes provided operation with RestTemplate conveniently translating REST-specific
     * exceptions to locally defined exceptions.
     * 
     * @param operation - wrapped RestTemplate operation, usually in form of lambda
     * @param exClass - class of exception to be thrown on any REST error. 
     * 					Description of REST error will be provided in exception's message string.
     * @return result of REST operation as returned by methods of RestTemplate
     */
	public <T> T executeRestOperation(RestOperationWrapper<T> operation, Class<? extends Exception> exClass) {
    	boolean errorOccured = false;
    	StringBuilder errMsg = new StringBuilder("REST client error. ");
    	T result = null;
    	try {
    		result = operation.doWithRestTemplate(restTemplate);
    	}
    	catch (HttpClientErrorException e) {
    		errorOccured = true;
    		errMsg.append(e.getResponseBodyAsString());
        }
        catch (RestClientException e) {
        	errorOccured = true;
        	errMsg.append(e.getMessage());
        } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	if (errorOccured) {
    		try {
				Constructor<? extends Exception> constructor = exClass.getDeclaredConstructor(String.class);
				Exception ex = constructor.newInstance(errMsg.toString());
				throw ex;
			} catch (Exception e) {
				throw new RuntimeException("Error instantiating exception class.", e);
			}
    	}
    	
    	return result;
    }
    
    /**
     * Executes provided operation with RestTemplate conveniently translating REST-specific
     * exceptions to locally defined PortalRestClientException with specified error type.
     * 
     * @param operation - wrapped RestTemplate operation, usually in form of lambda
     * @param errorType - value of ErrorType enum to set for PortalRestClientException
     * @return result of REST operation as returned by methods of RestTemplate
     */
    public <T> T executeRestOperation(RestOperationWrapper<T> operation, ErrorType errorType) {
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
    
    /**
     * Executes provided operation with RestTemplate conveniently translating REST-specific
     * exceptions to locally defined PortalRestClientException with specified error type.
     * This version of method is for REST operations that does not return any result.
     * 
     * @param operation
     * @param errorType
     */
    public void executeVoidRestOperation(VoidRestOperationWrapper operation, ErrorType errorType) {
    	RestOperationWrapper<Void> row = new RestOperationWrapper<Void>() {
			@Override
			public Void doWithRestTemplate(RestTemplate rt) {
				operation.doWithRestTemplate(rt);
				return null;
			}
		};
    	executeRestOperation(row, errorType);
    }
    
    /**
     * Interface for wrapping operations using RestTemplate as lambdas
     * and pass to executeRestOperation methods.
     * 
     * @param <T> - class of returned object 
     */
    @FunctionalInterface
    public static interface RestOperationWrapper<T> {
    	T doWithRestTemplate(RestTemplate rt) throws URISyntaxException;
    }

    /**
     * Interface for wrapping operations using RestTemplate as lambdas
     * and pass to executeRestOperation methods.
     */
    @FunctionalInterface
    public static interface VoidRestOperationWrapper {
    	void doWithRestTemplate(RestTemplate rt);
    }
    
    public boolean isError(HttpStatus status) {
        HttpStatus.Series series = status.series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series)
                || HttpStatus.Series.SERVER_ERROR.equals(series));
    }

    /**
     * Convenience method to execute POST requests with properly set Accept and ContentType
     * headers for JSON encoding. 
     * @param uri
     * @param resource
     * @param responseType
     * @param errorType
     * @return
     */
    public <T> T executePost(String uri, Object resource, Class<T> responseType, ErrorType errorType) {
        HttpEntity<?> entity = new HttpEntity<>(resource, prepareHeaders());
        return executeRestOperation(rt -> rt.postForObject(uri, entity, responseType), errorType);
    }

	public <T> T executePost(String uri, ParameterizedTypeReference<T> responseType, ErrorType errorType) {
		RestOperationWrapper<T> wrapper = (rt) -> {
			URI uri2 = new URI(uri);
			ResponseEntity<T> response = rt.exchange(uri2, HttpMethod.POST, new HttpEntity<>(prepareHeaders()), responseType);
			return response.getBody();
		};
		return executeRestOperation(wrapper, errorType);
	}



    
    public <T> T executeGet(String uri, Class<T> responseType, ErrorType errorType) {
    	RestOperationWrapper<T> wrapper = (rt) -> {
    		ResponseEntity<T> response = rt.exchange(uri, HttpMethod.GET, new HttpEntity<>(prepareHeaders()), responseType);
    		return response.getBody();
    	};
    	return executeRestOperation(wrapper, errorType);
    }

    public <T> T executeGet(String uri, ParameterizedTypeReference<T> responseType, ErrorType errorType) {
    	RestOperationWrapper<T> wrapper = (rt) -> {
    		URI uri2 = new URI(uri);
    		ResponseEntity<T> response = rt.exchange(uri2, HttpMethod.GET, new HttpEntity<>(prepareHeaders()), responseType);
    		return response.getBody();
    	};
    	return executeRestOperation(wrapper, errorType);
    }
    
    protected HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    
    protected static class ClientRequestInterceptor implements ClientHttpRequestInterceptor {
    	@Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, 
        		ClientHttpRequestExecution execution) throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            log(request, body, response);
            return response;
        }

        private void log(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
            log.debug(request.getHeaders());
            log.debug(() -> new String(body));
        }
    }
}
