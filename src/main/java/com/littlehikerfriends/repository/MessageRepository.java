package com.littlehikerfriends.repository;

import com.littlehikerfriends.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    // 특정 등산 모임의 모든 메시지 조회 (최신순)
    List<Message> findByHikeIdOrderByCreatedAtDesc(Integer hikeId);
    
    // 특정 등산 모임의 메시지 페이징 조회
    Page<Message> findByHikeIdOrderByCreatedAtDesc(Integer hikeId, Pageable pageable);
    
    // 특정 사용자가 작성한 모든 메시지 조회
    List<Message> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    // 특정 등산 모임에서 특정 사용자가 작성한 메시지들
    List<Message> findByHikeIdAndUserIdOrderByCreatedAtDesc(Integer hikeId, Integer userId);
    
    // 위치 기반 메시지들만 조회 (location_id가 null이 아닌 것들)
    List<Message> findByHikeIdAndLocationIsNotNullOrderByCreatedAtDesc(Integer hikeId);
    
    // 일반 메시지들만 조회 (location_id가 null인 것들)
    List<Message> findByHikeIdAndLocationIsNullOrderByCreatedAtDesc(Integer hikeId);
    
    // 특정 시간 범위 내의 메시지들 조회
    List<Message> findByHikeIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        Integer hikeId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 메시지 내용으로 검색
    List<Message> findByHikeIdAndContentContainingOrderByCreatedAtDesc(Integer hikeId, String keyword);
    
    // 특정 등산 모임의 메시지 개수
    long countByHikeId(Integer hikeId);
    
    // 특정 사용자가 작성한 메시지 개수
    long countByUserId(Integer userId);
    
    // 위치 기반 메시지 개수
    long countByHikeIdAndLocationIsNotNull(Integer hikeId);
    
    // 최근 N개의 메시지 조회
    List<Message> findTop10ByHikeIdOrderByCreatedAtDesc(Integer hikeId);
}
