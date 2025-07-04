package com.aiworkflow.repository;

import com.aiworkflow.entity.ProcessInstance;
import com.aiworkflow.common.enums.ProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程实例Repository
 * 
 * @author AI Workflow Team
 */
@Repository
public interface ProcessInstanceRepository extends JpaRepository<ProcessInstance, String> {

    /**
     * 根据流程定义ID查找流程实例列表
     * 
     * @param processId 流程定义ID
     * @return 流程实例列表
     */
    List<ProcessInstance> findByProcessId(String processId);

    /**
     * 根据业务键查找流程实例
     * 
     * @param businessKey 业务键
     * @return 流程实例列表
     */
    List<ProcessInstance> findByBusinessKey(String businessKey);

    /**
     * 根据状态查找流程实例列表
     * 
     * @param status 流程状态
     * @return 流程实例列表
     */
    List<ProcessInstance> findByStatus(ProcessStatus status);

    /**
     * 根据流程定义ID和状态查找流程实例列表
     * 
     * @param processId 流程定义ID
     * @param status 流程状态
     * @return 流程实例列表
     */
    List<ProcessInstance> findByProcessIdAndStatus(String processId, ProcessStatus status);

    /**
     * 查找超时的流程实例
     * 
     * @param timeoutBefore 超时时间点
     * @param status 流程状态
     * @return 超时流程实例列表
     */
    @Query("SELECT p FROM ProcessInstance p WHERE p.startTime < :timeoutBefore AND p.status = :status AND p.endTime IS NULL")
    List<ProcessInstance> findTimeoutInstances(@Param("timeoutBefore") LocalDateTime timeoutBefore, 
                                              @Param("status") ProcessStatus status);

    /**
     * 根据时间范围查找流程实例
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 流程实例列表
     */
    @Query("SELECT p FROM ProcessInstance p WHERE p.startTime BETWEEN :startTime AND :endTime")
    List<ProcessInstance> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各状态流程实例数量
     * 
     * @return 状态统计
     */
    @Query("SELECT p.status, COUNT(p) FROM ProcessInstance p GROUP BY p.status")
    List<Object[]> countByStatus();

    /**
     * 查找活跃的流程实例
     * 
     * @return 活跃流程实例列表
     */
    @Query("SELECT p FROM ProcessInstance p WHERE p.status = 'ACTIVE' ORDER BY p.startTime DESC")
    List<ProcessInstance> findActiveInstances();
}