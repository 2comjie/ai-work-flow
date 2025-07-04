package com.aiworkflow.repository;

import com.aiworkflow.entity.TaskInstance;
import com.aiworkflow.common.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务实例Repository
 * 
 * @author AI Workflow Team
 */
@Repository
public interface TaskInstanceRepository extends JpaRepository<TaskInstance, String> {

    /**
     * 根据流程实例ID查找任务列表
     * 
     * @param instanceId 流程实例ID
     * @return 任务列表
     */
    List<TaskInstance> findByInstanceId(String instanceId);

    /**
     * 根据状态查找任务列表
     * 
     * @param status 任务状态
     * @return 任务列表
     */
    List<TaskInstance> findByStatus(TaskStatus status);

    /**
     * 根据Agent ID查找任务列表
     * 
     * @param agentId Agent ID
     * @return 任务列表
     */
    List<TaskInstance> findByAgentId(String agentId);

    /**
     * 查找超时的任务
     * 
     * @param timeout 超时时间
     * @return 超时任务列表
     */
    @Query("SELECT t FROM TaskInstance t WHERE t.status = :status AND t.startTime < :timeout")
    List<TaskInstance> findTimeoutTasks(@Param("status") TaskStatus status, @Param("timeout") LocalDateTime timeout);

    /**
     * 根据优先级排序查找待执行任务
     * 
     * @param status 任务状态
     * @return 任务列表
     */
    @Query("SELECT t FROM TaskInstance t WHERE t.status = :status ORDER BY t.priority DESC, t.startTime ASC")
    List<TaskInstance> findPendingTasksOrderByPriority(@Param("status") TaskStatus status);

    /**
     * 统计各状态任务数量
     * 
     * @return 状态统计
     */
    @Query("SELECT t.status, COUNT(t) FROM TaskInstance t GROUP BY t.status")
    List<Object[]> countByStatus();
}