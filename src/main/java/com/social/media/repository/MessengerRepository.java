package com.social.media.repository;

import com.social.media.model.entity.Messenger;
import com.social.media.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MessengerRepository extends JpaRepository<Messenger, Long> {
    Messenger findByOwnerAndRecipient(User owner, User recipient);
}
