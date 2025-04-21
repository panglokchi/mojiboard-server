package com.redfish.moji_server.repositories;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.redfish.moji_server.models.Message;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface MessageRepository extends CrudRepository<Message, Integer> {
    List<Message> findByParent(Message parent);

    @Query("SELECT m FROM Message m WHERE m.parent.id = ?1 ORDER BY time DESC")
    List<Message> findMessagesByParentId(Integer parentId);

    @Query("SELECT m FROM Message m WHERE m.parent.id IS NULL ORDER BY time DESC")
    List<Message> findTopLevelMessages();
}