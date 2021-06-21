package com.board.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupDTO {
    /*단말기 그룹 DTO / 업데이트할 떄 delay 부여 가능*/
    private String groupName;
    private String updateDelay;
}
