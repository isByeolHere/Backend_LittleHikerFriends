package com.littlehikerfriends.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "위치 정보 응답")
public class LocationResponse {
    
    @Schema(description = "위치 ID", example = "1")
    private Integer id;
    
    @Schema(description = "사용자 ID", example = "123")
    private Integer userId;
    
    @Schema(description = "사용자 닉네임", example = "등산러버")
    private String userNickname;
    
    @Schema(description = "위도", example = "37.5665")
    private Double latitude;
    
    @Schema(description = "경도", example = "126.9780")
    private Double longitude;
    
    @Schema(description = "고도 (미터)", example = "123.5")
    private Double altitude;
    
    @Schema(description = "위치 기록 시간", example = "2025-08-15T10:30:00")
    private LocalDateTime timestamp;
    
    // 기본 생성자
    public LocationResponse() {}
    
    // 생성자
    public LocationResponse(Integer id, Integer userId, String userNickname, 
                           Double latitude, Double longitude, Double altitude, 
                           LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.userNickname = userNickname;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Double getAltitude() { return altitude; }
    public void setAltitude(Double altitude) { this.altitude = altitude; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
