
package io.consortia.governance.vendor.contract;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ResponseUtils;

public class VendorChannelJoinRequestChaincode extends ChaincodeBase {

    private static Log _logger = LogFactory.getLog(VendorChannelJoinRequestChaincode.class);
 
    @Override
    public Response init(ChaincodeStub stub) {
        return ResponseUtils.newSuccessResponse();
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            _logger.info("Invoke join request chaincode");
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
        String joinRequestAsString = new String(params.get(0));
        _logger.debug(joinRequestAsString);

        JoinRequestRegistryDTO joinRequestDTO = JoinRequestRegistryDTO.Create(joinRequestAsString);

        JoinRequestRegistryDTO foundRequest = fetchRequestById(stub, Arrays.asList(joinRequestDTO.getJoinRequestId()));
        if (foundRequest != null) {
            ResponseUtils.newErrorResponse(
                    "Join Request already exists with the same ID: " + joinRequestDTO.getJoinRequestId());
        }
        stub.putStringState(joinRequestDTO.getJoinRequestId(), joinRequestDTO.toJson());

        return ResponseUtils.newSuccessResponse();

    }

    /**
     * 
     * @param stub
     * @param params
     * @return
     */
    private Response query(ChaincodeStub stub, List<String> params) {
        JoinRequestRegistryDTO joinRequestDTO = fetchRequestById(stub, params);
        if (joinRequestDTO == null) {
            ResponseUtils.newErrorResponse("Join Request doesn't exists with ID: " + params.get(0));
        }
        try {
            return ResponseUtils.newSuccessResponse(joinRequestDTO.toJson().getBytes("UTF-8"));
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
    protected JoinRequestRegistryDTO fetchRequestById(ChaincodeStub stub, List<String> params) {
        String joinRequestId = params.get(0);
        String joinRequestAsString = stub.getStringState(joinRequestId);

        _logger.debug(joinRequestAsString);

        if (joinRequestAsString == null || joinRequestAsString.equals("")) {
            return null;
        }

        JoinRequestRegistryDTO joinRequestDTO = JoinRequestRegistryDTO.Create(joinRequestAsString);
        return joinRequestDTO;
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
        JoinRequestRegistryDTO joinRequestDTO = fetchRequestById(stub, params);
        if (joinRequestDTO == null) {
            ResponseUtils.newErrorResponse("Join Request doesn't exists with ID: " + params.get(0));
        }

        String joinRequestId = params.get(0);
        _logger.debug("Delete join request with id: " + joinRequestId);

        stub.delState(joinRequestId);

        return ResponseUtils.newSuccessResponse();
    }

    public static void main(String[] args) {
        new VendorChannelJoinRequestChaincode().start(args);
    }

}