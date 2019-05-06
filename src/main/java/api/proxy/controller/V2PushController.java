package api.proxy.controller;

import api.proxy.service.ApiPath;
import api.proxy.service.ApiResponse;
import api.proxy.service.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;


@RestController
@RequestMapping("/v2/chain")
public class V2PushController {

    private static Logger LOGGER = LoggerFactory.getLogger(ApiService.class);

    private String API_SERVER;
    private String TOKEN;
    private int NUM_RETRY = 1;


    @Autowired
    public void setProperties(Properties properties) {
        API_SERVER = properties.getApiServer();
        TOKEN = properties.getToken();
        NUM_RETRY = properties.getNumRetry();
    }

    @RequestMapping(value = "/push_transaction", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> pushTransaction(@RequestBody JsonNode request) {
        return processRequest(ApiPath.CHAIN_PUSH_TRANSACTION, request);
    }

    @RequestMapping(value = "/push_transactions", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> pushTransactions(@RequestBody JsonNode request) {
        return processRequest(ApiPath.CHAIN_PUSH_TRANSACTIONS, request);
    }

    private ResponseEntity<Object> processRequest(String apiPath, JsonNode request) {
        return processRequest(apiPath, request.asText());
    }

    private ResponseEntity<Object> processRequest(String apiPath, String body) {
        int attempt = 0;
        ApiResponse response = null;
        try {
            while(++attempt <= NUM_RETRY) {
                response = ApiService.call(API_SERVER + apiPath, TOKEN, body);
                if(response.getCode() == 200) {
                    return new ResponseEntity<>(response.getContent(), HttpStatus.OK);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error for API call "+apiPath+" : "+ex.getMessage(), ex);
            return new ResponseEntity<>("{\"error\":\""+ex.getMessage()+"\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // Otherwise return failed response after max attempts:
        if(response != null) {
            LOGGER.error("Failed after max number of attempts["+attempt+"] " + response.toString());
            return new ResponseEntity<>(response.getContent(), HttpStatus.valueOf(response.getCode()));
        } else {
            LOGGER.error("API response object is null - should never happen!");
            return new ResponseEntity<>("{\"error\":\"Null API response!\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
