package com.individual_s7.post_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user_friends")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFriends {

    @Id
    @Indexed(unique = true)
    private Long userId;
    private List<Long> friendsIds;
}
