package com.splitpay.app.repository;

import com.splitpay.app.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT G FROM Group G WHERE G.groupId IN (?1)")
    List<Group> findAllByGroupId(List<Long> groupIds);

    Optional<Group> findByGroupCode(String groupCode);
}
