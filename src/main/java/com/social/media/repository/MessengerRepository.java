package com.social.media.repository;

import com.social.media.model.entity.Messenger;
import com.social.media.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessengerRepository extends JpaRepository<Messenger, Long> {
    Optional<Messenger> findByOwnerAndRecipient(User owner, User recipient);
}
