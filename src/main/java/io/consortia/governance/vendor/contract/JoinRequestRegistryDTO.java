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
public class JoinRequestRegistryDTO {
    
    private String joinRequestId;
    
    private String joiningAccountVendorServiceProvider;
    
    private String sourceContractId;


    @JsonCreator
    public static JoinRequestRegistryDTO Create(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        JoinRequestRegistryDTO caseDto = null;
        try {
            caseDto = mapper.readValue(jsonString, JoinRequestRegistryDTO.class);
        } catch (Exception e) {
            return new JoinRequestRegistryDTO();
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
