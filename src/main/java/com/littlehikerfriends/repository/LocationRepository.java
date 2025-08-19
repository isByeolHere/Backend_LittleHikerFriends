package com.littlehikerfriends.repository;

import com.littlehikerfriends.entity.Location;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    
    // 특정 사용자의 모든 위치 기록 조회 (최신순)
    List<Location> findByUserIdOrderByTimestampDesc(Integer userId);
    
    // 특정 사용자의 최신 위치 조회
    Optional<Location> findTopByUserIdOrderByTimestampDesc(Integer userId);
    
    // 특정 시간 범위 내의 위치 기록 조회
    List<Location> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    // 특정 사용자의 특정 시간 범위 내 위치 기록 조회
    List<Location> findByUserIdAndTimestampBetweenOrderByTimestampDesc(
        Integer userId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 특정 지점으로부터 일정 거리 내의 위치들 조회 (미터 단위)
    @Query(value = "SELECT * FROM locations l WHERE ST_DWithin(l.geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :distanceMeters)", 
           nativeQuery = true)
    List<Location> findLocationsWithinDistance(
        @Param("latitude") double latitude, 
        @Param("longitude") double longitude, 
        @Param("distanceMeters") double distanceMeters);
    
    // 특정 등산 모임 참여자들의 최신 위치 조회
    @Query("SELECT l FROM Location l WHERE l.user.id IN " +
           "(SELECT hm.user.id FROM HikeMember hm WHERE hm.hike.id = :hikeId) " +
           "AND l.id IN (SELECT MAX(l2.id) FROM Location l2 WHERE l2.user.id = l.user.id GROUP BY l2.user.id)")
    List<Location> findLatestLocationsByHikeMembers(@Param("hikeId") Integer hikeId);
    
    // 두 지점 간의 거리 계산 (미터 단위)
    @Query(value = "SELECT ST_Distance(ST_SetSRID(ST_MakePoint(:lon1, :lat1), 4326), ST_SetSRID(ST_MakePoint(:lon2, :lat2), 4326))", 
           nativeQuery = true)
    Double calculateDistance(
        @Param("lat1") double lat1, @Param("lon1") double lon1,
        @Param("lat2") double lat2, @Param("lon2") double lon2);
    
    // 특정 사용자의 위치 기록 개수
    long countByUserId(Integer userId);
    
    // 특정 시간 이후의 위치 기록들 조회
    List<Location> findByTimestampAfter(LocalDateTime timestamp);
    
    // 특정 고도 범위 내의 위치들 조회
    List<Location> findByAltitudeBetween(Double minAltitude, Double maxAltitude);
}
