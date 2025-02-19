package com.splitpay.app.repository;

import com.splitpay.app.model.Group;
import com.splitpay.app.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("SELECT G FROM GroupMember G WHERE G.group.groupId = ?1")
    List<GroupMember> findByGroupId(Long groupId);

    @Modifying
    @Query("DELETE FROM GroupMember G WHERE G.group.groupId = ?1 and G.user.userId = ?2")
    void deleteGroupMemberByGroupIdAndUserId(Long groupId, Long userId);

    @Query("SELECT GM FROM GroupMember GM WHERE GM.user.userId = ?1")
    List<GroupMember> findAllByUserId(Long userId);

    @Query("SELECT GM FROM GroupMember GM WHERE GM.group.groupId = ?1 AND GM.user.userId = ?2")
    GroupMember findByGroupIdAndUserId(Long groupId, Long userId);
}
