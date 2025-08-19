package com.littlehikerfriends.controller;

import com.littlehikerfriends.dto.LocationRequest;
import com.littlehikerfriends.dto.LocationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
@Tag(name = "Location", description = "위치 관리 API")
public class LocationController {
    
    @PostMapping
    @Operation(summary = "위치 업데이트", description = "사용자의 현재 위치를 업데이트합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "위치 업데이트 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<LocationResponse> updateLocation(
            @RequestBody @Valid LocationRequest request) {
        
        // TODO: 실제 구현 필요
        LocationResponse response = new LocationResponse(
            1, 123, "등산러버", 
            request.getLatitude(), request.getLongitude(), request.getAltitude(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
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
        
        // TODO: 실제 구현 필요
        List<LocationResponse> locations = List.of(
            new LocationResponse(1, 123, "등산러버", 37.5665, 126.9780, 123.5, LocalDateTime.now()),
            new LocationResponse(2, 124, "산악인", 37.5670, 126.9785, 125.0, LocalDateTime.now())
        );
        
        return ResponseEntity.ok(locations);
    }
    
    @GetMapping("/nearby")
    @Operation(summary = "주변 위치 조회", description = "특정 지점 주변의 위치들을 조회합니다.")
    public ResponseEntity<List<LocationResponse>> getNearbyLocations(
            @Parameter(description = "위도", example = "37.5665")
            @RequestParam Double latitude,
            @Parameter(description = "경도", example = "126.9780") 
            @RequestParam Double longitude,
            @Parameter(description = "검색 반경 (미터)", example = "1000")
            @RequestParam(defaultValue = "1000") Double radiusMeters) {
        
        // TODO: 실제 구현 필요
        return ResponseEntity.ok(List.of());
    }
}
