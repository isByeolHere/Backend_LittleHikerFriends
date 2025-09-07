package com.littlehikerfriends.repository;

import com.littlehikerfriends.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    
    // 특정 사용자의 위치 조회 (업데이트용)
    Optional<Location> findByUserId(Integer userId);
    
    // 특정 사용자의 모든 위치 기록 조회 (최신순)
    List<Location> findByUserIdOrderByUpdatedAtDesc(Integer userId);
    
    // 특정 등산 모임 참여자들의 최신 위치 조회
    @Query("SELECT l FROM Location l WHERE l.user.id IN " +
           "(SELECT hm.user.id FROM HikeMember hm WHERE hm.hike.id = :hikeId)")
    List<Location> findLocationsByHikeId(@Param("hikeId") Integer hikeId);
    
    // 두 지점 간의 거리 계산 (PostGIS 사용)
    @Query(value = "SELECT ST_Distance(ST_SetSRID(ST_MakePoint(:lon1, :lat1), 4326), ST_SetSRID(ST_MakePoint(:lon2, :lat2), 4326))", 
           nativeQuery = true)
    Double calculateDistance(
        @Param("lat1") double lat1, @Param("lon1") double lon1,
        @Param("lat2") double lat2, @Param("lon2") double lon2);
    
    // 특정 사용자의 위치 기록 개수
    long countByUserId(Integer userId);
}
