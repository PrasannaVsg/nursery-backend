package com.nursery.repository;

import com.nursery.entity.TimelineEvent;
import com.nursery.entity.TimelineEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TimelineEventRepository extends JpaRepository<TimelineEvent, UUID> {

    List<TimelineEvent> findByBatchIdAndDeletedFalseOrderByDayNumberAscUpdatedAtAsc(UUID batchId);

    /** Sum of dispatched quantity for a batch. remaining = alive - this. */
    @Query("""
            select coalesce(sum(e.qty), 0) from TimelineEvent e
            where e.batchId = :batchId and e.type = :type and e.deleted = false
            """)
    int sumQtyByType(@Param("batchId") UUID batchId, @Param("type") TimelineEventType type);
}
