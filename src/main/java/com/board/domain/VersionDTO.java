package com.board.domain;

import com.board.paging.Criteria;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionDTO extends CommonDTO {
    private String versionName;
    private String insertTime;
    private String currentVersionYn;
    private String appPath;
    private String runYn;
    private String updateDelay;
}
