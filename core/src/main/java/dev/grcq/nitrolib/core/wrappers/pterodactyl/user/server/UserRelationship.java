package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@AllArgsConstructor
public class UserRelationship {

    @Getter private final List<UserAllocation> allocations;

}
