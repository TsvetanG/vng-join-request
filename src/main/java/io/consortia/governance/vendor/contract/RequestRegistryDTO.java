package io.consortia.governance.vendor.contract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
 
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class RequestRegistryDTO {
    
    private String requestId;
    
    private String accountVendorServiceProviderId;
    
    private String sourceContractId;


    @JsonCreator
    public static RequestRegistryDTO Create(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        RequestRegistryDTO caseDto = null;
        try {
            caseDto = mapper.readValue(jsonString, RequestRegistryDTO.class);
        } catch (Exception e) {
            return new RequestRegistryDTO();
        }
        return caseDto;
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
 
}
