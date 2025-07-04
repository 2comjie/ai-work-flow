package com.aiworkflow.repository;

import com.aiworkflow.entity.ProcessDefinition;
import com.aiworkflow.common.enums.ProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 流程定义Repository
 * 
 * @author AI Workflow Team
 */
@Repository
public interface ProcessDefinitionRepository extends JpaRepository<ProcessDefinition, String> {

    /**
     * 根据流程键查找流程定义
     * 
     * @param processKey 流程键
     * @return 流程定义
     */
    ProcessDefinition findByProcessKey(String processKey);

    /**
     * 根据状态查找流程定义列表
     * 
     * @param status 流程状态
     * @return 流程定义列表
     */
    List<ProcessDefinition> findByStatus(ProcessStatus status);

    /**
     * 根据流程键和状态查找流程定义
     * 
     * @param processKey 流程键
     * @param status 流程状态
     * @return 流程定义
     */
    ProcessDefinition findByProcessKeyAndStatus(String processKey, ProcessStatus status);

    /**
     * 查找最新版本的活跃流程定义
     * 
     * @param processKey 流程键
     * @return 流程定义
     */
    @Query("SELECT p FROM ProcessDefinition p WHERE p.processKey = :processKey AND p.status = 'ACTIVE' ORDER BY p.version DESC")
    ProcessDefinition findLatestActiveByProcessKey(@Param("processKey") String processKey);

    /**
     * 根据创建者查找流程定义列表
     * 
     * @param createdBy 创建者
     * @return 流程定义列表
     */
    List<ProcessDefinition> findByCreatedBy(String createdBy);

    /**
     * 统计各状态流程定义数量
     * 
     * @return 状态统计
     */
    @Query("SELECT p.status, COUNT(p) FROM ProcessDefinition p GROUP BY p.status")
    List<Object[]> countByStatus();
}