package com.board.mapper;

import java.util.List;

import com.board.domain.DeviceVersionDTO;
import com.board.domain.VersionDTO;
import org.apache.ibatis.annotations.Mapper;

import com.board.domain.BoardDTO;

@Mapper
public interface BoardMapper {

	public int insertBoard(BoardDTO params);

	public BoardDTO selectBoardDetail(Long idx);

	public int update_chk(BoardDTO params);
	public int update_chk_for_api(BoardDTO params);
	public String uptAble(BoardDTO params);
	public int updateBoard(BoardDTO params);

	public int deleteBoard(Long idx);

	public List<BoardDTO> selectDeviceList(BoardDTO params);
	public List<VersionDTO> selectVersion();
	public List<DeviceVersionDTO> selectVersionCount();
	public int selectBoardTotalCount(BoardDTO params);

	public int uptReserv(VersionDTO params);
	public VersionDTO selectLatestVersion(BoardDTO params);
	public boolean updatenotify(BoardDTO params);
	public String preUpdateAble(BoardDTO params);

}
