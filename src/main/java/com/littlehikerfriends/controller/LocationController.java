package com.littlehikerfriends.controller;

import com.littlehikerfriends.dto.LocationRequest;
import com.littlehikerfriends.dto.LocationResponse;
import com.littlehikerfriends.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@Tag(name = "Location", description = "위치 관리 API")
public class LocationController {
    
    private final LocationService locationService;
    
    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }
    
    @PostMapping
    @Operation(summary = "위치 업데이트", description = "사용자의 현재 위치를 업데이트합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "위치 업데이트 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<LocationResponse> updateLocation(
            @RequestBody @Valid LocationRequest request,
            Authentication authentication) {
        
        try {
            // JWT에서 사용자 ID 추출 (임시로 1 사용, 실제로는 JWT에서 추출)
            Integer userId = 1; // TODO: JWT에서 사용자 ID 추출
            
            LocationResponse response = locationService.updateLocation(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/hikes/{hikeId}/members")
    @Operation(summary = "등산 모임 멤버들의 위치 조회", description = "특정 등산 모임에 참여한 멤버들의 최신 위치를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "위치 조회 성공"),
        @ApiResponse(responseCode = "404", description = "등산 모임을 찾을 수 없음")
    })
    public ResponseEntity<List<LocationResponse>> getHikeMembersLocations(
            @Parameter(description = "등산 모임 ID", example = "1")
            @PathVariable Integer hikeId) {
        
        try {
            List<LocationResponse> locations = locationService.getHikeMembersLocations(hikeId);
            return ResponseEntity.ok(locations);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
