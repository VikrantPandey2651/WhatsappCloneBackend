package com.vikrant.whatsappclone.user;

import com.vikrant.whatsappclone.chat.Chat;
import com.vikrant.whatsappclone.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@NamedQuery(name=UserConstants.FIND_USER_BY_EMAIL,
        query = "SELECT u FROM User u where u.email = :email")
@NamedQuery(name=UserConstants.FIND_ALL_USERS_EXCEPT_SELF,
        query = "SELECT u FROM User u where u.id != :publicId")
@NamedQuery(name=UserConstants.FIND_USER_BY_PUBLIC_ID,
        query="SELECT u FROM User u where u.id = :publicId")
public class User extends BaseAuditingEntity {

    private static final int LAST_ACTIVE_INTERVAL = 5;

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime lastSeen;

    @OneToMany(mappedBy = "sender")
    private List<Chat> chatsAsSender;

    @OneToMany(mappedBy = "recipient")
    private List<Chat> chatsAsReceiver;

    @Transient
    public boolean isUserOnline(){
        //lastseen = 10:05
        // now 10:09 --> online
        // now 10:12 --> offline

        return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().minusMinutes(LAST_ACTIVE_INTERVAL));

    }


}
