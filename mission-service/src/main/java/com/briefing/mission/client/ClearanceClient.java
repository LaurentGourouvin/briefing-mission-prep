package com.briefing.mission.client;

import com.briefing.mission.client.clearanceDto.ClearanceRequest;
import com.briefing.mission.client.clearanceDto.ClearanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name = "clearance", url = "${clients.clearance.url}")
public interface ClearanceClient {
     @PostMapping("/clearances")
     ClearanceResponse askClearance(ClearanceRequest req);
}
