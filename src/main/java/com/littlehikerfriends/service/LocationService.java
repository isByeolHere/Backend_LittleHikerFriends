package com.littlehikerfriends.service;

import com.littlehikerfriends.dto.LocationRequest;
import com.littlehikerfriends.dto.LocationResponse;
import com.littlehikerfriends.entity.Location;
import com.littlehikerfriends.entity.User;
import com.littlehikerfriends.repository.LocationRepository;
import com.littlehikerfriends.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationService {
    
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final GeometryFactory geometryFactory;
    
    @Autowired
    public LocationService(LocationRepository locationRepository, UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.geometryFactory = new GeometryFactory();
    }
    
    /**
     * 사용자 위치 업데이트
     * iOS에서 위도, 경도, 고도만 전송하면 서버에서 PostGIS Point 생성
     */
    public LocationResponse updateLocation(Integer userId, LocationRequest request) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        // 2. 기존 위치 조회 (있으면 업데이트, 없으면 생성)
        Location location = locationRepository.findByUserId(userId)
            .orElse(new Location());
        
        // 3. 위치 정보 설정
        location.setUser(user);
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setAltitude(request.getAltitude());
        location.setUpdatedAt(LocalDateTime.now());
        
        // 4. PostGIS Point 생성 (SRID 4326 = WGS84)
        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        point.setSRID(4326);
        location.setGeom(point);
        
        // 5. 저장
        Location savedLocation = locationRepository.save(location);
        
        // 6. 응답 생성
        return new LocationResponse(
            savedLocation.getId(),
            savedLocation.getUser().getId(),
            savedLocation.getUser().getNickname(),
            savedLocation.getLatitude(),
            savedLocation.getLongitude(),
            savedLocation.getAltitude(),
            savedLocation.getUpdatedAt()
        );
    }
    
    /**
     * 등산 모임 멤버들의 위치 조회
     */
    @Transactional(readOnly = true)
    public List<LocationResponse> getHikeMembersLocations(Integer hikeId) {
        // 1. 등산 모임 멤버들의 위치 조회
        List<Location> locations = locationRepository.findLocationsByHikeId(hikeId);
        
        // 2. LocationResponse로 변환
        return locations.stream()
            .map(location -> new LocationResponse(
                location.getId(),
                location.getUser().getId(),
                location.getUser().getNickname(),
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude(),
                location.getUpdatedAt()
            ))
            .collect(Collectors.toList());
    }
}
