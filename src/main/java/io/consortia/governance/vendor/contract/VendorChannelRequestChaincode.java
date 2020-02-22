
package io.consortia.governance.vendor.contract;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ResponseUtils;

public class VendorChannelRequestChaincode extends ChaincodeBase {

    private static Log _logger = LogFactory.getLog(VendorChannelRequestChaincode.class);
 
    @Override
    public Response init(ChaincodeStub stub) {
        return ResponseUtils.newSuccessResponse();
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            _logger.info("Invoke request chaincode");
            String func = stub.getFunction();
            List<String> params = stub.getParameters();
            if (func.equals("create")) {
                return create(stub, params);
            }
            if (func.equals("delete")) {
                return delete(stub, params);
            }
            if (func.equals("query")) {
                return query(stub, params);
            }
            return ResponseUtils.newErrorResponse(
                    "Invalid invoke function name. Expecting one of: [\"create\", \"delete\", \"query\"]");
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e);
        }
    }

    /**
     * 
     * @param stub
     * @param params
     * @return
     */
    protected Response create(ChaincodeStub stub, List<String> params) {
        String requestAsString = new String(params.get(0));
        _logger.debug(requestAsString);

        RequestRegistryDTO requestDTO = RequestRegistryDTO.Create(requestAsString);

        RequestRegistryDTO foundRequest = fetchRequestById(stub, Arrays.asList(requestDTO.getRequestId()));
        if (foundRequest != null) {
            return ResponseUtils.newErrorResponse(
                    "Request already exists with the same ID: " + requestDTO.getRequestId());
        }
        stub.putStringState(requestDTO.getRequestId(), requestDTO.toJson());

        return ResponseUtils.newSuccessResponse();

    }

    /**
     * 
     * @param stub
     * @param params
     * @return
     */
    private Response query(ChaincodeStub stub, List<String> params) {
        RequestRegistryDTO requestDTO = fetchRequestById(stub, params);
        if (requestDTO == null) {
            return ResponseUtils.newErrorResponse("Request doesn't exists with ID: " + params.get(0));
        }
        try {
            return ResponseUtils.newSuccessResponse(requestDTO.toJson().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return ResponseUtils.newErrorResponse(e);
        }

    }

    /**
     * 
     * @param stub
     * @param params
     * @return
     */
    protected RequestRegistryDTO fetchRequestById(ChaincodeStub stub, List<String> params) {
        String requestId = params.get(0);
        String requestAsString = stub.getStringState(requestId);

        _logger.debug(requestAsString);

        if (requestAsString == null || requestAsString.equals("")) {
            return null;
        }

        RequestRegistryDTO requestDTO = RequestRegistryDTO.Create(requestAsString);
        return requestDTO;
    }

    /**
     * 
     * @param stub
     * @param params
     * @return
     */
    private Response delete(ChaincodeStub stub, List<String> params) {
        if (params.size() != 1) {
            return ResponseUtils.newErrorResponse("Incorrect number of arguments. Expecting 1");
        }
        RequestRegistryDTO requestDTO = fetchRequestById(stub, params);
        if (requestDTO == null) {
            return ResponseUtils.newErrorResponse("Request doesn't exists with ID: " + params.get(0));
        }

        String requestId = params.get(0);
        _logger.debug("Delete request with id: " + requestId);

        stub.delState(requestId);

        return ResponseUtils.newSuccessResponse();
    }

    public static void main(String[] args) {
        new VendorChannelRequestChaincode().start(args);
    }

}