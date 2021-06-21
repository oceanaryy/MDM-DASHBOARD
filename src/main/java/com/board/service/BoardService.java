package com.board.service;

import java.util.List;

import com.board.domain.BoardDTO;
import com.board.domain.DeviceVersionDTO;
import com.board.domain.VersionDTO;

public interface BoardService {

	public boolean update_chk(BoardDTO params);
	public boolean update_chk_for_api(BoardDTO params);
	public boolean update_notify(BoardDTO params);
	public String uptAble(BoardDTO params);
	public BoardDTO getBoardDetail(Long idx);

	public boolean deleteBoard(Long idx);

	public List<BoardDTO> getDeviceList(BoardDTO params);

	public List<VersionDTO> getVerionList();

	public List<DeviceVersionDTO> getDeviceVerion();
	public boolean uptReserv(VersionDTO params);
	public VersionDTO selectLatestVersion(BoardDTO params);
	public String preUpdateAble(BoardDTO params);
}
